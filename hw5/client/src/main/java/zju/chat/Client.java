package zju.chat;

import lombok.Getter;
import lombok.Setter;
import zju.chat.model.ChatRequest;
import zju.chat.model.ChatResponse;
import zju.chat.model.Message;
import zju.chat.model.UserInfoBody;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * Client is a class that handles requests from the client.
 * It is created when a client connects.
 * It is destroyed when the client closes the connection.
 */
@Getter
@Setter
public class Client {

    /**
     * The socket that connects to the client.
     */
    private final Socket socket;
    /**
     * The input and output streams of the socket.
     */
    private final ObjectInputStream in;
    /**
     * The output stream of the socket.
     */
    private final ObjectOutputStream out;
    /**
     * The mapper that maps messages to the database.
     */
    private final Mapper mapper;
    /**
     * The scheduler that periodically updates the database.
     */
    private final ScheduledExecutorService scheduler;
    /**
     * The username of the client.
     */
    private String username;
    /**
     * The chat window of the client.
     */
    private Chat chat;
    /**
     * The cached messages of the client.
     */
    private HashMap<String, Vector<Message>> messages = new HashMap<>();
    /**
     * The timestamp when the database is last updated.
     */
    private long startTimestamp;
    /**
     * The futures of the request.
     */
    private CompletableFuture<ChatResponse> loginFuture;
    private CompletableFuture<ChatResponse> sendFuture;
    private CompletableFuture<ChatResponse> roomMembersFuture;
    private CompletableFuture<ChatResponse> quitRoomFuture;
    private CompletableFuture<ChatResponse> createRoomFuture;
    private CompletableFuture<ChatResponse> joinRoomFuture;
    private CompletableFuture<ChatResponse> checkUserFuture;

