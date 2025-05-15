/**
 * Transaction is an immutable data class that records(pun not intended lmao) the
 * stuff being exchanged during a transaction. Shares is the number of shares
 * bought/sold, while price is the total price paid/received.
 * traderId is the id of the trader making the transaction
 */
public record Transaction(
    int shares,
    Stock stock,
    double price,
    boolean selling,
    int traderId,
    long transactionIndex,
    long dayTransactionMade,
    Stock stock
) {}
