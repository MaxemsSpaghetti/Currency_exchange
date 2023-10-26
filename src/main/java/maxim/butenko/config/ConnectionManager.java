package maxim.butenko.config;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public final class ConnectionManager {

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager() {

    }
    @SneakyThrows
    public static Connection get() {
        return DriverManager.getConnection("jdbc:sqlite:C:/Users/Jew/SQLite/currency_exchange.db");
    }
}
