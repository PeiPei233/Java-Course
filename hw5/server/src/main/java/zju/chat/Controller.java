package zju.chat;

import zju.chat.model.ChatRequest;
import zju.chat.model.ChatResponse;
import zju.chat.model.Message;
import zju.chat.model.UserInfoBody;
import zju.chat.service.AccountService;
import zju.chat.service.MessageService;
import zju.chat.service.SessionService;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Controller is a thread that handles requests from a client.
 * It is created by {@link zju.chat.Main} when a client connects.
 * It is destroyed when the client closes the connection.
 */
public class Controller extends Thread {

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
     * The singleton instances of services.
     */
    private final SessionService sessionService = SessionService.getInstance();
    private final MessageService messageService = MessageService.getInstance();
    private final AccountService accountService = AccountService.getInstance();

    /**
     * The username of the client.
     */
    private String username;


    /**
     * Create a new controller.
     *
     * @param socket the socket that connects to the client
     * @throws IOException if an I/O error occurs when creating the input or output stream
     */
    Controller(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * The main loop of the controller.
     * It reads requests from the client and sends responses.
     * It also handles exceptions.
     * If the client closes the connection, the loop breaks.
     * <p>
     * The request is an instance of {@link ChatRequest}.
     * <br>
     * The response is an instance of {@link ChatResponse}.
     * <br>
     * The response contains the command, the status and the payload.
     * <br>
     * The command is the same as the request.
     * <br>
     * The status is either "success" or "fail".
     * <br>
     * The payload is the result of the request.
     * </p>
     */
    @Override
    public void run() {
        while (true) {
            try {
                // read request
                ChatRequest request = (ChatRequest) in.readObject();
                System.out.println("Received request: " + request);
                try {
                    Object payload = null;
                    switch (request.getCommand()) {
                        // login and get offline messages
                        case "login": {
                            UserInfoBody body = (UserInfoBody) request.getPayload();
                            payload = login(body);
                            break;
                        }
                        // register a new user
                        case "register": {
                            UserInfoBody body = (UserInfoBody) request.getPayload();
                            accountService.register(body.getEmail(), body.getUsername(), body.getPassword());
                            break;
                        }
                        // send a message
                        case "send":
                            Message message = (Message) request.getPayload();
                            messageService.sendMessage(username, message);
                            break;
                        // get room members
                        case "getRoomMembers": {
                            String room = (String) request.getPayload();
                            payload = accountService.getRoomMembers(room);
                            break;
                        }
                        // quit a room
                        case "quitRoom": {
                            String room = (String) request.getPayload();
                            accountService.quitRoom(username, room);
                            break;
                        }
                        // join a room
                        case "joinRoom": {
                            String room = (String) request.getPayload();
                            accountService.joinRoom(username, room);
                            break;
                        }
                        // create a room
                        case "createRoom": {
                            String room = (String) request.getPayload();
                            accountService.createRoom(username, room);
                            break;
                        }
                        // check if a user exists
                        case "checkUser": {
                            String username = (String) request.getPayload();
                            accountService.checkUser(username);
                            break;
                        }
                        default:
                            throw new NoSuchMethodException("Command not found: " + request.getCommand());
                    }
                    // send response
                    send(request.getCommand(), "success", payload);
                } catch (Exception e) {
                    // send error message
                    send(request.getCommand(), "fail", e.getMessage());
                }
            } catch (EOFException e) {
                System.out.println("Client has closed the connection");
                break;
            } catch (IOException e) {
                System.err.println("IO error: " + e.getMessage());
                try {
                    socket.close();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Failed to read request: " + e.getMessage());
            }
        }

        try {
            in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (username != null) sessionService.removeSession(username);
        } catch (IOException e) {
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }

    /**
     * Send a response to the client.
     *
     * @param command the command of the response
     * @param status  the status of the response
     * @param payload the payload of the response
     */
    public void send(String command, String status, Object payload) {
        ChatResponse response = new ChatResponse(command, status, payload);
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send response: " + e.getMessage());
        }
        System.out.println("Sent response: " + response);
    }

    /**
     * Login a user.
     *
     * @param body the body of the request containing the username and the password
     * @return the offline messages of the user
     * @throws Exception if the user is already logged in or the username and the password are incorrect
     */
    private Vector<Message> login(UserInfoBody body) throws Exception {
        if (username != null || sessionService.hasSession(body.getUsername())) {
            throw new Exception("Already logged in");
        }
        accountService.validateUser(body.getUsername(), body.getPassword());
        username = body.getUsername();
        sessionService.addSession(body.getUsername(), this);

        // get offline messages
        return messageService.getOfflineMessages(username);
    }

}
