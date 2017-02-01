package lithium.community.android.sdk.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiKudoMetricesTest {

    private LiBaseModelImpl.LiSum sum;

    private LiKudoMetrics liKudoMetrics = new LiKudoMetrics();

    @Test
    public void getParamsTest(){

        sum = new LiBaseModelImpl.LiSum();
        LiBaseModelImpl.LiInt value = new LiBaseModelImpl.LiInt();
        value.setValue(100L);
        sum.setWeight(value);

        liKudoMetrics.setSum(sum);

        assertEquals(value, liKudoMetrics.getSum().getWeight() );

    }
}
