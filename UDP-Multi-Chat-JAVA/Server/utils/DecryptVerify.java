package Server.utils;

import java.util.Arrays;

import Server.security.HMACUtil;
import Server.security.EncryptionUtil;

public class DecryptVerify {
public static String decryptAndVerify(byte[] data) throws Exception {
    byte[] encryptedMessage = Arrays.copyOfRange(data, 0, data.length - 32); // Assuming HMAC is 32 bytes
    byte[] receivedHmac = Arrays.copyOfRange(data, data.length - 32, data.length);

    if (!HMACUtil.verifyHMAC(encryptedMessage, receivedHmac)) {
        return null; // Invalid HMAC, discard the message
    }

    return EncryptionUtil.decrypt(encryptedMessage);
}
}