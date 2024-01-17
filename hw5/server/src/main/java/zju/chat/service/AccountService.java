package zju.chat.service;

import lombok.Getter;
import zju.chat.Mapper;

import java.util.NoSuchElementException;
import java.util.Vector;

public class AccountService {

    @Getter
    static AccountService instance = new AccountService();

    private final Mapper mapper = Mapper.getInstance();

    public void validateUser(String username, String password) throws Exception {
        mapper.validateUser(username, password);
    }

    public void register(String email, String username, String password) throws Exception {
        mapper.register(email, username, password);
    }

    public Vector<String> getRoomMembers(String room) throws Exception {
        return mapper.getRoomMembers(room);
    }

    public void quitRoom(String username, String room) throws Exception {
        mapper.quitRoom(username, room);
    }

    public void joinRoom(String username, String room) throws Exception {
        try {
            mapper.checkRoom(room);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("roomNotExist");
        }
        mapper.joinRoom(username, room);
    }

    public void createRoom(String username, String room) throws Exception {
        mapper.joinRoom(username, room);
    }

    public void checkUser(String username) throws Exception {
        mapper.checkUser(username);
    }


}
