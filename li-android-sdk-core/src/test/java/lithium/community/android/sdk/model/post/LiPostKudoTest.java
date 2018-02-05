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

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.response.LiMessage;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiPostKudoTest {

    private final String type = "kudo";
    private LiMessage liMessage = new LiMessage();
    private LiPostKudoModel kudoModel = new LiPostKudoModel();

    @Test
    public void getTypeTest() {
        kudoModel.setType(type);
        assertEquals(type, kudoModel.getType());
    }

    @Test
    public void getMessageTest() {
        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue((long) 100);
        liMessage.setId(liInt);
        kudoModel.setMessage(liMessage);
        assertEquals(liMessage, kudoModel.getMessage());
    }
}
