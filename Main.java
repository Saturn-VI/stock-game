public class Main {

    public static final int playerTraderId = 0;

    public static void main(String[] args) {
        Market.initializeMarket();
        initializeTraders();
        DataReader.getStocks();
        for (int i = 0; i < 15; i++) runMarketTests();
        printAllTraders();
    }

    public static void runMarketTests() {
        AbstractTrader playerTrader = Market.getTraderById(0);
        String symbol = "AAPL";
        Stock stock = Market.getStockByTicker(symbol);
        try {
            System.out.println(stock);
            Market.simulateMarketDay();
            Market.buyShares(playerTraderId, 5, symbol);
            //stock.setPrice(stock.getPrice() - 5);
            //Market.buyShares(playerTraderId, 5, symbol);
            //stock.setPrice(stock.getPrice() + 5);
            System.out.println(playerTrader);
        } catch (NotEnoughMoneyException e) {
            System.out.println("Not enough money to buy shares.");
        }
        System.out.println(Market.getListOfStocksForTrader(playerTraderId));
    }

    public static void initializeTraders() {
        Market.addTrader(new PlayerTrader(0, 10000));
        for (int i = 1; i < 20; i++) {
            Market.addTrader(new BotTrader(i));
        }
    }

    public static void printAllTraders() {
        for (int i=0; i<20; i++) {
            System.out.println(Market.getTraderById(i));
        }
    }
}
