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

    public static void buyShares(int shares, String stockName, int traderId) throws NotEnoughMoneyException {
        AbstractTrader trader = getTraderById(traderId);
        Stock stock = getStockByTicker(stockName);
        double price = shares * stock.getPrice();
        Transaction tr = new Transaction(
            shares,
            stock,
            price,
            false,
            traderId,
            currentTransactionIndex++,
            currentDay
        );
        transactions.add(tr);
    }

    public static void sellShares(int shares, String stockName, int traderId) throws NotEnoughSharesException {
        AbstractTrader trader = getTraderById(traderId);
        Stock stock = getStockByTicker(stockName);
        double price = sharesOwnedInStock(traderId, stockName) * stock.getPrice();
        Transaction tr = new Transaction(
            shares,
            stock,
            price,
            true,
            traderId,
            currentTransactionIndex++,
            currentDay
        );
        transactions.add(tr);
    }

    public static long sharesOwnedInStock(int traderId, String stockName) {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevantTransactions = filterByTrader(traderId, copyTransactions());
        long sharesOwned = 0;
        Stock stock = getStockByTicker(stockName);
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

    public static double getTraderMoneyAmount(int traderId) {
        AbstractTrader trader = getTraderById(traderId);
        if (trader == null) {
            return 0;
        }
        ArrayList<Transaction> relevant = filterByTrader(traderId, copyTransactions());
        double money = trader.initialMoney();
        for (Transaction t : relevant) {
            if (t.selling()) {
                money += t.shares() * t.price();
            } else {
                money -= t.shares() * t.price();
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

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }

    public static ArrayList<Transaction> copyTransactions() {
        return (ArrayList<Transaction>) transactions.clone();
    }

    public static ArrayList<Transaction> filterByStock(String stockName, ArrayList<Transaction> trs) {
        Stock stock = getStockByTicker(stockName);
        for (int i=trs.size()-1; i>=0; i--) {
            if (!trs.get(i).stock().equals(stockName)) {
                trs.remove(i);
            }
        }
        return trs;
    }

    public static ArrayList<Transaction> filterByTrader(int traderId, ArrayList<Transaction> trs) {
        for (int i=trs.size()-1; i>=0; i--) {
            if (trs.get(i).traderId() != traderId) {
                trs.remove(i);
            }
        }
        return trs;
    }

    // removes every transaction that is older than days
    public static ArrayList<Transaction> filterByDays(int days, ArrayList<Transaction> trs) {
        for (int i=trs.size()-1; i>=0; i--) {
            if (trs.get(i).dayTransactionMade() < currentDay - days) {
                trs.remove(i);
            }
        }
        return trs;
    }

    
}
