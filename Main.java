public class Main {

    public static final int playerTraderId = 0;

    public static void main(String[] args) {
        Market.initializeMarket();
        initializeTraders();
        DataReader.getStocks();
        // printAllTraders();

        // buying automatically instantiates GameWindow
        buyRandomStartingStocks();
        Market.simulateMarketDay();
    }

    public static void buyRandomStartingStocks() {
        for (Stock stock : Market.getStocks()) {
            if (Math.random() < 1 - (3 * (1.0 / Market.getStocks().size()))) {
                continue;
            }
            int sharesToBuy = (int) (Math.random() * 5) + 2;
            try {
                Market.buyShares(playerTraderId, sharesToBuy, stock.getSymbol());
            } catch (NotEnoughMoneyException e) {
                System.out.println("Not enough money to buy shares.");
            }
        }
        if (Market.getListOfStocksForTrader(playerTraderId).size() < 4) {
            buyRandomStartingStocks(); // just make sure the player starts with a bunch of stocks
        }
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
