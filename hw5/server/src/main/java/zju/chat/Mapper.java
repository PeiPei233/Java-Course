package zju.chat;

import lombok.Getter;
import zju.chat.model.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Mapper for database
 * Singleton
 */
public class Mapper {

    /**
     * Singleton
     */
    @Getter
    private static final Mapper instance = new Mapper();

    /**
     * Connection pool
     */
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Constructor
     * Initialize connection pool
     * Initialize database tables if not exist
     */
    private Mapper() {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            // get initialization sql statements from init.sql
            InputStream is = Mapper.class.getResourceAsStream("/init.sql");
            assert is != null;
            String initContent = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            String[] initStatements = initContent.split(";");
            for (String sql : initStatements) {
                if (sql.trim().isEmpty()) {
                    continue;
                }
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Validate user by username and password
     *
     * @param username the username
     * @param password the password
     * @throws Exception if the username or the password is incorrect
     */
    public void validateUser(String username, String password) throws Exception {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            if (!statement.executeQuery().next()) {
                throw new Exception("Username or password incorrect");
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Register a user
     *
     * @param email    the email
     * @param username the username
     * @param password the password
     * @throws Exception if the username or the email already exists
     */
    public void register(String email, String username, String password) throws Exception {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            // check if username exists
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                throw new Exception("Username already exists");
            }

            // check if email exists
            statement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
            statement.setString(1, email);
            if (statement.executeQuery().next()) {
                throw new Exception("Email already exists");
            }

            // check if a room with the same name exists
            statement = connection.prepareStatement("SELECT * FROM room WHERE name = ?");
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                throw new Exception("Username already exists");
            }

            // insert user
            try {
                statement = connection.prepareStatement("INSERT INTO user (email, username, password) VALUES (?, ?, ?)");
                statement.setString(1, email);
                statement.setString(2, username);
                statement.setString(3, password);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Register user fail: " + e.getMessage());
            }

        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Get the members of a room
     *
     * @param room the room name
     * @return the members of the room
     * @throws Exception if the operation fails
     */
    public Vector<String> getRoomMembers(String room) throws Exception {
        Connection connection = null;
        Vector<String> roomMembers = new Vector<>();
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT member FROM room WHERE name = ?");
            statement.setString(1, room);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                roomMembers.add(resultSet.getString("member"));
            }
            if (roomMembers.size() == 0) {
                throw new Exception("Room not exist");
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
        return roomMembers;
    }

    /**
     * Quit a room
     *
     * @param username the username of the user to quit the room
     * @param room     the room name
     * @throws Exception if the operation fails
     */
    public void quitRoom(String username, String room) throws Exception {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM room WHERE member = ?");
                statement.setString(1, username);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Quit room fail: " + e.getMessage());
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Join a room
     *
     * @param username the username of the user to join the room
     * @param room     the room name
     * @throws Exception if the operation fails
     */
    public void joinRoom(String username, String room) throws Exception {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            // check if already in the room
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM room WHERE name = ? AND member = ?");
            statement.setString(1, room);
            statement.setString(2, username);
            if (statement.executeQuery().next()) {
                return;
            }

            try {
                statement = connection.prepareStatement("INSERT INTO room (name, member) VALUES (?, ?)");
                statement.setString(1, room);
                statement.setString(2, username);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Quit room fail: " + e.getMessage());
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Check if a room exists
     *
     * @param room the room name
     * @throws Exception if the room does not exist
     */
    public void checkRoom(String room) throws Exception {
        Connection connection = null;
        Vector<String> roomMembers = new Vector<>();
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM room WHERE name = ?");
            statement.setString(1, room);
            if (!statement.executeQuery().next()) {
                throw new NoSuchElementException("roomNotExist");
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Check if a user exists
     *
     * @param username the username
     * @throws Exception if the user does not exist
     */
    public void checkUser(String username) throws Exception {
        Connection connection = null;
        Vector<String> roomMembers = new Vector<>();
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
            statement.setString(1, username);
            if (!statement.executeQuery().next()) {
                throw new NoSuchElementException("userNotExist");
            }
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Save the offline message
     *
     * @param username the username of the user to save the message
     * @param message  the message to be saved
     */
    public void saveMessage(String username, Message message) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO save_messages (`from`, `to`, content, isRoom, timestamp, username) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, message.getFrom());
            statement.setString(2, message.getTo());
            statement.setString(3, message.getContent());
            statement.setLong(4, message.isRoom() ? 1 : 0);
            statement.setLong(5, message.getTimestamp());
            statement.setString(6, username);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    /**
     * Get the offline messages of a user
     *
     * @param username the username of the user to get the messages
     * @return the offline messages of the user
     * @throws Exception if the operation fails
     */
    public Vector<Message> getMessages(String username) throws Exception {
        Connection connection = null;
        Vector<Message> messages = new Vector<>();
        try {
            connection = connectionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM save_messages WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();
                message.setFrom(resultSet.getString("from"));
                message.setTo(resultSet.getString("to"));
                message.setContent(resultSet.getString("content"));
                message.setRoom(resultSet.getLong("isRoom") != 0);
                message.setTimestamp(resultSet.getLong("timestamp"));
                messages.add(message);
            }

            // delete messages
            statement = connection.prepareStatement("DELETE FROM save_messages WHERE username = ?");
            statement.setString(1, username);
            statement.executeUpdate();
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
        return messages;
    }
}
