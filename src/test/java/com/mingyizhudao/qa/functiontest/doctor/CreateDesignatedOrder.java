package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by TianJing on 2017/8/7.
 */
public class CreateDesignatedOrder extends BaseTest{
   @Test
    public void test_指定医生创建订单(){
       CreateOrder.s_CreateOrder(designatedDoctor_token);
   }

    @Test(groups = {"pending"})
    public void test_01_指定医生创建待处理的订单(){
        int initNum = 1;
        int expectNum = Integer.parseInt(designatedOrderNumber);
        while (initNum <= expectNum){
            String orderId = CreateOrder.s_CreateOrder(designatedDoctor_token);
            logger.info("orderNumber:" + orderId);
            initNum++;
        }
    }

    @Test(groups = {"pending"})
    public void test_02_指定医生创建订单_客服领取未推荐专家的医生(){
        int initNum = 1;
        int expectNum = Integer.parseInt(designatedOrderNumber);
        while (initNum <= expectNum){
            String orderId = CreateOrder.s_CreateOrder(designatedDoctor_token);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            logger.info("orderNumber:" + orderId);
            initNum++;
        }
    }

    @Test
    public void test_03_指定医生创建订单_推荐指定的专家(){
        int initNum = 1;
        int expectNum = Integer.parseInt(designatedOrderNumber);
        while (initNum <= expectNum){
            String orderId = CreateOrder.s_CreateOrder(designatedDoctor_token);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId, designatedExceptDoctor);
            logger.info("orderNumber:" + orderId);
            initNum++;

        }
    }

    @Test
    public void test_04_指定医生创建待支付预约金订单_推荐指定的专家_确认合作(){
        int initNum = 1;
        int expectNum = Integer.parseInt(designatedOrderNumber);
        while (initNum <= expectNum){
            String orderId = CreateOrder.s_CreateOrder(designatedDoctor_token);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId, designatedExceptDoctor);
            Order_ThreewayCall_V2.s_CallV2(orderId,"success");
            initNum++;
        }
    }

    @Test
    public void test_05_创建认证后的医生(){
        int initNum = 1;
        int expectNum = Integer.parseInt(designatedOrderNumber);
        while (initNum <= expectNum){
            mainUser = new User();
            mainUser.getDoctor().setHospital_id("57");//北京大学口腔医院, 北京，区域服务人员 - 方超
            HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
            if(mainDoctorInfo == null) {
                logger.error("创建注册专家失败，退出执行");
                System.exit(10000);
            }
            logger.info("医生姓名为：" + mainUser.getDoctor().getName());
            initNum++;
        }
    }
}
