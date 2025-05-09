import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class DataReader {
    public static ArrayList<Stock> getStocks() {
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        Scanner scan;
        try {
            scan = new Scanner(new File("data.dat"));
        } catch (FileNotFoundException exception) {
            return new ArrayList<Stock>(); // return empty ArrayList if data file not found
        }

        while (scan.hasNextLine()) {
            String symbol = scan.next();
            double price = scan.nextDouble();
            long totalShares = scan.nextLong();
            String name = scan.nextLine();
            stocks.add(new Stock(symbol, name, price, totalShares));
        }
        scan.close();
        System.out.println(stocks);
        return stocks;
    }
}
