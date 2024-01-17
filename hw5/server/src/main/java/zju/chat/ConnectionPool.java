package zju.chat;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    @Getter
    private static final ConnectionPool instance = new ConnectionPool();

    private final LinkedBlockingQueue<Connection> pool = new LinkedBlockingQueue<>();

    public ConnectionPool() {
        try {
            for (int i = 0; i < 10; i++) {
                Connection connection = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
                pool.offer(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void releaseConnection(Connection connection) {
        pool.offer(connection);
    }

}
