package zju.chat;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Connection pool
 */
public class ConnectionPool {

    /**
     * Singleton
     */
    @Getter
    private static final ConnectionPool instance = new ConnectionPool();

    /**
     * Connection pool
     */
    private final LinkedBlockingQueue<Connection> pool = new LinkedBlockingQueue<>();

    /**
     * Constructor
     * Initialize connection pool
     */
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

    /**
     * Get a connection from the pool
     *
     * @return a connection
     * @throws InterruptedException if the thread is interrupted
     */
    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    /**
     * Release a connection to the pool
     *
     * @param connection the connection to be released
     */
    public void releaseConnection(Connection connection) {
        pool.offer(connection);
    }

}
