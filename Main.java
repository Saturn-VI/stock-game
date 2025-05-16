import javax.swing.SwingUtilities;

public class Main {

    public static final int playerTraderId = 0;

    public static void main(String[] args) {
        Market.initializeMarket();
        DataReader.getStocks();
        runMarketTests();
        SwingUtilities.invokeLater(() -> new GameWindow());
    }

    public static void runMarketTests() {
        AbstractTrader playerTrader = new PlayerTrader(playerTraderId);
        Market.addTrader(playerTrader);
        Stock stock = Market.getStockByName("TSLA");
        try {
            Market.buyShares(playerTraderId, 5, "TSLA");
            stock.setPrice(stock.getPrice() - 5);
            Market.buyShares(playerTraderId, 5, "TSLA");
            stock.setPrice(stock.getPrice() + 5);
            stock.setPrice(stock.getPrice() - 2.5);
        } catch (NotEnoughMoneyException e) {
            System.out.println("Not enough money to buy shares.");
        }
        System.out.println(Market.getListOfStocksForTrader(playerTraderId));
    }
}
