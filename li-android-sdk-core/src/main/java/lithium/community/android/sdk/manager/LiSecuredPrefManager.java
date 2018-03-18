/*
 * Copyright 2018 Lithium Technologies Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lithium.community.android.sdk.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import lithium.community.android.sdk.exception.LiInitializationException;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.MessageConstants;

/**
 * <p>
 * Lia Secured Preference Manager is used internally by the SDK to
 * securely manage preferences which are used by the SDK.
 * </p>
 *
 * @author adityasharat
 */
class LiSecuredPrefManager {

    private static final String ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int ENCRYPTION_LENGTH = 16;

    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static LiSecuredPrefManager instance;

    @NonNull
    private final Key key;

    /**
     * Default private constructor.
     *
     * @param secret The secret encryption key.
     * @throws NoSuchAlgorithmException If the device does not support SHA-1 encryption.
     */
    private LiSecuredPrefManager(@NonNull String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        LiCoreSDKUtils.checkNotNull(secret, MessageConstants.wasNull("secret encryption key"));
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        key = new SecretKeySpec(Arrays.copyOf(sha.digest(secret.getBytes()), ENCRYPTION_LENGTH), "AES");
    }

    /**
     * Default initializer.
     *
     * @param secret The secret encryption key.
     */
    public static synchronized void initialize(@NonNull String secret) throws LiInitializationException {
        if (isInitialized.compareAndSet(false, true)) {
            try {
                instance = new LiSecuredPrefManager(secret);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new LiInitializationException(LiSecuredPrefManager.class.getSimpleName(), e);
            }
        }
    }

    /**
     * Deprecated initializer.
     *
     * @param context An Android context.
     * @return instance of Lithium Secured Preference Manager.
     * @deprecated Use {@link #initialize(String)} instead.
     */
    @Deprecated
    public static synchronized LiSecuredPrefManager init(@NonNull Context context) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        try {
            initialize(context.getPackageName() + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (LiInitializationException e) {
            e.printStackTrace();
        }

        return getInstance();
    }

    /**
     * Get instance the of Lia Secured Preference Manager.
     *
     * @return Instance of the Secured Preference Manager.
     */
    @Nullable
    public static LiSecuredPrefManager getInstance() {
        return instance;
    }

    @NonNull
    private static String encode(@NonNull byte[] input) throws UnsupportedEncodingException {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    @NonNull
    private static byte[] decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }

    /**
     * Get the instance of the shared preference.
     *
     * @return Lia Shared Preferences.
     */
    @NonNull
    private SharedPreferences getSecuredPreferences(@NonNull Context context) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        return context.getSharedPreferences(LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get a value of a preference.
     *
     * @param context An Android Context.
     * @param key     The preference key.
     * @return The value of the key or {@code null} if {@code key} does not exist.
     */
    @Nullable
    public String getString(@NonNull Context context, @NonNull String key) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNullOrNotEmpty(key, MessageConstants.wasNull("key"));
        SharedPreferences preferences = getSecuredPreferences(context);
        String value = null;
        try {
            String encryptedKey = encrypt(key);
            String encryptedValue = preferences.getString(encryptedKey, null);
            if (encryptedValue != null) {
                value = decrypt(encryptedValue);
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | UnsupportedEncodingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
            value = preferences.getString(key, null);
        }
        return value;
    }

    /**
     * Saves a key and value in preference.
     *
     * @param context An Android Context.
     * @param key     The preference key.
     * @param value   Its value.
     */
    public void putString(@NonNull Context context, @NonNull String key, @NonNull String value) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNullOrNotEmpty(key, MessageConstants.wasNull("key"));
        LiCoreSDKUtils.checkNullOrNotEmpty(key, MessageConstants.wasNull("value"));
        try {
            String encryptedKey = encrypt(key);
            String encryptedValue = encrypt(value);
            getSecuredPreferences(context).edit().putString(encryptedKey, encryptedValue).apply();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | UnsupportedEncodingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
            getSecuredPreferences(context).edit().putString(key, value).apply();
        }
    }

    /**
     * Removes a saved preference.
     *
     * @param context An Android Context.
     * @param key     The preference key.
     */
    public void remove(@NonNull Context context, @NonNull String key) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNullOrNotEmpty(key, MessageConstants.wasNull("key"));
        try {
            String encryptedString = encrypt(key);
            getSecuredPreferences(context).edit().remove(encryptedString).apply();
        } catch (Exception e) {
            e.printStackTrace();
            getSecuredPreferences(context).edit().remove(key).apply();
        }
    }

    @NonNull
    private String encrypt(@NonNull String string) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = string.getBytes();
        byte[] encrypted = cipher.doFinal(bytes);
        return encode(encrypted);
    }

    @NonNull
    private String decrypt(@NonNull String string) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = decode(string);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}
