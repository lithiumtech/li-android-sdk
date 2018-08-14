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

package com.lithium.community.android.model.response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiAvatar;
import com.lithium.community.android.model.helpers.LiImage;
import com.lithium.community.android.model.helpers.LiRanking;
import com.lithium.community.android.model.response.LiUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/*
 Created by mahaveer.udabal on 10/18/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LiUserTest {

    private final Boolean ANONYMOUS = true;
    private final Float AVERAGE_MESSAGE_RATING = 3.2f;
    private final Float AVERAGE_RATING = 3.6f;
    private final Boolean BANNED = true;
    private final Boolean DELETED = false;
    private final String EMAIL = "lithium@lithium.com";
    private final String HREF = "//messages//213824";
    private final Long ID = 123456L;
    private final String PROFILE_PAGE_URL = "http://community.lithium.com/t5/user/viewprofilepage/user-id/52295";
    private final Boolean REGISTERED = true;
    private final Boolean VALID = false;
    private final String LI_DESCRIPTION = "Li_Description";
    private final String LI_RANKER = "Li_Ranker";
    private final String LI_LOGIN = "Li_Login";
    private final String LI_DATE_INSTANT = "HH:mm:ss.SSS";
    @Mock
    private LiImage avatarImage;
    @Mock
    private LiRanking ranking;
    private String login;
    private LiUser liUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        liUser = new LiUser();
    }

    @Test
    public void getAvatarImageTest() {
        liUser.setAvatarImage(avatarImage);
        when(avatarImage.getDescription()).thenReturn(LI_DESCRIPTION);
        assertNotEquals(null, liUser.getAvatarImage());
        assertEquals(LI_DESCRIPTION, liUser.getAvatarImage().getDescription());
    }

    @Test
    public void getAverageMessageRatingTest() {
        LiBaseModelImpl.LiFloat liFloat = new LiBaseModelImpl.LiFloat();
        liFloat.setValue(AVERAGE_MESSAGE_RATING);
        liUser.setAverageMessageRating(liFloat);
        assertEquals(AVERAGE_MESSAGE_RATING, liUser.getAverageMessageRating());
        assertTrue(liUser.getAverageMessageRatingAsLiFloat() instanceof LiBaseModelImpl.LiFloat);
        assertEquals(AVERAGE_MESSAGE_RATING, liUser.getAverageMessageRatingAsLiFloat().getValue());
    }

    @Test
    public void getAverageRatingTest() {
        LiBaseModelImpl.LiFloat liFloat = new LiBaseModelImpl.LiFloat();
        liFloat.setValue(AVERAGE_RATING);
        liUser.setAverageRating(liFloat);
        assertEquals(AVERAGE_RATING, liUser.getAverageRating());
        assertTrue(liUser.getAverageRatingAsLiFloat() instanceof LiBaseModelImpl.LiFloat);
        assertEquals(AVERAGE_RATING, liUser.getAverageRatingAsLiFloat().getValue());
    }

    @Test
    public void getBannedTest() {
        liUser.setBanned(BANNED);
        assertNotEquals(null, liUser.getBanned());
        assertEquals(BANNED, liUser.getBanned());
    }

    @Test
    public void getDeletedTest() {
        liUser.setDeleted(DELETED);
        assertNotEquals(null, liUser.getDeleted());
        assertEquals(DELETED, liUser.getDeleted());
    }

    @Test
    public void getEmailTest() {
        String emailStr = EMAIL;
        liUser.setEmail(emailStr);
        assertNotEquals(null, liUser.getEmail());
        assertEquals(EMAIL, liUser.getEmail());
    }

    @Test
    public void getHrefTest() {
        liUser.setHref(HREF);
        assertNotEquals(null, liUser.getHref());
        assertEquals(HREF, liUser.getHref());
    }

    @Test
    public void getRankingTest() {
        liUser.setRanking(ranking);
        when(ranking.getName()).thenReturn(LI_RANKER);
        assertNotEquals(null, liUser.getRanking());
        assertEquals(LI_RANKER, liUser.getRanking().getName());
    }

    @Test()
    public void getLastVisitInstantTest() {
        LiBaseModelImpl.LiDate liDateInstant = new LiBaseModelImpl.LiDate();
        liDateInstant.setValue(LI_DATE_INSTANT);
        liUser.setLastVisitInstant(liDateInstant);
        assertEquals(liDateInstant.getValue(), liUser.getLastVisitInstant());
        assertEquals(liDateInstant.getValue(), liUser.getLastVisitInstantAsLithiumInstant().getValue());
    }

    @Test
    public void getLoginTest() {
        liUser.setLogin(LI_LOGIN);
        assertNotEquals(null, liUser.getLogin());
        assertEquals(LI_LOGIN, liUser.getLogin());
    }

    @Test
    public void getIdTest() {
        liUser.setId(ID);
        assertNotEquals(null, liUser.getId());
        assertEquals(ID, liUser.getId());
    }

    @Test
    public void getProfilePageUrlTest() {
        liUser.setProfilePageUrl(PROFILE_PAGE_URL);
        assertNotEquals(null, liUser.getProfilePageUrl());
        assertEquals(PROFILE_PAGE_URL, liUser.getProfilePageUrl());
    }

    @Test
    public void getRegisteredTest() {
        liUser.setRegistered(REGISTERED);
        assertNotEquals(null, liUser.getRegistered());
        assertEquals(REGISTERED, liUser.getRegistered());
    }

    @Test
    public void isValidTest() {
        liUser.setValid(VALID);
        assertNotEquals(null, liUser.isValid());
        assertEquals(VALID, liUser.isValid());
    }

    @Test()
    public void getRegistrationInstantTest() {
        LiBaseModelImpl.LiDate liDateInstant = new LiBaseModelImpl.LiDate();
        liDateInstant.setValue(LI_DATE_INSTANT);
        liUser.setRegistrationInstant(liDateInstant);
        assertEquals(liDateInstant.getValue(), liUser.getRegistrationInstant());
        assertEquals(liDateInstant.getValue(), liUser.getRegistrationInstantAsLithiumInstant().getValue());
    }

    @Test
    public void getAnonymousTest() {
        liUser.setAnonymous(ANONYMOUS);
        assertNotEquals(null, liUser.getAnonymous());
        assertEquals(ANONYMOUS, liUser.getAnonymous());
    }

    @Test
    public void jsonSerializeTest() {

        LiAvatar avatar = new LiAvatar();
        avatar.setMessage("AVATAR");
        liUser.setAvatarImage(avatarImage);
        liUser.setId(ID);
        liUser.setLogin(LI_LOGIN);
        liUser.setProfilePageUrl(PROFILE_PAGE_URL);
        liUser.setHref(HREF);
        String email = EMAIL;
        liUser.setEmail(email);
        liUser.setAvatar(avatar);
        Assert.assertEquals(
                "{\"USER_EMAIL\":\"lithium@lithium.com\",\"USER_LOGIN\":\"Li_Login\","
                        + "\"USER_VIEW_HREF\":\"http:\\/\\/community.lithium"
                        + ".com\\/t5\\/user\\/viewprofilepage\\/user-id\\/52295\",\"USER_ID\":\"123456\","
                        + "\"USER_AVATAR\":{\"MESSAGE_IMAGE_URL\":\"AVATAR\",\"PROFILE_IMAGE_URL\":\"null\"},"
                        + "\"USER_HREF\":\"\\/\\/messages\\/\\/213824\"}",
                liUser.serialize().toString());

    }

    @Test
    public void jsonDeserializeTest() throws JSONException {
        avatarImage.setUrl("Avatar image URL");
        LiAvatar avatar = new LiAvatar();
        avatar.setMessage("AVATAR");
        LiUser liUser = new LiUser();
        liUser.setAvatarImage(avatarImage);
        liUser.setId(ID);
        liUser.setLogin(LI_LOGIN);
        liUser.setProfilePageUrl(PROFILE_PAGE_URL);
        liUser.setHref(HREF);
        String email = EMAIL;
        liUser.setEmail(email);
        liUser.setAvatar(avatar);
        JSONObject jsonObject = liUser.serialize();
        LiUser liUser1 = liUser.deserialize(jsonObject);
        Assert.assertEquals(liUser.getId(), liUser1.getId());
        Assert.assertEquals(liUser.getProfilePageUrl(), liUser1.getProfilePageUrl());
        Assert.assertEquals(liUser.getHref(), liUser1.getHref());
        Assert.assertEquals(liUser.getEmail(), liUser1.getEmail());
        Assert.assertEquals(liUser.getAvatarImageUrl(), liUser1.getAvatarImageUrl());
        Assert.assertEquals(liUser.getLoginId(), liUser1.getLoginId());
        Assert.assertEquals(liUser.getEmail(), liUser1.getEmail());
    }
}
