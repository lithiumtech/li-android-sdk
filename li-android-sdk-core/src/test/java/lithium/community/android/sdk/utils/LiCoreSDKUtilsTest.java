package lithium.community.android.sdk.utils;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */

public class LiCoreSDKUtilsTest {

    private static final String ERROR_MESSAGE = "testing for null";
    private static final String TEST_STRING = "test";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCheckNotNullSingleArgument() {
        thrown.expect(NullPointerException.class);
        LiCoreSDKUtils.checkNotNull(null);
    }

    @Test
    public void testCheckNotNullMulipleArgument() {
        thrown.expect(IllegalArgumentException.class);
        LiCoreSDKUtils.checkNotNull(null, null);
    }

    @Test
    public void testCheckNotNullSingleArgumentWithErrorMessage() {
        try {
            LiCoreSDKUtils.checkNotNull(null, ERROR_MESSAGE);
        } catch (Exception e) {
            Assert.assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testCheckCollectionNotEmptyForNullCollection() {
        try {
            LiCoreSDKUtils.checkCollectionNotEmpty(null, ERROR_MESSAGE);
        } catch (Exception e) {
            Assert.assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testCheckCollectionNotEmptyForNonEmptyCollection() {

        ArrayList<String> list = new ArrayList<String>() {{
            add("A");
            add("B");
            add("C");
        }};
        ArrayList<String> outputList = LiCoreSDKUtils.checkCollectionNotEmpty(list, ERROR_MESSAGE);
        Assert.assertTrue(list.equals(outputList));
    }

    @Test
    public void testReadInputStreamForNullValue() throws IOException {
        thrown.expect(NullPointerException.class);
        LiCoreSDKUtils.readInputStream(null);
    }

    @Test
    public void testReadInputStreamForNonNullValue() throws IOException {
        InputStream is = new ByteArrayInputStream(TEST_STRING.getBytes());
        String output = LiCoreSDKUtils.readInputStream(is);
        Assert.assertEquals(TEST_STRING, output);
    }

    @Test
    public void testCloseQuietly() {
        Exception ex = null;
        InputStream is = new ByteArrayInputStream(TEST_STRING.getBytes());
        try {
            LiCoreSDKUtils.closeQuietly(is);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null, ex);
    }

    @Test
    public void testStringToSet() {
        Assert.assertEquals("[Hello, World]", LiCoreSDKUtils.stringToSet("Hello World").toString());
    }

    @Test
    public void testStringToSetForNullString() {
        Assert.assertEquals(null, LiCoreSDKUtils.stringToSet(null));
    }

    @Test
    public void testPutIfNotNull() throws Exception {
        JSONObject jsonObject = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(jsonObject, "hello", "world");
        Assert.assertEquals("world", jsonObject.get("hello"));
    }

    @Test
    public void testPutIfNotNullForNullJson() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        JSONObject jsonObject = null;
        LiCoreSDKUtils.putIfNotNull(jsonObject, "hello", "world");
    }

    @Test
    public void testPutIfNotNullForNullField() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        JSONObject jsonObject = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(jsonObject, null, "world");
    }

    @Test
    public void testPut() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("hello", "world");
        LiCoreSDKUtils.put(jsonObject, "hello", value);
        Assert.assertEquals("{\"hello\":\"world\"}", jsonObject.get("hello").toString());
    }

    @Test
    public void testPutForIntValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        LiCoreSDKUtils.put(jsonObject, "hello", 2);
        Assert.assertEquals("2", jsonObject.get("hello").toString());
    }

    @Test
    public void testPutForStringValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        LiCoreSDKUtils.put(jsonObject, "hello", "world");
        Assert.assertEquals("world", jsonObject.get("hello").toString());
    }

    @Test
    public void testPutForJSONArrayValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray() {{
            put("hello");
            put("world");
        }};
        LiCoreSDKUtils.put(jsonObject, "hello", jsonArray);
        Assert.assertEquals("[\"hello\",\"world\"]", jsonObject.get("hello").toString());
    }

    @Test
    public void testPutIfNotNullForLongValue() throws Exception {
        JSONObject jsonObject = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(jsonObject, "hello", 1L);
        Assert.assertEquals(1L, jsonObject.get("hello"));
    }

    @Test
    public void testPutIfNotNullForUriValue() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Uri uri = Uri.parse("http://localhost:8080");
        LiCoreSDKUtils.putIfNotNull(jsonObject, "hello", uri);
        Assert.assertEquals("http://localhost:8080", jsonObject.get("hello").toString());
    }

    @Test
    public void testGetString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        Assert.assertEquals("world", LiCoreSDKUtils.getString(jsonObject, "hello"));
    }

    @Test
    public void testGetStringForInvalidField() throws JSONException {
        thrown.expect(JSONException.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        LiCoreSDKUtils.getString(jsonObject, "hi");
    }

    @Test
    public void testGetStringIfDefined() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        Assert.assertEquals("world", LiCoreSDKUtils.getStringIfDefined(jsonObject, "hello"));
    }

    @Test
    public void testGetStringIfDefinedForUndefinedValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        Assert.assertEquals(null, LiCoreSDKUtils.getStringIfDefined(jsonObject, "hi"));
    }

    @Test
    public void testGetLongIfDefined() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Long l = new Long("1");
        jsonObject.put("hello", l);
        Assert.assertEquals(l, LiCoreSDKUtils.getLongIfDefined(jsonObject, "hello"));
    }

    @Test
    public void testGetLongIfDefinedForUndefinedValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Long l = new Long("1");
        jsonObject.put("hello", l);
        Assert.assertEquals(null, LiCoreSDKUtils.getLongIfDefined(jsonObject, "hi"));
    }

    @Test
    public void testIterableToString() {
        ArrayList<String> list = new ArrayList<String>() {{
            add("hello");
            add("world");
        }};

        Assert.assertEquals("hello world", LiCoreSDKUtils.iterableToString(list));
    }

    @Test
    public void testIterableToStringForNullValue() {
        Assert.assertEquals(null, LiCoreSDKUtils.iterableToString(null));
    }

    @Test
    public void testGetUriIfDefined() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String uri = "http://localhost:8080";
        jsonObject.put("hello", uri);
        Assert.assertEquals(uri, LiCoreSDKUtils.getUriIfDefined(jsonObject, "hello").toString());
    }

    @Test
    public void testGetUriIfDefinedForInvalidValue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String uri = "http://localhost:8080";
        jsonObject.put("hello", uri);
        Assert.assertEquals(null, LiCoreSDKUtils.getUriIfDefined(jsonObject, "hi"));
    }


}
