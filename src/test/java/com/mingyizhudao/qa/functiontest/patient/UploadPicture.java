package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TianJing on 2017/9/4.
 */
public class UploadPicture extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String url = "/api/v1/orders/{orderNumber}/uploadPicture";

    @Test
    public void test_01_上传图片(){

        String orderInfo = CreateAppointment.s_CreateOrder();
        String orderNumber = JSONObject.fromObject(orderInfo).getJSONObject("data").getString("order_number");
        logger.info("创建的orderNumber为：" + orderNumber);

        String res = "";
        AppointmentOrder ap = new AppointmentOrder("picture");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        logger.info("新增两张图片。。。");
        ap.setMedical_record_pictures(new ArrayList<AppointmentOrder.Picture>(){
            {
                add(ap.new Picture("2017/09/04/f6e46a2cb9624844877c61af6c698e87/测试1.jpg", "7"));
                add(ap.new Picture("2017/09/04/ae52e5e4da7b4a12bbca0d6840a9b8d1/测试2.jpg", "7"));
            }
        });
        res = HttpRequest.s_SendPut(host_patient + url, ap.transform(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures(0):key"), "2017/09/04/f6e46a2cb9624844877c61af6c698e87/测试1.jpg");
        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_record_pictures(0):url"), "没有图片URL");
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures(1):key"), "2017/09/04/ae52e5e4da7b4a12bbca0d6840a9b8d1/测试2.jpg");
        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_record_pictures(1):url"), "没有图片URL");

//        logger.info("再新增一张图片。。。");
//        ap.setMedical_record_pictures(new ArrayList<AppointmentOrder.Picture>(){
//            {
//                add(ap.new Picture("2017/09/04/f6e46a2cb9624844877c61af6c698e87/测试1.jpg", "7"));
//                add(ap.new Picture("2017/09/04/ae52e5e4da7b4a12bbca0d6840a9b8d1/测试2.jpg", "7"));
//                add(ap.new Picture("2017/09/04/79e9fc37553d4c8382c51567acad81fb/测试3.jpg", "7"));
//            }
//        });
//        res = HttpRequest.s_SendPut(host_patient + url, ap.transform(), "", pathValue);
//        s_CheckResponse(res);
//        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
//        logger.info("查看刚刚更新的订单详情");
//        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures(0):key"), "2017/09/04/ae52e5e4da7b4a12bbca0d6840a9b8d1/测试2.jpg");
//        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_record_pictures(0):url"), "没有图片URL");
//        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures(1):key"), "2017/09/04/ae52e5e4da7b4a12bbca0d6840a9b8d1/测试2.jpg");
//        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_record_pictures(1):url"), "没有图片URL");
//        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures(2):key"), "2017/09/04/79e9fc37553d4c8382c51567acad81fb/测试3.jpg");
//        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_record_pictures(2):url"), "没有图片URL");
    }

}
