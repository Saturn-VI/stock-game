// represents a single snapshot in time
public record StockListTableInfo(
    String stockName,
    String stockSymbol,
    double stockPrice,
    double changeFromYesterday,
    double changePercent,
    int sharesInStock // potentially zero
) {}
