public class PlayerTrader extends AbstractTrader {

  public PlayerTrader(int traderId) {
    super(traderId, "You");
  }

  public PlayerTrader(int traderId, int money) {
    super(traderId, money, "You");
  }
}
