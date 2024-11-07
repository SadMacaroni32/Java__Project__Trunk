package src.utility;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private static Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("C:/Users/jaquino/Desktop/Github_Planet/Java__Project__Trunk/Machine-Problem/src/properties/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
