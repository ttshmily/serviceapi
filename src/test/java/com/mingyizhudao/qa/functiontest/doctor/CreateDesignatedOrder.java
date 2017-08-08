package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import org.testng.annotations.Test;

/**
 * Created by TianJing on 2017/8/7.
 */
public class CreateDesignatedOrder extends BaseTest{
   @Test
    public void test_指定医生创建订单(){
       CreateOrder.s_CreateOrder(designatedDoctor_token);
   }
}
