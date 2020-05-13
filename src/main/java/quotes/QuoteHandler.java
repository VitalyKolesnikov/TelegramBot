package quotes;

public class QuoteHandler {
    public static String getRandomQuote() {
        return Math.random() > 0.1 ? QuoteDAO.getRandomQuoteFromDb() : Forismatic.getRandomQuote();
    }
}