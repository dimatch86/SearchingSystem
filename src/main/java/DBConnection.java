
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


// Данный класс не используется !

public class DBConnection {

    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3306/kut?useUnicode=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuffer insertQuery = new StringBuffer();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                //connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS field(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "selector VARCHAR(255) NOT NULL, " +
                        "weight FLOAT NOT NULL, " +
                        "PRIMARY KEY(id))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        /*if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("CREATE TABLE field(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "path TEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL, " +
                        "PRIMARY KEY(id), KEY(path(50)))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
        return connection;
    }

    public static void executeInsertIntoTableField(String name, String selector, float weight) throws SQLException {

        String sql = "INSERT INTO field(name, selector, weight) " +
                "VALUES('" + name + "', '" + selector + "', '" + weight + "')";
        DBConnection.getConnection().createStatement().execute(sql);
    }
    public static void executeSingleInsert(String path, int code, String content) throws SQLException {

        String sql = "INSERT INTO page(path, code, content) " +
                "VALUES('" + path + "', '" + code + "', '" + content + "')";
        DBConnection.getConnection().createStatement().execute(sql);
    }

    public static void executeInsert() throws SQLException {

        String sql = "INSERT INTO page(path, code, content) " +
                "VALUES" + insertQuery.toString();
        DBConnection.getConnection().createStatement().execute(sql);
    }
    public synchronized static void createMultiInsert(String path, int code, String content) throws SQLException {

            insertQuery.append((insertQuery.length() == 0 ? "" : ",") +
                    "('" + path + "', '" + code + "', '" + content + "')");

        if (insertQuery.length() > 2000000) {
            executeInsert();
            insertQuery = new StringBuffer();
        }
    }
}