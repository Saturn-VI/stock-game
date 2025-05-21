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

    public static void simulateMarketDay() {
        System.out.println("\nDay " + currentDay);
        ArrayList<Transaction> trs = copyTransactions();
        ArrayList<Integer> shareExchangeList = new ArrayList<>();
        int totalShareExchange = 0;
        double marketSentiment = 0;

        simulateAllBots();

        for (Stock stock : stocks) {
            filterByStock(stock.getSymbol(), trs);
            filterByDays(3, trs);

            int netShares = 0; // selling a lot will result in negative
            for (Transaction tr : trs) {
                if (tr.selling()) netShares -= tr.shares();
                else netShares += tr.shares();
            }

            // extremely basic algorithm that takes in a sentiment
            // based on what the net share is
            // and makes it a bit random
            // and then changes the stock price accordingly

            // NOTE: IF HAVE TIME, MAKE THE ALGORITHM
            // A BIT MORE ADVANCED BY CONSIDERING
            // OVERALL MARKET SENTIMENT
            
            double rawSentiment = 1 + (double) netShares / 10000;
            double sentiment = rawSentiment * (0.975 + Math.random()/20);
            if (stock.getSymbol().equals("AAPL")) System.out.println(rawSentiment + " " + sentiment + " " + netShares);
            stock.setPrice(stock.getPrice() * sentiment);

            shareExchangeList.add(netShares);
        }

        currentDay++;
    }

    public static void simulateAllBots() {
        for (AbstractTrader trader : getListOfTraders()) {
            trader.simulateTraderDay();
        }
    }

    // HELPER METHODS
    public static ArrayList<AbstractTrader> getListOfTraders() {
        return new ArrayList<AbstractTrader>(traders);
    }

    public static long getCurrentDay() {
        return currentDay;
    }

    public static void addTrader(AbstractTrader trader) {
        traders.add(trader);
    }

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
    ) throws NotEnoughSharesException, StockDoesNotExistException {
        Stock stock = getStockByTicker(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }
        if (sharesAmount > getSharesOwnedInStock(traderId, stockName)) {
            throw new NotEnoughSharesException("");
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
        GameWindow.getInstance().updateData();
    }

    public static void sellAllShares(int traderId, String stockName)
        throws NotEnoughSharesException, StockDoesNotExistException {
        try {
            sellShares(
                traderId,
                Math.max(getSharesOwnedInStock(traderId, stockName), 1), // do this so the user can get an error
                stockName
            );
        } catch (NotEnoughSharesException | StockDoesNotExistException e) {
            throw e;
        }
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
        GameWindow.getInstance().updateData();
    }

    public static void buyAllShares(int traderId, String stockName)
        throws NotEnoughMoneyException {
        int sharesThatCanBeBought = Math.max((int) (getTraderMoneyAmount(traderId) /
            getStockByTicker(stockName).getPrice()), 1); // do this so the user can get an error
        try {
            buyShares(traderId, sharesThatCanBeBought, stockName);
        } catch (NotEnoughMoneyException e) {
            throw e;
        }
    }

    public static int getSharesOwnedInStock(int traderId, String stockName)
        throws StockDoesNotExistException {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = filterByTrader(
            traderId,
            copyTransactions()
        );
        int sharesOwned = 0;
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

    public static AbstractTrader getTraderById(int traderId) {
        for (AbstractTrader t : traders) {
            if (t.getTraderId() == traderId) return t;
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
            if (sharesOwned == 0) {
                netMoneyFlow = 0; // reset if no shares at any point
            }
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

    public static ArrayList<String> getStockNames() {
        ArrayList<String> out = new ArrayList<String>();
        for (Stock stock : stocks) {
            out.add(stock.getSymbol());
        }
        return out;
    }

    public static ArrayList<AbstractTrader> getTraders() {
        return traders;
    }

    public static ArrayList<Transaction> copyTransactions() {
        return new ArrayList<Transaction>(transactions);
    }

    public static ArrayList<String> getListOfStocksForTrader(int traderId) {
        ArrayList<String> out = new ArrayList<String>();
        for (Transaction transaction : copyTransactions()) {
            if (transaction.traderId() == traderId) {
                if (!out.contains(transaction.stock().getSymbol())) {
                    out.add(transaction.stock().getSymbol());
                }
            }
        }
        for (int i = out.size() - 1; i >= 0; i--) {
            try {
                if (getSharesOwnedInStock(traderId, out.get(i)) <= 0) {
                    out.remove(i);
                }
            } catch (StockDoesNotExistException e) {}
        }
        return out;
    }

    public static ArrayList<Transaction> filterByStock(
        String stockName,
        ArrayList<Transaction> trs
    ) {
        ArrayList<Transaction> out = new ArrayList<>();
        Stock stock = getStockByTicker(stockName);
        for (Transaction tr : trs) {
            if (tr.stock().equals(stock)) {
                out.add(tr);
            }
        }
        return out;
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
