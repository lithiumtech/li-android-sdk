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

package com.lithium.community.android.utils;

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
     * @param uuid The UUID to convert.
     * @return the UUID byte array.
     */
    public static byte[] toBytes(UUID uuid) {
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
     * Converts a string to UUID and returns it as a byte array.
     *
     * @param input the input string.
     * @return the byte array if the UUID.
     * @throws IllegalArgumentException if name does not conform to the string representation as described in
     *                                  {@link UUID#toString}.
     */
    public static byte[] toBytes(String input) {
        UUID uuid = UUID.fromString(input);

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