    /**
     * Create a client, connect to the server, start the receiver thread, login and start the scheduler.
     *
     * @param username the username
     * @param password the password
     * @param server   the server address
     * @throws Exception if the login fails
     */
    private Client(String username, String password, String server) throws Exception {
        // get server address and port
        String host = server.split(":")[0];
        int port = Integer.parseInt(server.split(":")[1]);
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.username = username;

        // start receiver thread
        startTimestamp = System.currentTimeMillis();
        new Receiver().start();

        // send login
        loginFuture = new CompletableFuture<>();
        send("login", new UserInfoBody(username, password));
        loginFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });

        // get offline messages
        Vector<Message> offlineMessages = null;
        try {
            ChatResponse response = loginFuture.get();
            if (!response.getStatus().equals("success")) {
                throw new Exception("login fail: " + response.getPayload());
            }
            offlineMessages = (Vector<Message>) response.getPayload();
            mapper = new Mapper(username);
            mapper.saveMessages(offlineMessages);
        } catch (Exception e) {
            in.close();
            out.close();
            socket.close();
            throw e;
        }

        // start scheduler to update database every 5 minutes
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                concurrentDatabase();
            } catch (Exception e) {
                System.err.println("Failed to concurrent database: " + e.getMessage());
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    /**
     * Login to the server.
     *
     * @param username the username
     * @param password the password
     * @param server   the server address
     * @throws Exception if the login fails
     */
    public static void login(String username, String password, String server) throws Exception {
        Client client;
        try {
            client = new Client(username, password, server);
        } catch (UnknownHostException e) {
            throw new Exception("Unknown host. Please enter the server address in the format of \"host:port\"");
        } catch (IOException e) {
            throw new Exception("Failed to connect to server");
        } catch (NumberFormatException e) {
            throw new Exception("Invalid port number. Please enter the server address in the format of \"host:port\"");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Invalid server address. Please enter the server address in the format of \"host:port\"");
        }

        // login success and create chat window
        Chat chat = new Chat(client);
        client.setChat(chat);
    }

    /**
     * Register a new user.
     *
     * @param email    the email
     * @param username the username
     * @param password the password
     * @param server   the server address
     * @throws Exception if the registration fails
     */
    public static void register(String email, String username, String password, String server) throws Exception {
        // check email
        if (!email.matches("[a-zA-Z0-9_]+@[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+")) {
            throw new Exception("Invalid email");
        }

        // check username
        //  can only contain letters, numbers, underscores
        if (!username.matches("[a-zA-Z0-9_]+")) {
            throw new Exception("Invalid username. Username can only contain letters, numbers, underscores");
        }
        // username must be between 3 and 20 characters
        if (username.length() < 3 || username.length() > 20) {
            throw new Exception("Invalid username. Username must be between 3 and 20 characters");
        }

        // check password
        //  must be between 6 and 20 characters
        if (password.length() < 6 || password.length() > 20) {
            throw new Exception("Invalid password. Password must be between 6 and 20 characters");
        }

        // check server address
        String host;
        int port;
        try {
            host = server.split(":")[0];
            port = Integer.parseInt(server.split(":")[1]);
        } catch (Exception e) {
            throw new Exception("Invalid server address. Please enter the server address in the format of \"host:port\"");
        }

        // connect to server
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            throw new Exception("Unknown host. Please enter the server address in the format of \"host:port\"");
        } catch (IOException e) {
            throw new Exception("Failed to connect to server");
        }

        // send register request
        try {
            ChatRequest request = new ChatRequest("register", new UserInfoBody(email, username, password));
            out.writeObject(request);
            out.flush();
            ChatResponse response = (ChatResponse) in.readObject();
            if (!response.getStatus().equals("success")) {
                throw new Exception((String) response.getPayload());
            }
        } catch (Exception e) {
            throw new Exception("Failed to register: " + e.getMessage());
        } finally {
            in.close();
            out.close();
            socket.close();
        }
    }

    /**
     * Send a request to the server.
     *
     * @param command the command of the request
     * @param payload the payload of the request
     * @throws Exception if the request fails
     */
    private void send(String command, Object payload) throws Exception {
        ChatRequest request = new ChatRequest(command, payload);
        out.writeObject(request);
        out.flush();
    }

    /**
     * Get the messages of a contact.
     *
     * @param contact the contact
     * @return the messages of the contact
     * @throws Exception if the sql query fails
     */
    public Vector<Message> getMessages(String contact) throws Exception {
        if (messages.containsKey(contact)) {
            return messages.get(contact);
        } else {
            Vector<Message> messages = mapper.getMessages(contact);
            this.messages.put(contact, messages);
            return messages;
        }
    }

    /**
     * Get the contacts of the user.
     *
     * @return the contacts of the user
     */
    public Vector<Message> getContacts() {
        try {
            return mapper.getContacts();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Vector<>();
        }
    }

    /**
     * Send a message to a contact.
     *
     * @param contact the contact
     * @param content the content of the message
     * @param isRoom  whether the contact is a room
     * @throws Exception if the request fails
     */
    public void sendMessage(String contact, String content, boolean isRoom) throws Exception {
        sendFuture = new CompletableFuture<>();
        Message message = new Message(username, contact, content, isRoom, System.currentTimeMillis());
        send("send", message);
        // if the message is sent successfully, add the message to the chat window
        sendFuture.thenAccept(response -> {
            if (response.getStatus().equals("success")) {
                receiveMessage(message);
            } else {
                JOptionPane.showMessageDialog(chat, "Send message fail", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Add a contact.
     *
     * @param contact the contact
     * @param isRoom  whether the contact is a room
     */
    public void addContact(String contact, boolean isRoom) {
        // if the contact has already existed, do nothing
        try {
            mapper.checkContact(contact);
            JOptionPane.showMessageDialog(chat, "Contact already exists", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchElementException ignored) {
        } catch (Exception e) {
            JOptionPane.showMessageDialog(chat, "Add contact fail", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (isRoom) {
            try {
                joinRoom(contact);
                Vector<Message> listModel = new Vector<>();
                messages.put(contact, listModel);
                SwingUtilities.invokeLater(() -> {
                    chat.addMessage(new Message(null, contact, " ", true, System.currentTimeMillis()));
                });
            } catch (NoSuchElementException e) {
                // room not exist, ask user to create a new room
                int result = JOptionPane.showConfirmDialog(chat, "Room not exist, create a new room?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // create a new room
                    try {
                        createRoom(contact);
                        Vector<Message> listModel = new Vector<>();
                        messages.put(contact, listModel);
                        SwingUtilities.invokeLater(() -> {
                            chat.addMessage(new Message(null, contact, " ", true, System.currentTimeMillis()));
                        });
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(chat, "Failed to create room: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(chat, "Failed to join room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // check if the user exists
            try {
                checkUser(contact);
                Vector<Message> listModel = new Vector<>();
                messages.put(contact, listModel);
                SwingUtilities.invokeLater(() -> {
                    chat.addMessage(new Message(null, contact, " ", false, System.currentTimeMillis()));
                });
            } catch (Exception e) {
                JOptionPane.showMessageDialog(chat, "Failed to add contact: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Get the members of a room.
     *
     * @param room the room name
     * @return the members of the room
     * @throws Exception if the room does not exist
     */
    public Vector<String> getRoomMembers(String room) throws Exception {
        roomMembersFuture = new CompletableFuture<>();
        send("getRoomMembers", room);
        roomMembersFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });
        ChatResponse response = roomMembersFuture.get();
        if (!response.getStatus().equals("success")) {
            throw new Exception("Failed to get room members: " + response.getPayload().toString());
        }
        return (Vector<String>) response.getPayload();
    }

    /**
     * Quit a room.
     *
     * @param room the room name
     * @throws Exception if the operation fails
     */
    public void quitRoom(String room) throws Exception {
        quitRoomFuture = new CompletableFuture<>();
        send("quitRoom", room);
        quitRoomFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });
        ChatResponse response = quitRoomFuture.get();
        if (!response.getStatus().equals("success")) {
            throw new Exception("Failed to quit room: " + response.getPayload().toString());
        }
        // remove the room from the chat window
        messages.remove(room);
        // delete the messages of the room from the database
        mapper.deleteRoom(room);
    }

    /**
     * Receive a message.
     *
     * @param message the message
     */
    public void receiveMessage(Message message) {
        System.out.println("Receive message: " + message);
        Vector<Message> listModel = messages.computeIfAbsent(message.getOpposite(username), k -> new Vector<>());
        // insert the message according to timestamp
        int i = listModel.size() - 1;
        while (i >= 0 && listModel.get(i).getTimestamp() > message.getTimestamp()) {
            i--;
        }
        listModel.insertElementAt(message, i + 1);
        SwingUtilities.invokeLater(() -> {
            chat.addMessage(message);
        });
    }

    /**
     * Join a room.
     *
     * @param room the room name
     * @throws Exception if the room does not exist
     */
    private void joinRoom(String room) throws Exception {
        joinRoomFuture = new CompletableFuture<>();
        send("joinRoom", room);
        joinRoomFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });
        ChatResponse response = joinRoomFuture.get();
        if (!response.getStatus().equals("success")) {
            if (response.getPayload().equals("roomNotExist")) {
                throw new NoSuchElementException("Room not exist");
            } else {
                throw new Exception((String) response.getPayload());
            }
        }
    }

    /**
     * Create a room.
     *
     * @param room the room name
     * @throws Exception if the operation fails
     */
    private void createRoom(String room) throws Exception {
        createRoomFuture = new CompletableFuture<>();
        send("createRoom", room);
        createRoomFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });
        ChatResponse response = createRoomFuture.get();
        if (!response.getStatus().equals("success")) {
            throw new Exception((String) response.getPayload());
        }
    }

    /**
     * Check if a user exists.
     *
     * @param username the username
     * @throws Exception if the user does not exist
     */
    private void checkUser(String username) throws Exception {
        checkUserFuture = new CompletableFuture<>();
        send("checkUser", username);
        checkUserFuture.orTimeout(3, TimeUnit.SECONDS).exceptionally(e -> {
            if (e instanceof TimeoutException) {
                throw new RuntimeException("Request Time out");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        });
        ChatResponse response = checkUserFuture.get();
        if (!response.getStatus().equals("success")) {
            throw new Exception((String) response.getPayload());
        }
    }

    /**
     * Concurrent the database.
     *
     * @throws Exception if the sql query fails
     */
    private void concurrentDatabase() throws Exception {
        // get the start and end timestamp of the messages to be concurrent
        long lastTimestamp = startTimestamp;
        long currentTimestamp = System.currentTimeMillis();
        mapper.concurrent(messages, lastTimestamp, currentTimestamp);   // concurrent the database
        startTimestamp = currentTimestamp;
    }

    /**
     * Logout.
     * close the socket, stop the scheduler and concurrent the database.
     */
    public void logout() {
        if (mapper != null) {
            try {
                concurrentDatabase();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(chat, "Failed to concurrent database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
        try {
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(chat, "Failed to close socket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Receiver is a thread that receives responses from the server.
     */
    class Receiver extends Thread {
        @Override
        public void run() {
            while (true) {
                ChatResponse response = null;
                try {
                    response = (ChatResponse) in.readObject();
                } catch (EOFException e) {
                    System.out.println("Client has closed the connection");
                    break;
                } catch (IOException e) {
                    System.err.println("IO error: " + e.getMessage());
                    if (socket.isClosed()) {
                        break;
                    }
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(chat, "Connection reset, please login again.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to read request: " + e.getMessage());
                    continue;
                }

                assert response != null;
                switch (response.getCommand()) {
                    case "message" -> receiveMessage((Message) response.getPayload());
                    case "getRoomMembers" -> roomMembersFuture.complete(response);
                    case "quitRoom" -> quitRoomFuture.complete(response);
                    case "createRoom" -> createRoomFuture.complete(response);
                    case "joinRoom" -> joinRoomFuture.complete(response);
                    case "checkUser" -> checkUserFuture.complete(response);
                    case "send" -> sendFuture.complete(response);
                    case "login" -> loginFuture.complete(response);
                }
            }
            logout();
        }
    }

}
