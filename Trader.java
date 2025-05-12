public record Trader(
    int traderId,
    double initialMoney,
    AbstractTrader tradingAgent
) {}
