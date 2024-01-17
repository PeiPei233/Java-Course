package zju.chat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final int SERVER_PORT;

    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASSWORD;
    public static final int DB_POOL_SIZE;

    static {
        String serverPort = System.getenv("SERVER_PORT");
        int port = 8080;
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbPoolSize = System.getenv("DB_POOL_SIZE");
        int poolSize = 10;

        Properties properties = new Properties();
        try {
            properties.load(Main.class.getResourceAsStream("/application.properties"));
            if (serverPort == null) {
                serverPort = properties.getProperty("server.port");
            }
            if (serverPort == null) {
                serverPort = "8080";
            }
            if (dbUrl == null) {
                dbUrl = properties.getProperty("db.url");
            }
            if (dbUser == null) {
                dbUser = properties.getProperty("db.user");
            }
            if (dbPassword == null) {
                dbPassword = properties.getProperty("db.password");
            }
            if (dbPoolSize == null) {
                dbPoolSize = properties.getProperty("db.poolSize");
            }

            try {
                port = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse the port, use 8080 instead.");
            }
            try {
                poolSize = Integer.parseInt(dbPoolSize);
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse the pool size, use 10 instead.");
            }
        } catch (Exception e) {
            System.err.println("Failed to load application.properties");
            System.err.println("Using default values");
        }
        SERVER_PORT = port;
        DB_URL = dbUrl;
        DB_USER = dbUser;
        DB_PASSWORD = dbPassword;
        DB_POOL_SIZE = poolSize;

        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            System.err.println("Failed to load database configuration");
            System.exit(1);
        }
    }

}
