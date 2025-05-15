import javax.swing.SwingUtilities;

public class Main {
    public static final int playerTraderId = 0;

    public static void main(String[] args) {
        Market.initializeMarket();
        DataReader.getStocks();
        SwingUtilities.invokeLater(() -> new GameWindow());
    }
}
