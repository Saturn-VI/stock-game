import java.util.*;

public class Market {

	private ArrayList<Stock> stocks;

	public Market() {
	    // read in from checkpoint file
		stocks = DataReader.getStocks();
		System.out.println(stocks);
	}
}
