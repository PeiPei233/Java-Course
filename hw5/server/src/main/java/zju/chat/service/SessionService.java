package zju.chat.service;

import lombok.Getter;
import zju.chat.Controller;

import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionService provides services related to session management.
 * It is a singleton.
 * A session is a mapping from username to controller.
 * A session is created when a user logs in.
 * A session is destroyed when a user logs out.
 */
public class SessionService {

    /**
     * Singleton
     */
    @Getter
    static SessionService instance = new SessionService();

    /**
     * username -> controller
     */
    private final ConcurrentHashMap<String, Controller> sessionMap = new ConcurrentHashMap<>();

    /**
     * Add a session.
     *
     * @param username   the username of the user
     * @param controller the controller of the user
     */
    public void addSession(String username, Controller controller) {
        sessionMap.put(username, controller);
    }

    /**
     * Remove a session.
     *
     * @param username the username of the user
     */
    public void removeSession(String username) {
        sessionMap.remove(username);
    }

    /**
     * Get a session.
     *
     * @param username the username of the user
     * @return the controller of the user
     */
    public Controller getSession(String username) {
        return sessionMap.get(username);
    }

    /**
     * Check if a user has a session.
     *
     * @param username the username of the user
     * @return true if the user has a session, false otherwise
     */
    public boolean hasSession(String username) {
        return sessionMap.containsKey(username);
    }
}
