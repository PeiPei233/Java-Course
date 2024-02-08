package zju.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * UserInfoBody is the body of the request sent from the client to the server.
 * It contains the email, the username and the password of the user.
 * <p>
 * The email is the email of the user.
 * Can be null when the request is not registering.
 * <br>
 * The username is the username of the user.
 * <br>
 * The password is the password of the user.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1100L;
    private String email;           // the email of the user
    private String username;        // the username of the user
    private String password;        // the password of the user

    public UserInfoBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
