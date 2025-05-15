import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<AbstractTrader> traders;
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
        Transaction tr = new Transaction(shares, stock, price, false, traderId, day, stock.getPrice());
        transactions.add(tr);
    }

    public static void sellShares(int shares, Stock stock, int traderId) throws NotEnoughSharesException {
        AbstractTrader trader = getTraderById(traderId);
        double price = sharesOwnedInStock(traderId, stock) * stock.getPrice();
        Transaction tr = new Transaction(shares, stock, price, true, traderId, day, stock.getPrice());
        transactions.add(tr);
    }

    public static long sharesOwnedInStock(int traderId, Stock stock) {
        AbstractTrader trader = getTraderById(traderId);
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

    public static double getTraderMoney(int traderId) {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevant = getTransactionsForTrader(
            traderId
        );
        double money = trader.initialMoney();
        for (Transaction t : relevant) {
            if (t.selling()) {
                money += t.shares() * t.initialPrice();
            } else {
                money -= t.shares() * t.initialPrice();
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

    public static AbstractTrader getTraderById(int traderId) {
        for (Trader t : traders) {
            
        }
    }

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }
}
