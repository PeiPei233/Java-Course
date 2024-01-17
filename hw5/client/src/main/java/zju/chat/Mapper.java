package zju.chat;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Mapper {

    Connection connection;

    Mapper(String username) throws SQLException {
        String url = "jdbc:sqlie:" + username + ".sqlite";
        connection = DriverManager.getConnection(url);

        InputStream is = Mapper.class.getResourceAsStream("/init.sql");
        String initContent = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        String[] initStatements = initContent.split(";");
        for (String sql: initStatements) {
            if (sql.trim().isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

}
