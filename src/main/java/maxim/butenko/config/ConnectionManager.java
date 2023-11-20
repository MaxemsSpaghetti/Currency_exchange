package maxim.butenko.config;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionManager {

    private static final String DATABASE_NAME = "currency_exchange.db";
    private static String path;


    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            URL dbUrl = ConnectionManager.class.getClassLoader().getResource(DATABASE_NAME);

            try {
                path = new File(Objects.requireNonNull(dbUrl).toURI()).getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            Class.forName("org.sqlite.JDBC");

        } catch (RuntimeException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Connection get() {
        return DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));
    }
}
