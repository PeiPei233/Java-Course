package zju.chat.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ChatRequest is the request sent from the client to the server.
 * It contains a command and a payload.
 * <p>
 * The command is a string that specifies the type of the request.
 * <br>
 * The payload is an object that contains the data of the request.
 * <br>
 * The payload can be null.
 * </p>
 */
@Data
public class ChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 101L;
    private String command;     // the type of the request
    private Object payload;     // the data of the request
}
