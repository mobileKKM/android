package de.codebucket.mkkm.util;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.codebucket.mkkm.BuildConfig;

public class EncryptUtils {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "38782f413f442847";

    // Base64 validation regular expression
    private static final Pattern BASE64_PATTERN = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

    public static String encrpytString(String decrypted) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(EncryptUtils.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] data = cipher.doFinal(decrypted.getBytes("utf-8"));
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decryptString(String encrypted) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(EncryptUtils.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] data = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
            return new String(data, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Key generateKey() {
        String key = BuildConfig.ENCRYPTION_KEY;
        if (key.isEmpty() || key.equals("null")) {
            key = EncryptUtils.KEY;
        }

        return new SecretKeySpec(key.getBytes(), EncryptUtils.ALGORITHM);
    }

    public static boolean isBase64(String base64) {
        final String sanitized = base64.replaceAll("\\s", "");
        return BASE64_PATTERN.matcher(sanitized).matches();
    }
}
