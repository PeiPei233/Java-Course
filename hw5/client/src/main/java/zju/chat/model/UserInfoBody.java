package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1100L;
    private String email;
    private String username;
    private String password;

    public UserInfoBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
