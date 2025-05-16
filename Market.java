import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<AbstractTrader> traders;

    private static long currentDay;
    private static long currentTransactionIndex;

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        //System.out.println(stocks);
        transactions = new ArrayList<Transaction>();
        traders = new ArrayList<AbstractTrader>();
        currentDay = 0;
        currentTransactionIndex = 0;
    }

    public void simulateMarketDay() {
        ArrayList<Transaction> trs = copyTransactions();
        ArrayList<Integer> shareExchangeList = new ArrayList<>();
        int totalShareExchange = 0;

        for (Stock stock : stocks) {
            filterByStock(stock.getSymbol(), trs);
            filterByDays(5, trs);

            int netShares = 0; // selling a lot will result in negative
            for (Transaction tr : trs) {
                if (tr.selling()) netShares -= tr.shares();
                else netShares += tr.shares();
            }
            shareExchangeList.add(netShares);
        }

        for (int i : shareExchangeList) totalShareExchange += i;
        double sentiment = Math.random();
    }

    /**
     * HELPER METHODS
     */

    public static Stock getStockByTicker(String ticker) {
        for (Stock stock : getStocks()) {
            if (stock.getSymbol().equals(ticker)) {
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
        Stock stock = getStockByTicker(stockName);
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
                currentDay
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
        Stock stock = getStockByTicker(stockName);
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
                currentDay
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
        ArrayList<Transaction> relevantTransactions = filterByTrader(
            traderId,
            copyTransactions()
        );
        long sharesOwned = 0;
        Stock stock = getStockByTicker(stockName);
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

    public static double getTraderMoneyAmount(int traderId) {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevant = filterByTrader(
            traderId,
            copyTransactions()
        );
        double money = trader.initialMoney();
        for (Transaction t : relevant) {
            if (t.selling()) {
                money += (double) t.shares() * t.price();
            } else {
                money -= (double) t.shares() * t.price();
            }
        }
        return money;
    }

    public static AbstractTrader getTraderById(int traderId) {
        for (AbstractTrader t : traders) {
            if (t.getId() == traderId) return t;
        }
        return null;
    }

    public static double getCurrentTraderProfitOnStock(
        int traderId,
        String stockName
    ) throws StockDoesNotExistException {
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId,
            stockName
        );
        Stock stock = getStockByTicker(stockName);
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
        Stock stock = getStockByTicker(stockName);
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

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }

    public static ArrayList<Transaction> copyTransactions() {
        return new ArrayList<Transaction>(transactions);
    }

    public static ArrayList<Transaction> filterByStock(
        String stockName,
        ArrayList<Transaction> trs
    ) {
        Stock stock = getStockByTicker(stockName);
        for (int i = trs.size() - 1; i >= 0; i--) {
            if (!trs.get(i).stock().equals(stockName)) {
                trs.remove(i);
            }
        }
        return trs;
    }

    public static ArrayList<Transaction> filterByTrader(
        int traderId,
        ArrayList<Transaction> trs
    ) {
        for (int i = trs.size() - 1; i >= 0; i--) {
            if (trs.get(i).traderId() != traderId) {
                trs.remove(i);
            }
        }
        return trs;
    }

    // removes every transaction that is older than days
    public static ArrayList<Transaction> filterByDays(
        int days,
        ArrayList<Transaction> trs
    ) {
        for (int i = trs.size() - 1; i >= 0; i--) {
            if (trs.get(i).dayTransactionMade() < currentDay - days) {
                trs.remove(i);
            }
        }
        return trs;
    }
}
