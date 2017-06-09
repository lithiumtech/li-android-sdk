package lithium.community.android.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.NoSuchPropertyException;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;

/**
 * Created by Lithium Technologies Inc on 5/29/17.
 */

class LiSecuredPrefManager {
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static LiSecuredPrefManager _instance;
    private MessageDigest sha;
    private Key aesKey;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int ENCRYPTION_LENGTH = 16;
    private byte[] encryptionKey;

    /**
     * Instance of this.
     */
    public static LiSecuredPrefManager getInstance() {
        if (_instance == null) {
            Log.e(LI_LOG_TAG, "LiSecuredPrefManager not intialized. Call init method first");
            return null;
        }
        return _instance;
    }
    private static String encode(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static byte[] decode(String input) {
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private LiSecuredPrefManager(Context context) throws NoSuchAlgorithmException {
        String encryptionStr = context.getPackageName() + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        encryptionKey = encryptionStr.getBytes();
        sha = MessageDigest.getInstance("SHA-1");
        aesKey = new SecretKeySpec(Arrays.copyOf(sha.digest(encryptionKey), ENCRYPTION_LENGTH), ENCRYPTION_ALGORITHM);
    }

    public static synchronized LiSecuredPrefManager init(Context context) {
        if (isInitialized.compareAndSet(false, true)) {
            try {
                _instance = new LiSecuredPrefManager(context);
            } catch (NoSuchAlgorithmException e) {
                Log.e(LiCoreSDKConstants.LI_LOG_TAG, e.getMessage());
            }
        }
        return _instance;
    }

    public String encrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return encode(cipher.doFinal(str.getBytes("UTF-8")));
    }

    public String decrypt(String encrStr) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return new String(cipher.doFinal(decode(encrStr)), "UTF-8");
    }

    /**
     * fetches shared preference
     *
     * @param context {@link Context}
     * @return SharedPreferences
     */
    SharedPreferences getSecuredPreferences(Context context) {
        return context.getSharedPreferences(LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    public void remove(Context context, String key) {
        try {
            _instance.getSecuredPreferences(context).edit().remove(
                    _instance.encrypt(key)).apply();
        } catch (Exception e) {
            Log.e(LI_LOG_TAG, "Encryption error, "+e.getMessage());
            _instance.getSecuredPreferences(context).edit().remove(key).apply();
        }
    }

    public void putString(Context context, String key, String value) {
        try {
            _instance.getSecuredPreferences(context).edit().putString(
                    _instance.encrypt(key),
                    _instance.encrypt(value)).apply();
        } catch (Exception e) {
            Log.e(LI_LOG_TAG, "Encryption error, "+e.getMessage());
            _instance.getSecuredPreferences(context).edit().putString(key, value).apply();
        }
    }

    public String getString(Context context, String key) {
        //get secured preferences and check key has any value there
        SharedPreferences securedPreferences = _instance.getSecuredPreferences(context);
        String value;
        try {
            value = _instance.decrypt(securedPreferences.getString(_instance.encrypt(key), null));
            if (value == null) {
                //if not present then check in old unencrypted preferences and then move it new encrypted preferences file
                value = securedPreferences.getString(key, null);
                putString(context, key, value);
                securedPreferences.edit().remove(key).apply();
            }
        } catch (Exception e) {
            Log.e(LI_LOG_TAG, "Encryption error, "+e.getMessage());
            value = securedPreferences.getString(key, null);
        }
        return value;
    }
}
