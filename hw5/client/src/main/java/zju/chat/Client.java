package zju.chat;

import lombok.Getter;
import lombok.Setter;
import zju.chat.model.ChatRequest;
import zju.chat.model.ChatResponse;
import zju.chat.model.UserInfoBody;
import zju.chat.model.Message;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Getter
@Setter
public class Client {

    private String username;
    private Chat chat;
    private HashMap<String, Vector<Message>> messages = new HashMap<>();
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private CompletableFuture<ChatResponse> loginFuture;
    private CompletableFuture<ChatResponse> sendFuture;
    private CompletableFuture<ChatResponse> roomMembersFuture;
    private CompletableFuture<ChatResponse> quitRoomFuture;
    private CompletableFuture<ChatResponse> createRoomFuture;
    private CompletableFuture<ChatResponse> joinRoomFuture;
    private CompletableFuture<ChatResponse> checkUserFuture;

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
                if (response.getCommand().equals("message")) {
                    receiveMessage((Message) response.getPayload());
                } else if (response.getCommand().equals("getRoomMembers")) {
                    roomMembersFuture.complete(response);
                } else if (response.getCommand().equals("quitRoom")) {
                    quitRoomFuture.complete(response);
                } else if (response.getCommand().equals("createRoom")) {
                    createRoomFuture.complete(response);
                } else if (response.getCommand().equals("joinRoom")) {
                    joinRoomFuture.complete(response);
                } else if (response.getCommand().equals("checkUser")) {
                    checkUserFuture.complete(response);
                } else if (response.getCommand().equals("send")) {
                    sendFuture.complete(response);
                } else if (response.getCommand().equals("login")) {
                    loginFuture.complete(response);
                }
            }
        }
    }

    private Client(String username, String password, String server) throws Exception {
        String host = server.split(":")[0];
        int port = Integer.parseInt(server.split(":")[1]);
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.username = username;
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

        try {
            ChatResponse response = loginFuture.get();
            if (!response.getStatus().equals("success")) {
                throw new Exception("login fail: " + response.getPayload());
            }
        } catch (Exception e) {
            in.close();
            out.close();
            socket.close();
            throw e;
        }
    }

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

        Chat chat = new Chat(client);
        client.setChat(chat);
    }

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

        try {
            ChatRequest request = new ChatRequest("register", new UserInfoBody(email, username, password));
            out.writeObject(request);
            out.flush();
            ChatResponse response = (ChatResponse) in.readObject();
            if (!response.getStatus().equals("success")) {
                throw new Exception((String) response.getPayload());
            } else {
            }
        } catch (Exception e) {
            throw new Exception("Failed to register: " + e.getMessage());
        } finally {
            in.close();
            out.close();
            socket.close();
        }
    }

    private void send(String command, Object payload) throws Exception {
        ChatRequest request = new ChatRequest(command, payload);
        out.writeObject(request);
        out.flush();
    }

    public Vector<Message> getMessages(String contact) {
        return messages.get(contact);
    }

    public Vector<Message> getContacts() {
        Vector<Message> contacts = new Vector<>();
        for (String contact : messages.keySet()) {
            contacts.add(messages.get(contact).lastElement());
        }
        // sort by descending order of millis timestamp
        contacts.sort((o1, o2) -> (int) (o2.getTimestamp() - o1.getTimestamp()));
        return contacts;
    }

    public void sendMessage(String contact, String content, boolean isRoom) throws Exception {
        sendFuture = new CompletableFuture<>();
        Message message = new Message(username, contact, content, isRoom, System.currentTimeMillis());
        send("send", message);
        sendFuture.thenAccept(response -> {
            if (response.getStatus().equals("success")) {
                receiveMessage(message);
            } else {
                JOptionPane.showMessageDialog(chat, "Send message fail", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void addContact(String contact, boolean isRoom) {
        // if the contact has already existed, do nothing
        if (messages.containsKey(contact)) {
            return;
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
            // check if the user is himself
//            if (contact.equals(this.username)) {
//                JOptionPane.showMessageDialog(chat, "Cannot add contact to yourself", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }

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

    public void logout() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
    }

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

}
