package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 102L;
    private String command;
    private String status;
    private Object payload;
}
