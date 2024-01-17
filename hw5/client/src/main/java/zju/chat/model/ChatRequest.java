package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 101L;
    private String command;
    private Object payload;
}
