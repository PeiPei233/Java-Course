package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1101L;

    private String from;
    private String to;
    private String content;
    private boolean isRoom;
    private Long timestamp;

    public String getOpposite(String username) {
        // from is null when the message is empty to indicate a new chat
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
