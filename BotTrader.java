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
    "Ferrero",
    "Wallstreet",
    "Betts",
    "Buckett",
    "Shockey"
  };

  public BotTrader(int traderId) {
    super(traderId, getRandomName());
  }

  public BotTrader(int traderId, int money) {
    super(traderId, money, getRandomName());
  }

  public static String getRandomName() {
    return firstNames[(int)(Math.random() * firstNames.length)] + " " + lastNames[(int)(Math.random() * lastNames.length)];
  }
}
