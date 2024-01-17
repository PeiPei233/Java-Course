package zju.chat;

import zju.chat.model.ChatRequest;
import zju.chat.model.ChatResponse;
import zju.chat.model.UserInfoBody;
import zju.chat.model.Message;
import zju.chat.service.AccountService;
import zju.chat.service.MessageService;
import zju.chat.service.SessionService;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class Controller extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final SessionService sessionService = SessionService.getInstance();
    private final MessageService messageService = MessageService.getInstance();
    private final AccountService accountService = AccountService.getInstance();
    private String username;


    Controller(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                ChatRequest request = (ChatRequest) in.readObject();
                System.out.println("Received request: " + request);
                try {
                    Object payload = null;
                    switch (request.getCommand()) {
                        case "login": {
                            UserInfoBody body = (UserInfoBody) request.getPayload();
                            payload = login(body);
                            break;
                        }
                        case "register": {
                            UserInfoBody body = (UserInfoBody) request.getPayload();
                            accountService.register(body.getEmail(), body.getUsername(), body.getPassword());
                            break;
                        }
                        case "send":
                            Message message = (Message) request.getPayload();
                            messageService.sendMessage(username, message);
                            payload = null;
                            break;
                        case "logout":
                            break;
                        case "getRoomMembers": {
                            String room = (String) request.getPayload();
                            payload = accountService.getRoomMembers(room);
                            break;
                        }
                        case "quitRoom": {
                            String room = (String) request.getPayload();
                            accountService.quitRoom(username, room);
                            break;
                        }
                        case "joinRoom": {
                            String room = (String) request.getPayload();
                            accountService.joinRoom(username, room);
                            break;
                        }
                        case "createRoom": {
                            String room = (String) request.getPayload();
                            accountService.createRoom(username, room);
                            break;
                        }
                        case "checkUser": {
                            String username = (String) request.getPayload();
                            accountService.checkUser(username);
                            break;
                        }
                        default:
                            break;
                    }
                    send(request.getCommand(), "success", payload);
                } catch (Exception e) {
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
