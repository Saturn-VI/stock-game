import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<Trader> traders;

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        // System.out.println(stocks);
        transactions = new ArrayList<Transaction>();
        traders = new ArrayList<Trader>();
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
    public static void buyShares(int traderId, int stockAmount)
        throws NotEnoughMoneyException {}

    public static ArrayList<String> getListOfStocksForTrader(int traderId) {
        ArrayList<String> out = new ArrayList<String>();
        for (Transaction transaction : transactions) {
            if (transaction.traderId() != traderId) {
                continue;
            }
            if (out.contains(transaction.stock().getSymbol())) {
                continue;
            }
            out.add(transaction.stock().getSymbol());
        }
        return out;
    }

    public static long sharesOwnedInStock(int traderId, Stock stock) {
        Trader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId
        );
        long sharesOwned = 0;
        for (Transaction transaction : relevantTransactions) {
            if (!transaction.stock().equals(stock)) {
                continue;
            }
            if (transaction.selling()) {
                sharesOwned -= transaction.shares();
            } else {
                sharesOwned += transaction.shares();
            }
        }
        return sharesOwned;
    }

    public static double getTraderMoneyAmount(int traderId) {
        Trader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
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

    public static ArrayList<Transaction> getTransactionsForTrader(
        int traderId
    ) {
        ArrayList<Transaction> out = new ArrayList<Transaction>();
        for (Transaction t : transactions) {
            if (t.traderId() == traderId) {
                out.add(t);
            }
        }
        return out;
    }

    public static Trader getTraderById(int traderId) {
        for (Trader t : traders) {
            if (t.traderId() == traderId) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }
}
