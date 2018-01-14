package lithium.community.android.sdk.utils;

/*
* LiUUIDUtils.java
* Created on Sep 27, 2012
*
* Copyright 2012 Lithium Technologies, Inc.
 * Emeryville, California, U.S.A.  All Rights Reserved.
*
* This software is the  confidential and proprietary information
* of  Lithium  Technologies,  Inc.  ("Confidential Information")
* You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
*/


import java.util.UUID;

/**
 * On LIA side fetching is done bu UUID, so this class converts UUID to Client Id.
 */
public final class LiUUIDUtils {
    private LiUUIDUtils() {
    }

    /**
     * Converts a {@link UUID} to a byte array.
     *
     * @param uuid
     * @return the UUID byte array.
     */
    public static final byte[] toBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;
    }

    /**
     * Converts a {@link UUID} String to a byte array.
     *
     * @param uuidString
     * @return the UUID byte array.
     * @throws IllegalArgumentException if name does not conform to the string representation as described in
     *                                  {@link UUID#toString}.
     */
    public static final byte[] toBytes(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);

        return toBytes(uuid);
    }

    /**
     * Converts a byte array into a {@link UUID}.
     *
     * @param bytes
     * @return the UUID represented by the bytes passed in.
     */
    public static final UUID toUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }

        return new UUID(msb, lsb);
    }

}