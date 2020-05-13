package quotes;

import java.sql.*;

public class DB {

    final static String URL = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":5432/" + System.getenv("DB_NAME");
    final static String USER = System.getenv("DB_USER");
    final static String PASS = System.getenv("DB_PASS");

    public static String getTorettoRules() {
        StringBuilder result = new StringBuilder("<h3>Принципы Доминика Торетто</h3>\n");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT text FROM toretto");
            while (rs.next()) {
                result.append("- ");
                result.append(rs.getString("text"));
                result.append("\u261D");
                result.append("\n");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result.toString();
    }
}