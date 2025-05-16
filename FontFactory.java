import java.awt.*;
import java.util.*;
import java.io.*;

public class FontFactory {
    // has static methods to return font objects
    // e.g. getFont("bold"); getFont("regular");
    // basically just consistent styling for the game
    public static Font getFont(String type, int size) {
        if (type == null || !(new ArrayList<String>(Arrays.asList(
                new String[]{"Black", "Bold", "ExtraBold", "ExtraLight", "Light", "Medium", "Regular", "SemiBold", "Thin"
            }))).contains(type)) {
            return null;
        }
        Font selectedFont = null;
        try {
            File fontFile = new File(String.format("./Gothic_A1/GothicA1-%s.ttf", type));
            InputStream inputStream = new FileInputStream(fontFile);
            selectedFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            inputStream.close();
            selectedFont = selectedFont.deriveFont(Font.PLAIN, size);
        } catch (IOException | FontFormatException e) {
            System.out.println(String.format("Failed to load font %s with size %d: ", type, size) + e.toString());
        }
        return selectedFont;
    }
}
