package zju.chat.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChatResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 102L;
    private String command;
    private String status;
    private Object payload;
}
