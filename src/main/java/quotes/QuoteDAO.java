package quotes;

import DB.ConnectionFactory;

import java.sql.*;

public class QuoteDAO {

    public static String getTorettoRules() {
        StringBuilder result = new StringBuilder("<b>Принципы Доминика Торетто</b>\n");

        try (Connection conn = ConnectionFactory.getConnection() ;
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT text FROM toretto");
            while (rs.next()) {
                result.append("- ");
                result.append(rs.getString("text"));
                result.append("\u261D");
                result.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String getRandomQuoteFromDb() {

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM pacanes ORDER BY RANDOM() LIMIT 1");
            if (rs.next()) {
                return rs.getString("text");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "oops!";
    }
}