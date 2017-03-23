package lithium.community.android.sdk.model;

import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiImageMetaData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
   Created by mahaveer.udabal on 10/18/16.
 */

public class LiImageMetaDataTest {

    private final String FORMAT="JPEG";
    private final Long SIZE=10L;
    private LiImageMetaData liImageMetaData=new LiImageMetaData();

    @Test
    public void getFormatTest(){
        LiBaseModelImpl.LiString format=new LiBaseModelImpl.LiString();
        format.setValue(this.FORMAT);
        liImageMetaData.setFormat(format);
        assertEquals( FORMAT, liImageMetaData.getFormat() );
        assertTrue( liImageMetaData.getFormatAsLithiumString() instanceof LiBaseModelImpl.LiString );
        assertEquals( FORMAT, liImageMetaData.getFormatAsLithiumString().getValue() );
    }

    @Test
    public void getSizeTest() {
        LiBaseModelImpl.LiLong liInteger = new LiBaseModelImpl.LiLong();
        liInteger.setValue(this.SIZE);
        liImageMetaData.setSize(liInteger);
        assertEquals( this.SIZE, liImageMetaData.getSize() );
        assertTrue( liImageMetaData.getSizeAsLithiumLong()instanceof LiBaseModelImpl.LiLong );
        assertEquals( this.SIZE, liImageMetaData.getSizeAsLithiumLong().getValue() );
    }
}
