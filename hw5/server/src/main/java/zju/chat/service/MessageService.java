package zju.chat.service;

import lombok.Getter;
import zju.chat.Controller;
import zju.chat.Mapper;
import zju.chat.model.Message;

import java.util.Vector;

public class MessageService {

    @Getter
    static MessageService instance = new MessageService();

    private final SessionService sessionService = SessionService.getInstance();
    private final Mapper mapper = Mapper.getInstance();

    public void sendMessage(String username, Message message) throws Exception {
        System.out.println("Sending message: " + username + " " + message);
        if (message.isRoom()) {
            Vector<String> roomMembers = mapper.getRoomMembers(message.getOpposite(username));
            for (String member: roomMembers) {
                if (!member.equals(username)) {
                    Controller controller = sessionService.getSession(member);
                    if (controller == null) {
                        System.err.println("Failed to get controller");
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
            if (controller == null) {
                System.err.println("Failed to get controller");
                return;
            }
            controller.send("message", "success", message);
        }
    }

}
