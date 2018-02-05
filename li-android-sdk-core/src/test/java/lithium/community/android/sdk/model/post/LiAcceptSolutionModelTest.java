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

package lithium.community.android.sdk.model.post;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiAcceptSolutionModelTest {

    private final String type = "accept_solution";
    private final String messageId = "100";

    private LiAcceptSolutionModel liAcceptSolutionModel = new LiAcceptSolutionModel();

    @Test
    public void getTypeTest() {
        liAcceptSolutionModel.setType(type);
        assertEquals(type, liAcceptSolutionModel.getType());
    }

    @Test
    public void getMessageIdTest() {
        liAcceptSolutionModel.setMessageid(messageId);
        assertEquals(messageId, liAcceptSolutionModel.getMessageid());
    }

    @Test
    public void testToJson() {
        liAcceptSolutionModel.setType(type);
        liAcceptSolutionModel.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}",
                liAcceptSolutionModel.toJson().toString());
    }

    @Test
    public void testToJsonString() {
        liAcceptSolutionModel.setType(type);
        liAcceptSolutionModel.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}",
                liAcceptSolutionModel.toJsonString().toString());
    }
}
