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
import lithium.community.android.sdk.model.helpers.LiBoard;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiPostMessageModelTest {

    private final String type = "post_question";
    private final String subject = "Test Question";
    private final String body = "This is test question";
    private LiBoard board = new LiBoard();
    private LiPostMessageModel liPostMessageModel = new LiPostMessageModel();

    @Test
    public void getTypeTest() {
        liPostMessageModel.setType(type);
        assertEquals(type, liPostMessageModel.getType());
    }

    @Test
    public void getSubjecTest() {
        liPostMessageModel.setSubject(subject);
        assertEquals(subject, liPostMessageModel.getSubject());
    }

    @Test
    public void getBodyTest() {
        liPostMessageModel.setBody(body);
        assertEquals(body, liPostMessageModel.getBody());
    }

    @Test
    public void getBoardTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue("Board Description");
        board.setDescription(liString);
        liPostMessageModel.setBoard(board);
        assertEquals(board, liPostMessageModel.getBoard());
    }
}
