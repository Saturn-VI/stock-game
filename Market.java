import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<AbstractTrader> traders;
    private static int day = 0;

    private static long currentTransactionIndex;

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        // System.out.println(stocks);
        transactions = new ArrayList<Transaction>();
        traders = new ArrayList<AbstractTrader>();
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
    ) throws NotEnoughMoneyException, StockDoesNotExistException {
        Stock stock = getStockByName(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }
        if (sharesAmount > getSharesOwnedInStock(traderId, stockName)) {
            throw new NotEnoughMoneyException("");
        }
        transactions.add(
            new Transaction(
                sharesAmount,
                stock,
                stock.getPrice(),
                true,
                traderId,
                currentTransactionIndex,
                day
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
        double estimatedCost = (double) sharesAmount * stock.getPrice();
        if (estimatedCost > moneyAmount) {
            throw new NotEnoughMoneyException("");
        }
        transactions.add(
            new Transaction(
                sharesAmount,
                stock,
                stock.getPrice(),
                false,
                traderId,
                currentTransactionIndex,
                day
            )
        );
        currentTransactionIndex++;
    }

    public static long getSharesOwnedInStock(int traderId, String stockName)
        throws StockDoesNotExistException {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId
        );
        long sharesOwned = 0;
        Stock stock = getStockByName(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }
        for (Transaction transaction : relevantTransactions) {
            boolean isStock =
                transaction.stock() != null &&
                transaction.stock().equals(stock);
            if (isStock) {
                if (transaction.selling()) {
                    sharesOwned -= transaction.shares();
                } else {
                    sharesOwned += transaction.shares();
                }
            }
        }
        return sharesOwned;
    }

    public static ArrayList<String> getListOfStocksForTrader(int traderId) {
        ArrayList<String> out = new ArrayList<String>();
        for (Transaction t : transactions) {
            if (
                t.traderId() == traderId && !out.contains(t.stock().getName())
            ) {
                out.add(t.stock().getName());
            }
        }
        return out;
    }

    public static double getTraderMoneyAmount(int traderId) {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevant = getTransactionsForTrader(traderId);
        double money = trader.getInitialMoney();
        for (Transaction t : relevant) {
            if (t.selling()) {
                money += (double) t.shares() * t.price();
            } else {
                money -= (double) t.shares() * t.price();
            }
        }
        return money;
    }

    public static double getCurrentTraderProfitOnStock(
        int traderId,
        String stockName
    ) throws StockDoesNotExistException {
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId,
            stockName
        );
        Stock stock = getStockByName(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }
        long sharesOwned = 0;
        double netMoneyFlow = 0;

        for (Transaction t : relevantTransactions) {
            if (t.selling()) {
                netMoneyFlow += (double) t.shares() * t.price();
                sharesOwned -= t.shares();
            } else {
                netMoneyFlow -= (double) t.shares() * t.price();
                sharesOwned += t.shares();
            }
        }

        return (double) sharesOwned * stock.getPrice() + netMoneyFlow;
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
    ) throws StockDoesNotExistException {
        ArrayList<Transaction> out = new ArrayList<Transaction>();
        Stock stock = getStockByName(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }
        for (Transaction t : transactions) {
            if (
                t.traderId() == traderId &&
                t.stock() != null &&
                t.stock().equals(stock)
            ) {
                out.add(t);
            }
        }
        return out;
    }

    public static AbstractTrader getTraderById(int traderId) {
        for (AbstractTrader t : traders) {
            if (t.getTraderId() == traderId) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }
}
