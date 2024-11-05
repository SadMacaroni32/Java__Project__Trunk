import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class HMACUtil {
    private static final String HMAC_KEY = 
    "201447c254fd40c7c49a6ebcab17d904920fa676f0091216362cbc5a74f49968"; 

    public static byte[] generateHMAC(byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec hmacKey = new SecretKeySpec(HMAC_KEY.getBytes(), "HmacSHA256");
        mac.init(hmacKey);
        return mac.doFinal(data);
    }

    public static boolean verifyHMAC(byte[] data, byte[] receivedHmac) throws Exception {
        byte[] calculatedHmac = generateHMAC(data);
        return Arrays.equals(calculatedHmac, receivedHmac);
    }
}
