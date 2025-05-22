import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<AbstractTrader> traders;

    private static long currentDay;
    private static long currentTransactionIndex;

    private static HashMap<Stock, Double> evSentiments = new HashMap<>();
    private static HashMap<Stock, Double> hidSentiments = new HashMap<>();

    private static final String[] GOODEVENTS = {
        "The president has found an interest in %s. This stock might grow.",
        "There has been a CEO replacement in %s. Investors are hopeful that this new leadership help grow the company.",
        "%s has unveiled groundbreaking technology. Investors are excited to see what it means for the future of the company.",
        "%s has shifted to more ethical workplace practices. This may help boost stock growth for that company.",
        "The government has given %s a huge contract. Eager investors are immediately buying.",
        "Reports show that %s has beaten revenue and profit expectations. Experts predict it may continue into the future.",
        "Central bank lowers interest rates. $s may experience positive impact.",
        "%s expands into a larger market. Investors project growth for this company.",
        "The newest product of %s is hugely popular among consumers. It has exceeded sales targets and the company is now on track for further growth.",
        "Investors have recently placed %s as their top buys. Growth is predicted.",
    };

    private static final String[] BADEVENTS = {
        "Reports show that %s did not meet revenue expectations. This company might experience a downturn.",
        "The CEO of %s was found to be involved in a scandal. The future of this company does not look good.",
        "%s has been found to be abusing its workers. Support for this company is waning.",
        "The CEO of %s was assassinated last week. Stock prices are expected to drop.",
        "New technologies in other countries are predicted to surpass %s. This company will experience some falls.",
        "Rising interest rates will likely harm %s. Experts recommend selling now.",
        "Geopolitical tensions will negatively affect %s. Its stock is predicted to drop.",
        "Worker unrest halts production in %s. Stock prices will drop.",
        "%s was hit by a large cyber attack that compromised tons of data. Investors are looking to sell.",
        "%s has been actively making their produts less and less appealing to consumers. Experts suggest to sell now.",
    };

    public static void initializeMarket() {
        // read in from checkpoint file
        stocks = DataReader.getStocks();
        //System.out.println(stocks);
        transactions = new ArrayList<Transaction>();
        traders = new ArrayList<AbstractTrader>();
        currentDay = 0;
        currentTransactionIndex = 0;

        for (Stock stock : getStocks()) {
            evSentiments.put(stock, 1.0);
            hidSentiments.put(stock, 1.0);
        }
    }

    public static void simulateMarketDay() {
        System.out.println("\nDay " + currentDay);
        ArrayList<Transaction> trs;

        for (Stock stock : stocks) {
            if (Math.random() < 0.005) {
                System.out.println(randomEvent(stock));
            }
            if (Math.random() < 0.02) {
                System.out.println(hiddenEvent(stock));
            }
        }

        simulateAllBots();

        for (Stock stock : stocks) {
            trs = copyTransactions();

            filterByStock(stock.getSymbol(), trs);
            filterByDays(5, trs);

            int netShares = 0; // selling a lot will result in negative
            for (Transaction tr : trs) {
                if (tr.selling()) {
                    netShares -= tr.shares();
                } else {
                    netShares += tr.shares();
                }
            }

            // extremely basic algorithm that takes in a sentiment
            // based on what the net share is
            // and makes it a bit random
            // and then changes the stock price accordingly

            // NOTE: IF HAVE TIME, MAKE THE ALGORITHM
            // A BIT MORE ADVANCED BY CONSIDERING
            // OVERALL MARKET SENTIMENT

            double rawSentiment = 1 + (double) netShares / 10000;
            double sentiment = rawSentiment * (0.975 + Math.random() / 20);

            sentiment *= evSentiments.get(stock);
            sentiment *= hidSentiments.get(stock);

            if (stock.getSymbol().equals("AAPL")) System.out.println(
                rawSentiment + " " + sentiment + " " + netShares
            );

            stock.setPrice(stock.getPrice() * sentiment);

            // statement below updates the sentiments to be closer to 1
            evSentiments.put(stock, Math.pow(evSentiments.get(stock), 0.6));
            hidSentiments.put(stock, Math.pow(evSentiments.get(stock), 0.6));
        }

        currentDay++;
        GameWindow.getInstance().updateData();
    }

    public static void simulateAllBots() {
        for (AbstractTrader trader : getListOfTraders()) {
            trader.simulateTraderDay();
        }
    }

    // events have a stronger effect on stock prices than hidden events, but are rarer.
    public static String randomEvent(Stock stock) {
        double eventSentiment = 1;
        int goodOrBad = (int) (Math.random() * 2);
        // 0 = bad, 1 = good
        String randomMessage = "";
        if (goodOrBad == 0) {
            randomMessage = BADEVENTS[(int) (Math.random() * BADEVENTS.length)];
            eventSentiment = 0.75 + Math.random() / 5;
        } else {
            randomMessage = GOODEVENTS[(int) (Math.random() *
                    GOODEVENTS.length)];
            eventSentiment = 1.05 + Math.random() / 3;
        }
        GameWindow.getInstance()
            .triggerStockEvent(
                goodOrBad == 1,
                stock.getSymbol(),
                randomMessage
            );
        evSentiments.put(stock, evSentiments.get(stock) * eventSentiment);
        return String.format(randomMessage, stock.toString()) + eventSentiment;
    }

    // the hidden events make stocks more volatile and the overall game more challenging.
    public static String hiddenEvent(Stock stock) {
        double hiddenSentiment = 0.9;
        hiddenSentiment += Math.random() * 0.2;
        hidSentiments.put(stock, hiddenSentiment);
        return (
            stock.getSymbol() + " got a hidden sentiment of " + hiddenSentiment
        );
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
        if (traderId == Main.playerTraderId) {
            GameWindow.getInstance().updateData(); // small optimization
        }
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
        if (traderId == Main.playerTraderId) {
            GameWindow.getInstance().updateData(); // small optimization
        }
    }

    public static void buyAllShares(int traderId, String stockName)
        throws NotEnoughMoneyException {
        int sharesThatCanBeBought = Math.max(
            (int) (getTraderMoneyAmount(traderId) /
                getStockByTicker(stockName).getPrice()),
            1
        ); // do this so the user can get an error
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

    public static double getProfitPercentageOnStock(
        int traderId,
        String stockName
    ) throws StockDoesNotExistException {
        double initialCost = 0;
        ArrayList<Transaction> relevantTransactions = getTransactionsForTrader(
            traderId,
            stockName
        );
        Stock stock = getStockByTicker(stockName);
        if (stock == null) {
            throw new StockDoesNotExistException("");
        }

        for (Transaction t : relevantTransactions) {
            if (t.selling()) {
                initialCost -= (double) t.shares() * t.price();
            } else {
                initialCost += (double) t.shares() * t.price();
            }
        }
        return (
            (initialCost + getCurrentTraderProfitOnStock(traderId, stockName)) /
            initialCost
        );
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
