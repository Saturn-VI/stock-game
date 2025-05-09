public class Stock {

	private String symbol;
	private String name;
	private double price;
	private int totalShares; // total shares that exist for this stock

	public Stock(String symbol, String name, double price, int totalShares) {
	    this.symbol = symbol;
		this.name = name;
		this.price = price;
		this.totalShares = totalShares;
	}
}
