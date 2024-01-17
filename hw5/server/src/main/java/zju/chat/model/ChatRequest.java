package zju.chat.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 101L;
    private String command;
    private Object payload;
}
