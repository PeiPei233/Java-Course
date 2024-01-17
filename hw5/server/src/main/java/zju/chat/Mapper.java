package zju.chat;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.Vector;

public class Mapper {

    @Getter
    private static Mapper instance = new Mapper();

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

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

}
