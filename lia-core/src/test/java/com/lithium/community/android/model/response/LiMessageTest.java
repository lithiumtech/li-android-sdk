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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiTargetModel;
import com.lithium.community.android.TestHelper;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiRestv2Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class LiMessageTest {

    Gson gson;

    @Before
    public void setUp() throws LiRestResponseException, LiInitializationException {
        LiSDKManager.initialize(TestHelper.createMockContext(), TestHelper.getTestAppCredentials());
        gson = LiRestv2Client.getInstance().getGson();
    }

    @Test
    public void testMessageSerialization() {
        try {

            JsonObject jsonObject = gson.fromJson("{\n" +
                    "    \"status\": \"success\",\n" +
                    "    \"message\": \"\",\n" +
                    "    \"http_code\": 200,\n" +
                    "    \"data\": {\n" +
                    "        \"type\": \"messages\",\n" +
                    "        \"list_item_type\": \"message\",\n" +
                    "        \"size\": 1,\n" +
                    "        \"items\": [\n" +
                    "            {\n" +
                    "                \"type\": \"message\",\n" +
                    "                \"id\": \"213824\",\n" +
                    "                \"href\": \"/messages/213824\",\n" +
                    "                \"view_href\": \"http://community.lithium"
                    + ".com/t5/LiNC-16-Frequently-Asked/Who-do-I-contact-if-I-need-help-with-my-LiNC-16-conference/ta"
                    + "-p/213824\",\n"
                    +
                    "                \"author\": {\n" +
                    "                    \"type\": \"user\",\n" +
                    "                    \"id\": \"52295\",\n" +
                    "                    \"href\": \"/users/52295\",\n" +
                    "                    \"view_href\": \"http://community.lithium"
                    + ".com/t5/user/viewprofilepage/user-id/52295\",\n"
                    +
                    "                    \"login\": \"JennC\"\n" +
                    "                },\n" +
                    "                \"subject\": \"Who do I contact if I need help with my LiNC'16 conference "
                    + "registration?\",\n"
                    +
                    "                \"search_snippet\": \"Whether you need help with an existing registration,&nbsp;"
                    + "need to have it transferred,&nbsp;canceled&nbsp;or have questions about the registration "
                    + "process, the LiNC'16 event team is here to help you....\",\n"
                    +
                    "                \"body\": \"<DIV class=\\\"lia-message-template-content-zone\\\"><P>Whether you "
                    + "need help with an existing registration,&nbsp;need to have it transferred,&nbsp;canceled&nbsp;"
                    + "or have questions about the registration process, the LiNC'16 event team is here to help you"
                    + ".</P>\\n<P>&nbsp;</P>\\n<P><STRONG>Cancellation Policy:</STRONG> If you must cancel for any "
                    + "reason, you must notify us in writing by May 4, 2016, for a refund less a $100 processing fee."
                    + " Cancellations must be in writing, by fax or email. Cancellations within 30 days of the event "
                    + "are non-refundable, thus confirmed, and paid attendees who do not attend or who cancel after "
                    + "the deadline are liable for the entire fee.&nbsp;</P>\\n<P><STRONG>&nbsp;"
                    + "</STRONG></P>\\n<P><STRONG>Transfer Policy:</STRONG> You may transfer your registration to "
                    + "another person until May 26, 2016 by providing authorization to us at&nbsp;<A "
                    + "href=\\\"mailto:LiNC2016@lithiumevents.com\\\" rel=\\\"nofollow noopener "
                    + "noreferrer\\\">LiNC2016@lithiumevents.com</A>. In the unlikely event of cancellation of the "
                    + "conference, the liability of Lithium Technologies, Inc. is limited to the return of paid "
                    + "registration fees.</P>\\n<P>&nbsp;</P>\\n<P>Still Need Help?</P>\\n<P>Telephone:<BR /> (415) "
                    + "446-7733&nbsp;International<BR /> (800) 719-3412&nbsp;Toll free<BR /> <BR /> Fax:<BR /> (415) "
                    + "499-7979&nbsp;U.S.<BR /> <BR /> Email:<BR /> <A href=\\\"mailto:LiNC2016@lithiumevents.com\\\""
                    + " rel=\\\"nofollow noopener noreferrer\\\">LiNC2016@lithiumevents.com</A></P>\\n<P><BR /> "
                    + "Hours:<BR /> 8:00AM - 5:00PM PST</P>\\n<P>&nbsp;</P>\\n<P>For general questions about the "
                    + "event itself, please post in the&nbsp;<A href=\\\"http://community.lithium"
                    + ".com/t5/LiNC-16-Event-Group/gp-p/LiNC16\\\">LiNC'16 event group</A>.</P>\\n</DIV>\",\n"
                    +
                    "                \"teaser\": \"\",\n" +
                    "                \"board\": {\n" +
                    "                    \"type\": \"board\",\n" +
                    "                    \"id\": \"LiNC@tkb\",\n" +
                    "                    \"href\": \"/boards/LiNC@tkb\",\n" +
                    "                    \"view_href\": \"http://community.lithium"
                    + ".com/t5/LiNC-16-Frequently-Asked/tkb-p/LiNC%40tkb\"\n"
                    +
                    "                },\n" +
                    "                \"conversation\": {\n" +
                    "                    \"type\": \"conversation\",\n" +
                    "                    \"id\": \"213824\",\n" +
                    "                    \"view_href\": \"http://community.lithium"
                    + ".com/t5/LiNC-16-Frequently-Asked/Who-do-I-contact-if-I-need-help-with-my-LiNC-16-conference/ta"
                    + "-p/213824\",\n"
                    +
                    "                    \"style\": \"tkb\",\n" +
                    "                    \"messages_count\": 1,\n" +
                    "                    \"solved\": false,\n" +
                    "                    \"last_post_time\": \"2016-01-29T14:28:49.638-08:00\"\n" +
                    "                },\n" +
                    "                \"topic\": {\n" +
                    "                    \"type\": \"message\",\n" +
                    "                    \"id\": \"213824\",\n" +
                    "                    \"href\": \"/messages/213824\",\n" +
                    "                    \"view_href\": \"http://community.lithium"
                    + ".com/t5/LiNC-16-Frequently-Asked/Who-do-I-contact-if-I-need-help-with-my-LiNC-16-conference/ta"
                    + "-p/213824\"\n"
                    +
                    "                },\n" +
                    "                \"post_time\": \"2016-01-29T14:28:49.638-08:00\",\n" +
                    "                \"depth\": 0,\n" +
                    "                \"read_only\": false,\n" +
                    "                \"language\": \"EN\",\n" +
                    "                \"can_accept_solution\": false,\n" +
                    "                \"placeholder\": false,\n" +
                    "                \"is_solution\": false,\n" +
                    "                \"solution_data\": {},\n" +
                    "                \"metrics\": {\n" +
                    "                    \"type\": \"message_metrics\",\n" +
                    "                    \"views\": 2777\n" +
                    "                },\n" +
                    "                \"current_revision\": {\n" +
                    "                    \"type\": \"revision\",\n" +
                    "                    \"last_edit_author\": {\n" +
                    "                        \"type\": \"user\",\n" +
                    "                        \"id\": \"52295\",\n" +
                    "                        \"href\": \"/users/52295\",\n" +
                    "                        \"view_href\": \"http://community.lithium"
                    + ".com/t5/user/viewprofilepage/user-id/52295\",\n"
                    +
                    "                        \"login\": \"JennC\"\n" +
                    "                    },\n" +
                    "                    \"last_edit_time\": \"2016-01-29T14:29:30.492-08:00\"\n" +
                    "                },\n" +
                    "                \"kudos\": {\n" +
                    "                    \"query\": \"SELECT * FROM kudos WHERE message.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"tags\": {\n" +
                    "                    \"query\": \"SELECT * FROM tags WHERE messages.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"me_toos\": {\n" +
                    "                    \"query\": \"SELECT * FROM me_toos WHERE message.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"tkb_helpfulness_ratings\": {\n" +
                    "                    \"query\": \"SELECT * FROM tkb_helpfulness_ratings WHERE message.id = "
                    + "'213824'\"\n"
                    +
                    "                },\n" +
                    "                \"labels\": {\n" +
                    "                    \"query\": \"SELECT * FROM labels WHERE messages.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"images\": {\n" +
                    "                    \"query\": \"SELECT * FROM images WHERE messages.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"videos\": {\n" +
                    "                    \"query\": \"SELECT * FROM videos WHERE messages.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"attachments\": {\n" +
                    "                    \"query\": \"SELECT * FROM attachments WHERE message.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"replies\": {\n" +
                    "                    \"query\": \"SELECT * FROM messages WHERE parent.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"ratings\": {\n" +
                    "                    \"query\": \"SELECT * FROM ratings WHERE message.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"custom_tags\": {\n" +
                    "                    \"query\": \"SELECT * FROM custom_tags WHERE messages.id = '213824'\"\n" +
                    "                },\n" +
                    "                \"moderation_status\": \"approved\",\n" +
                    "                \"user_context\": {\n" +
                    "                    \"type\": \"user_context\",\n" +
                    "                    \"kudo\": false,\n" +
                    "                    \"me_too\": false,\n" +
                    "                    \"read\": false\n" +
                    "                },\n" +
                    "                \"device_id\": \"google_chrome_47\",\n" +
                    "                \"contributors\": {\n" +
                    "                    \"query\": \"SELECT * FROM users WHERE messages_contributed.id = '213824'\"\n"
                    +
                    "                },\n" +
                    "                \"helpfulness_allowed\": false,\n" +
                    "                \"popularity\": -5838.285729444445,\n" +
                    "                \"excluded_from_kudos_leaderboards\": false\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    \"metadata\": {}\n" +
                    "}", JsonObject.class);
            LiBaseResponse baseResponse = new LiBaseResponse();
            baseResponse.setData(jsonObject);
            List<LiBaseModel> responseList = (List<LiBaseModel>) baseResponse.toEntityList("message", LiMessage.class,
                    gson);
            assertEquals(1, responseList.size());
            LiMessage returnedMessage = ((LiTargetModel) responseList.get(0)).getLiMessage();
            assertEquals(213824, returnedMessage.getId().intValue());
            assertEquals(52295, returnedMessage.getAuthor().getId().intValue());
            assertEquals("LiNC@tkb", returnedMessage.getBoard().getId());
            assertEquals(2777, returnedMessage.getMessageMetrics().getViewCount().intValue());
            assertTrue(returnedMessage.getSubject().contains("Who do I contact if I need help with"));
            assertEquals("conversation", returnedMessage.getLiConversation().getType());
            assertEquals(false, returnedMessage.getUserContext().getKudo());
            assertEquals("2777", returnedMessage.getMessageMetrics().getViewCount().toString());
            assertEquals(
                    "http://community.lithium.com/t5/LiNC-16-Frequently-Asked/Who-do-I-contact-if-I-need-help-with-my"
                            + "-LiNC-16-conference/ta-p/213824",
                    returnedMessage.getViewHref());
            assertEquals(false, returnedMessage.isAcceptedSolution());
            assertEquals(false, returnedMessage.getCanAcceptSolution());

        } catch (LiRestResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMessageActivityQueryResponse() {
        try {
            JsonObject jsonObject = gson.fromJson("{\n" +
                    "    \"status\": \"success\",\n" +
                    "    \"message\": \"\",\n" +
                    "    \"http_code\": 200,\n" +
                    "    \"data\": {\n" +
                    "        \"type\": \"messages\",\n" +
                    "        \"list_item_type\": \"message\",\n" +
                    "        \"size\": 1,\n" +
                    "        \"items\": [\n" +
                    "            {\n" +
                    "                \"type\": \"message\",\n" +
                    "                \"id\": \"213824\",\n" +
                    "                \"kudos\": {\n" +
                    "                    \"sum\": {\n" +
                    "                        \"weight\": 0\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    \"metadata\": {}\n" +
                    "}", JsonObject.class);

            LiBaseResponse baseResponse = new LiBaseResponse();
            baseResponse.setData(jsonObject);
            List<LiBaseModel> responseList = baseResponse.toEntityList("message", LiMessage.class, gson);
            assertEquals(1, responseList.size());
            LiMessage returnedMessage = (LiMessage) responseList.get(0);
            assertEquals(213824, returnedMessage.getId().intValue());
            assertEquals(0, returnedMessage.getKudoMetrics().getSum().getWeight().getValue().intValue());
        } catch (LiRestResponseException e) {
            fail(e.getMessage());
        }
    }
}
