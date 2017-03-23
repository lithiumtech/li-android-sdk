/*
 * LiBaseModelImpl.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import lithium.community.android.sdk.model.helpers.LiPrivacyLevel;

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
        protected LiInt count;

        public LiInt getCount() {
            return count;
        }

        public void setCount(LiInt count) {
            this.count = count;
        }
    }

    public static class LiSum {

        @SerializedName("weight")
        protected LiInt weight;

        public LiInt getWeight() {
            return weight;
        }

        public void setWeight(LiInt weight) {
            this.weight = weight;
        }
    }

    public static class LiInt extends LiBaseModelImpl {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

    public static class LiInteger extends LiBaseModelImpl {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
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

    public static class LiString extends LiBaseModelImpl {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class LiMimeType extends LiString {
    }

    public static class LiModerationStatusType extends LiString {
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
        private LiDateInstant value;
        private SimpleDateFormat LIA_DATE_TIME_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        public void setValue(@NonNull String valueStr) {
            setValue(new LiDateInstant());
        }

        public String getValueAsLithiumFormattedDate() {
            return LIA_DATE_TIME_FORMAT.format(value);
        }

        public LiDateInstant getValue() {
            return value;
        }

        public void setValue(LiDateInstant value) {
            this.value = value;
        }
    }

    public static class LiDateInstant extends LiBaseModelImpl {
        private long value;
        private SimpleDateFormat LIA_DATE_TIME_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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

        public String getValueAsLithiumFormattedDate() {
            return LIA_DATE_TIME_FORMAT.format(value);
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
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
