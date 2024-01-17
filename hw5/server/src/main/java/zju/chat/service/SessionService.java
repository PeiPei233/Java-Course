package zju.chat.service;

import lombok.Getter;
import zju.chat.Controller;

import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    @Getter
    static SessionService instance = new SessionService();

    /**
     * username -> controller
     */
    private final ConcurrentHashMap<String, Controller> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String username, Controller controller) {
        sessionMap.put(username, controller);
    }

    public void removeSession(String username) {
        sessionMap.remove(username);
    }

    public Controller getSession(String username) {
        return sessionMap.get(username);
    }

    public boolean hasSession(String username) {
        return sessionMap.containsKey(username);
    }
}
