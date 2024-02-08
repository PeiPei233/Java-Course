package zju.chat;

import zju.chat.model.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.stream.Collectors;

public class Mapper {

    private final String username;
    Connection connection;


    Mapper(String username) throws SQLException {
        String url = "jdbc:sqlite:" + username + ".sqlite";
        connection = DriverManager.getConnection(url);

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
        this.username = username;
    }

    public Vector<Message> getMessages(String contact) throws Exception {
        Vector<Message> messages = new Vector<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE \"from\" = ? OR \"to\" = ? ORDER BY timestamp DESC LIMIT 1000");
        statement.setString(1, contact);
        statement.setString(2, contact);
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
        Collections.reverse(messages);
        return messages;
    }

    public Vector<Message> getContacts() throws Exception {
        Vector<Message> messages = new Vector<>();

        String sql = """
                WITH RankedMessages AS (
                    SELECT *,
                           CASE
                               WHEN isRoom = 1 THEN "to"
                               WHEN "from" = ? THEN "to"
                               ELSE "from"
                           END AS opposite,
                           ROW_NUMBER() OVER (PARTITION BY CASE
                                                             WHEN isRoom = 1 THEN "to"
                                                             WHEN "from" = ? THEN "to"
                                                             ELSE "from"
                                                         END ORDER BY timestamp DESC) as rank
                    FROM messages
                )
                SELECT * FROM RankedMessages
                WHERE rank = 1
                """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, username);
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
        return messages;
    }

    public void checkContact(String contact) throws Exception {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE \"from\" = ? OR \"to\" = ? LIMIT 1");
        statement.setString(1, contact);
        statement.setString(2, contact);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            throw new NoSuchElementException("No contact found");
        }
    }

    public void concurrent(HashMap<String, Vector<Message>> messages, long startTimestamp, long endTimestamp) throws SQLException {
        connection.setAutoCommit(false);
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (\"from\", \"to\", content, isRoom, timestamp) VALUES (?, ?, ?, ?, ?)");
            for (String contact : messages.keySet()) {
                for (Message message : messages.get(contact)) {
                    if (message.getTimestamp() < startTimestamp || message.getTimestamp() > endTimestamp) {
                        continue;
                    }
                    statement.setString(1, message.getFrom());
                    statement.setString(2, message.getTo());
                    statement.setString(3, message.getContent());
                    statement.setLong(4, message.isRoom() ? 1 : 0);
                    statement.setLong(5, message.getTimestamp());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void saveMessages(Vector<Message> messages) throws SQLException {
        connection.setAutoCommit(false);
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (\"from\", \"to\", content, isRoom, timestamp) VALUES (?, ?, ?, ?, ?)");
            for (Message message : messages) {
                statement.setString(1, message.getFrom());
                statement.setString(2, message.getTo());
                statement.setString(3, message.getContent());
                statement.setLong(4, message.isRoom() ? 1 : 0);
                statement.setLong(5, message.getTimestamp());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void deleteRoom(String room) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE \"to\" = ? AND isRoom = 1");
            statement.setString(1, room);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
