package com.lithium.community.android.model.post;

import com.lithium.community.android.utils.LiQueryConstant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LiSubscriptionPostModelTest {

    @Test
    public void testBoardTarget(){
        String boardId = "board:test";
        LiSubscriptionPostModel.BoardTarget target = new LiSubscriptionPostModel.BoardTarget(boardId);
        assertEquals("board", target.getType());
        assertEquals(boardId, target.getId());
    }

    @Test
    public void testMessageTarget(){
        String messageId = "message:test";
        LiSubscriptionPostModel.MessageTarget target = new LiSubscriptionPostModel.MessageTarget(messageId);
        assertEquals("message", target.getType());
        assertEquals(messageId, target.getId());
    }

    @Test
    public void testModel(){
        String messageId = "message:test";
        LiSubscriptionPostModel.Target target = new LiSubscriptionPostModel.MessageTarget(messageId);
        assertTrue(target instanceof LiSubscriptionPostModel.MessageTarget);
        LiSubscriptionPostModel postModel = new LiSubscriptionPostModel(target);
        assertNotNull(postModel);
        assertEquals(postModel.getType(), LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE);
        assertNotNull(postModel.getTarget());
        assertNotNull(postModel.getTarget().getId());
        assertEquals(postModel.getTarget().getId(), messageId);
    }
}
