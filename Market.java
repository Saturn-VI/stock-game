import java.util.*;

public class Market {

    private static ArrayList<Stock> stocks;

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

    public static ArrayList<Stock> getStocks() {
        return stocks;
    }
}
