public class Stock {

	private String symbol;
	private String name;
	private double price;
	private final long totalShares; // total shares that exist for this stock - this is not changed, just used to give more weight to certain stock movements

	public Stock(String symbol, String name, double price, long totalShares) {
	    this.symbol = symbol;
		this.name = name;
		this.price = price;
		this.totalShares = totalShares;
	}

	public String getSymbol() {
	    return symbol;
	}

	public String getName() {
	    return name;
	}

	public double getPrice() {
	    return price;
	}

	public long getTotalShares() {
	    return totalShares;
	}

	public String toString() {
	    return String.format("%s $%.2f", symbol, price);
	}
}
