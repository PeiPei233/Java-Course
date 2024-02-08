package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * ChatResponse is the response sent from the server to the client.
 * It contains a command, a status and a payload.
 * <p>
 * The command is a string that specifies the type of the response.
 * <br>
 * The status is a string that specifies the status of the response.
 * <br>
 * The payload is an object that contains the data of the response.
 * <br>
 * The payload can be null.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 102L;
    private String command;         // the type of the response
    private String status;          // the status of the response
    private Object payload;         // the data of the response
}
