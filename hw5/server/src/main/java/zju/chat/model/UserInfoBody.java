package zju.chat.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserInfoBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1100L;
    private String email;
    private String username;
    private String password;
}
