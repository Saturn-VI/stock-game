import java.util.*;

public class BotTrader extends AbstractTrader {

  public static final String[] firstNames = {
    "Michael",
    "Christopher",
    "Matthew",
    "Joshua",
    "David",
    "Bertie",
    "Steve",
    "James",
    "Daniel",
    "Robert",
    "John",
    "Joseph",
    "Jessica",
    "Jennifer",
    "Amanda",
    "Ashley",
    "Sarah",
    "Stephanie",
    "Melissa",
    "Nicole",
    "Elizabeth",
    "Heather",
    "Baron",
    "Anthony"
  };

  public static final String[] lastNames = {
    "Farthing",
    "Deepockets",
    "Topphat",
    "Moneybags",
    "Rolex",
    "Vanderbilt",
    "Rockefeller",
    "Ferrari",
    "Duke",
    "DuPont",
    "Forbes",
    "Hermes",
    "Porsche",
    "Van Damme",
    "Goldman",
    "Silvers",
    "Wallstreet",
    "Betts",
    "Buckett",
    "Shockey",
    "Broker",
    "Dollarwise",
    "Poore",
    "Riche",
    "Debt"
  };

  public BotTrader(int traderId) {
    super(traderId, getRandomName());
  }

  public BotTrader(int traderId, int money) {
    super(traderId, money, getRandomName());
  }

  @Override
  public void simulateTraderDay(){
    int sharesBought = 0;
    int sharesToBuy = (int) (Math.random() * 5);
    for (Stock stock : Market.getStocks()) {
      try {
        if (Math.random()*20 < 1) {
          Market.buyShares(getTraderId(), 1, stock.getSymbol());
          sharesBought++;
          System.out.println(getName()+" ($" + Market.getTraderMoneyAmount(getTraderId()) + ") bought a share of " + stock.toString());
        } 
        } catch (NotEnoughMoneyException e) {
        continue;
      }
      if (sharesBought >= sharesToBuy) break;
    }
  
    ArrayList<String> traderStocks = Market.getListOfStocksForTrader(getTraderId());
    Collections.shuffle(traderStocks);
    if (Market.getCurrentDay() > 3) {
      for (int i=0; i<(int)(Math.random() * 5); i++) {
        try {
          Market.sellShares(getTraderId(), 1, traderStocks.get(i));
          System.out.println(getName()+" ($" + Market.getTraderMoneyAmount(getTraderId()) + ") sold a share of " + Market.getStockByTicker(traderStocks.get(i)).toString());
        } catch (Exception e) {
          continue;
        }
      }
    }
    
  }

  public static String getRandomName() {
    return firstNames[(int)(Math.random() * firstNames.length)] + " " + lastNames[(int)(Math.random() * lastNames.length)];
  }
}
