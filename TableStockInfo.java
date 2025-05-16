// this represents a single snapshot in time
public record TableStockInfo(
    String stockName,
    String stockSymbol, // get from market
    double stockPrice, // get from market
    long sharesOwned, // get from market
    double holdingValue, // simple multiplication
    double currentProfit // should be mostly calculated in market
) {}
