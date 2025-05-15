import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static HashMap<Integer, AbstractTrader> traders;
    private static int day = 0;

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        System.out.println(stocks);
    }

    public static Stock getStockByTicker(String ticker) {
        for (Stock stock : getStocks()) {
            if (stock.getSymbol().equals(ticker)) {
                return stock;
            }
        }
        return null;
    }

    /**
     *
     */
    public static void buyShares(int shares, Stock stock, int traderId) throws NotEnoughMoneyException {
        AbstractTrader trader = getTraderById(traderId);
        double price = shares * stock.getPrice();
        Transaction tr = new Transaction(shares, stock, price, false, traderId, day);
        transactions.add(tr);
        trader.changeMoney(-1 * price);
    }

    public static void sellShares(int shares, Stock stock, int traderId) throws NotEnoughtSharesException {
        AbstractTrader trader = getTraderById(traderId);
        double price = sharesOwnedInStock(traderId, stock) * stock.getPrice();
        Transaction tr = new Transaction(shares, stock, price, true, traderId, day);
        transactions.add(tr);
        trader.changeMoney(price);
    }

    public static long sharesOwnedInStock(int traderId, Stock stock) {
        Trader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(traderId);
        long sharesOwned = 0;
        for (Transaction transaction : relevantTransactions) {
            boolean isStock = transaction.stock().equals(stock);
            if (transaction.selling() && isStock) {
                sharesOwned -= transaction.shares();
            } else if (isStock) {
                sharesOwned += transaction.shares();
            }
        }
        return sharesOwned;
    }

    public static double traderMoneyAmount(int traderId) {
        Trader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevant = getTransactionsForTrader(
            traderId
        );
        double money = trader.initialMoney();
        for (Transaction t : relevantTransactions) {
            if (t.selling()) {
                money += t.shares() * t.marketPriceAtTimeOfTransaction();
            } else {
                money -= t.shares() * t.marketPriceAtTimeOfTransaction();
            }
        }
        return money;
    }

    public static ArrayList<Transaction> getTransactionsForTrader(int traderId) {
        ArrayList<Transaction> out = new ArrayList<Transaction>();
        for (Transaction t : transactions) {
            if (t.traderId() == traderId) {
                out.add(t);
            }
        }
        return out;
    }

    public static Trader getTraderById(int traderId) {
        return traders.get(traderId);
    }

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }
}
