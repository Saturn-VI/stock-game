import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<Trader> traders;

    private static long day;
    private static long currentTransactionIndex;

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        // System.out.println(stocks);
        transactions = new ArrayList<Transaction>();
        traders = new ArrayList<Trader>();
        day = 0;
        currentTransactionIndex = 0;
    }

    public static Stock getStockByName(String stockName) {
        for (Stock stock : getStocks()) {
            if (stock.getSymbol().equals(stockName)) {
                return stock;
            }
        }
        return null;
    }

    public static void sellShares(
        int traderId,
        int sharesAmount,
        String stockName
    ) throws NotEnoughMoneyException {
        if (sharesAmount > getSharesOwnedInStock(traderId, stockName)) {
            throw new NotEnoughMoneyException("");
        }
        Stock stock = getStockByName(stockName);
        transactions.add(
            new Transaction(
                sharesAmount,
                stock.getPrice(),
                true,
                traderId,
                currentTransactionIndex,
                day,
                stock
            )
        );
        currentTransactionIndex++;
    }

    public static void buyShares(
        int traderId,
        int sharesAmount,
        String stockName
    ) throws NotEnoughMoneyException {
        double moneyAmount = getTraderMoneyAmount(traderId);
        Stock stock = getStockByName(stockName);
        double estimatedCost = sharesAmount * stock.getPrice();
        if (estimatedCost > moneyAmount) {
            throw new NotEnoughMoneyException("");
        }
        transactions.add(
            new Transaction(
                sharesAmount,
                stock.getPrice(),
                false,
                traderId,
                currentTransactionIndex,
                day,
                stock
            )
        );
        currentTransactionIndex++;
    }

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

    public static long getSharesOwnedInStock(int traderId, String stockName) {
        Trader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId
        );
        long sharesOwned = 0;
        Stock stock = getStockByName(stockName);
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
                money += t.shares() * t.price();
            } else {
                money -= t.shares() * t.price();
            }
        }
        return money;
    }

    public static double getCurrentTraderProfitOnStock(
        int traderId,
        String stockName
    ) {
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId,
            stockName
        );
        Stock stock = getStockByName(stockName);
        double totalSpent = 0;
        long sharesOwned = 0;
        for (Transaction t : relevantTransactions) {
            if (t.selling()) {
                sharesOwned -= t.shares();
                totalSpent -= t.price();
            } else {
                sharesOwned += t.shares();
                totalSpent += t.price();
            }
        }
        double currentHoldingValue = stock.getPrice() * sharesOwned;
        return currentHoldingValue - totalSpent;
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

    public static ArrayList<Transaction> getTransactionsForTrader(
        int traderId,
        String stockName
    ) {
        ArrayList<Transaction> out = new ArrayList<Transaction>();
        for (Transaction t : transactions) {
            if (
                t.traderId() == traderId &&
                t.stock().getName().equals(stockName)
            ) {
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
