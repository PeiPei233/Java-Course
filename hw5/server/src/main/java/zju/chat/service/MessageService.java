package zju.chat.service;

import lombok.Getter;
import zju.chat.Controller;
import zju.chat.Mapper;
import zju.chat.model.Message;

import java.util.Vector;

/**
 * MessageService provides services related to message management.
 * It is a singleton.
 */
public class MessageService {

    /**
     * Singleton
     */
    @Getter
    static MessageService instance = new MessageService();

    /**
     * singleton instances of services and mapper
     */
    private final SessionService sessionService = SessionService.getInstance();
    private final Mapper mapper = Mapper.getInstance();

    /**
     * Send a message to a user.
     *
     * @param username the username of the user to send the message
     * @param message  the message to be sent
     * @throws Exception if the sql query fails
     */
    public void sendMessage(String username, Message message) throws Exception {
        System.out.println("Sending message: " + username + " " + message);
        if (message.isRoom()) {
            Vector<String> roomMembers = mapper.getRoomMembers(message.getOpposite(username));
            for (String member : roomMembers) {
                if (!member.equals(username)) {
                    Controller controller = sessionService.getSession(member);
                    // if the user is offline, save the message to the database
                    if (controller == null) {
                        mapper.saveMessage(member, message);
                        continue;
                    }
                    controller.send("message", "success", message);
                }
            }
        } else {
            String opposite = message.getOpposite(username);
            if (opposite == null) {
                System.err.println("Failed to get opposite");
                return;
            }
            Controller controller = sessionService.getSession(opposite);
            // if the user is offline, save the message to the database
            if (controller == null) {
                mapper.saveMessage(opposite, message);
                return;
            }
            controller.send("message", "success", message);
        }
    }

    /**
     * Get offline messages of a user.
     *
     * @param username the username of the user
     * @return the offline messages of the user
     * @throws Exception if the sql query fails
     */
    public Vector<Message> getOfflineMessages(String username) throws Exception {
        return mapper.getMessages(username);
    }
}
