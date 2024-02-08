package zju.chat;

import java.util.Properties;

/**
 * This class is used to load configuration from environment variables and application.properties.
 * <p>
 * The environment variables are:
 * <ul>
 *     <li>SERVER_PORT</li>
 *     <li>DB_URL</li>
 *     <li>DB_USER</li>
 *     <li>DB_PASSWORD</li>
 *     <li>DB_POOL_SIZE</li>
 * </ul>
 * The application.properties is located at src/main/resources/application.properties.
 * <br>
 * The properties are:
 * <ul>
 *     <li>server.port</li>
 *     <li>db.url</li>
 *     <li>db.user</li>
 *     <li>db.password</li>
 *     <li>db.poolSize</li>
 * </ul>
 * The environment variables have higher priority than the properties.
 * <br>
 * If the environment variables are not set, the properties will be used.
 * <br>
 * If the properties are not set, the default values will be used.
 * <br>
 * If the database configuration is not set, the program will exit.
 * <br>
 * The default values are:
 * <ul>
 *     <li>server.port: 8080</li>
 *     <li>db.poolSize: 10</li>
 * </ul>
 */
public class Config {

    /**
     * The port of the server.
     */
    public static final int SERVER_PORT;

    /**
     * The url of the database.
     */
    public static final String DB_URL;

    /**
     * The username of the database.
     */
    public static final String DB_USER;

    /**
     * The password of the database.
     */
    public static final String DB_PASSWORD;

    /**
     * The size of the database connection pool.
     */
    public static final int DB_POOL_SIZE;

    static {

        // try to load from environment variables
        String serverPort = System.getenv("SERVER_PORT");
        int port = 8080;
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbPoolSize = System.getenv("DB_POOL_SIZE");
        int poolSize = 10;

        // try to load from application.properties
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

        // check database configuration
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            System.err.println("Failed to load database configuration");
            System.exit(1);
        }
    }

}
