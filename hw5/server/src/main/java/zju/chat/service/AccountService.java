package zju.chat.service;

import lombok.Getter;
import zju.chat.Mapper;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * AccountService provides services related to account management.
 * It is a singleton.
 */
public class AccountService {

    /**
     * Singleton
     */
    @Getter
    static AccountService instance = new AccountService();

    /**
     * Mapper
     */
    private final Mapper mapper = Mapper.getInstance();

    /**
     * validate user login
     *
     * @param username username
     * @param password password
     * @throws Exception if the username or password is incorrect
     */
    public void validateUser(String username, String password) throws Exception {
        mapper.validateUser(username, password);
    }

    /**
     * register a new user
     *
     * @param email    email
     * @param username username
     * @param password password
     * @throws Exception if the username or email is already used
     */
    public void register(String email, String username, String password) throws Exception {
        mapper.register(email, username, password);
    }

    /**
     * get the members of a room
     *
     * @param room room
     * @return the members of the room
     * @throws Exception if the room does not exist
     */
    public Vector<String> getRoomMembers(String room) throws Exception {
        return mapper.getRoomMembers(room);
    }

    /**
     * Quit a room
     *
     * @param username the username of the user to quit the room
     * @param room     the room name
     * @throws Exception if the operation fails
     */
    public void quitRoom(String username, String room) throws Exception {
        mapper.quitRoom(username, room);
    }

    /**
     * Join a room
     *
     * @param username the username of the user to join the room
     * @param room     the room name
     * @throws Exception if the room does not exist
     */
    public void joinRoom(String username, String room) throws Exception {
        try {
            mapper.checkRoom(room);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("roomNotExist");
        }
        mapper.joinRoom(username, room);
    }

    /**
     * Create a room
     *
     * @param username the username of the user to create the room
     * @param room     the room name
     * @throws Exception if the operation fails
     */
    public void createRoom(String username, String room) throws Exception {
        mapper.joinRoom(username, room);
    }

    /**
     * Check if a user exists
     *
     * @param username the username
     * @throws Exception if the user does not exist
     */
    public void checkUser(String username) throws Exception {
        mapper.checkUser(username);
    }


}
