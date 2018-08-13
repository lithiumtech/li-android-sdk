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

package com.lithium.community.android.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.model.helpers.LiPrivacyLevel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Lithium response base model.
 */
public class LiBaseModelImpl implements LiBaseModel {

    @Override
    public LiBaseModel getModel() {
        return this;
    }

    public static class LiCount {

        @SerializedName("count")
        protected int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class LiSum {

        @SerializedName("weight")
        protected int weight;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public static class LiLong extends LiBaseModelImpl {
        private Long value;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

    public static class LiBoolean extends LiBaseModelImpl {

        private Boolean value;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

    public static class LiFloat extends LiBaseModelImpl {

        private Float value;

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }

    public static class LiDate extends LiBaseModelImpl {
        private static final SimpleDateFormat LIA_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        private LiDateInstant value;

        public String getValueAsLithiumFormattedDate() {
            return LIA_DATE_TIME_FORMAT.format(value);
        }

        public LiDateInstant getValue() {
            return value;
        }

        public void setValue(@NonNull String valueStr) {
            setValue(new LiDateInstant());
        }

        public void setValue(LiDateInstant value) {
            this.value = value;
        }
    }

    public static class LiDateInstant extends LiBaseModelImpl {
        private static final SimpleDateFormat LIA_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        private long value;

        public String getValueAsLithiumFormattedDate() {
            return LIA_DATE_TIME_FORMAT.format(value);
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public void setValue(@NonNull String valueStr) {
            LIA_DATE_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
            Date dt = null;
            try {
                dt = LIA_DATE_TIME_FORMAT.parse(valueStr);
            } catch (ParseException e) {
                dt = Calendar.getInstance().getTime();
            }
            setValue(dt.getTime());
        }
    }

    public static class LiPrivacyLevelValue extends LiBaseModelImpl {

        private LiPrivacyLevel value;

        public LiPrivacyLevel getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = LiPrivacyLevel.getExternalSource(value);
        }
    }
}
