import java.util.*;

public class Market {
    private static Market instance;
	private ArrayList<Stock> stocks;

	private Market() {
	    // read in from checkpoint file
		stocks = DataReader.getStocks();
		System.out.println(stocks);
	}

	public Stock getStockByTicker(String ticker) {
	    for (Stock stock : getInstance().getStocks()) {
			if (stock.getSymbol().equals(ticker)) {
			    return stock;
			}
		}
		return null;
	}

	public ArrayList<Stock> getStocks() {
	    return stocks;
	}

	public static Market getInstance() {
	    if (instance == null) {
	    instance = new Market();
		}
		return instance;
	}
}
