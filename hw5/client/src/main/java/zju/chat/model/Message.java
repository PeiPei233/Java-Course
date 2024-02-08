package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Message is the message sent between clients.
 * It contains the sender `from`, the receiver `to`, the content, the room signal and the timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1101L;

    private String from;            // the sender
    private String to;              // the receiver
    private String content;         // the content
    private boolean isRoom;         // whether the message is sent to a room
    private Long timestamp;         // the timestamp

    /**
     * Get the opposite of the user.
     *
     * @param username the username of the user
     * @return the opposite of the user
     */
    public String getOpposite(String username) {
        // from is null when the message is empty to indicate a new chat
        // only happens in the client
        if (from == null) {
            return to;
        }
        if (isRoom) {
            return to;
        } else if (from.equals(username)) {
            return to;
        } else if (to.equals(username)) {
            return from;
        } else {
            return null;
        }
    }
}
