package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.dataprofile.doctor.MedicalRecords;
import com.mingyizhudao.qa.dataprofile.doctor.OrderDetail;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.fail;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class UpdateMedicalRecords extends BaseTest {

    public static final Logger logger= Logger.getLogger(UpdateMedicalRecords.class);
    public static String uri = "/api/updatemedicalrecords/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    public static String UpdateMedicalRecords(String token, String orderId, HashMap<String, String> map) {
        String res = "";
        MedicalRecords mr = new MedicalRecords(true);
        for (String key:map.keySet()
             ) {
            if (mr.body.getJSONObject("order").containsKey(key)) {
                mr.body.getJSONObject("order").replace(key, map.get(key));
            } else {
                mr.body.getJSONObject("order").accumulate(key, map.get(key));
            }
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);

        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        String tmpOrderId = UT.parseJson(JSONObject.fromObject(res), "data:order_number");
        if (!tmpOrderId.isEmpty() && null != tmpOrderId) {
            logger.info("orderId是: " + tmpOrderId);
            return tmpOrderId;
        } else {
            logger.error("获取orderId失败");
            return "";
        }
    }

    @Test
    public void test_01_更新病例_主诉疾病() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String mdid = UT.parseJson(data, "order:major_disease_id");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新主诉疾病为-1");
        mr.body.getJSONObject("order").replace("major_disease_id", "-1");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        Assert.assertEquals(code, "2210416", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:major_disease_id"), mdid, "主诉疾病为-1，不应该更新成功");
        logger.info("禁止更新主诉疾病为-1");

        logger.info("更新主诉疾病为0");
        mr.body.replace("major_disease_id", "0");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        Assert.assertEquals(code, "2210416", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:major_disease_id"), mdid, "主诉疾病为0，不应该更新成功");
        logger.info("禁止更新主诉疾病为0");

        logger.info("更新主诉疾病为30000000000");
        mr.body.getJSONObject("order").replace("major_disease_id", "30000000000");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:major_disease_id"), mdid, "主诉疾病为30000000000，不应该更新成功");
        logger.info("禁止更新主诉疾病为30000000000");

        String key  = UT.randomKey(KB.kb_disease);
        logger.info("更新主诉疾病为"+key);
        mr.body.getJSONObject("order").replace("major_disease_id", key);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:major_disease_id"), key, "主诉疾病"+key+"，未更新成功");
        logger.info("更新主诉疾病为"+key+"成功");

        key  = UT.randomKey(KB.kb_disease);
        logger.info("更新主诉疾病为"+key);
        mr.body.getJSONObject("order").replace("major_disease_id", key);
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:major_disease_id"), key, "主诉疾病"+key+"，未更新成功");
        logger.info("更新主诉疾病为40成功");

    }

    @Test
    public void test_02_更新病例_次诉疾病() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String mdId = UT.parseJson(data, "order:minor_disease_id");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新次诉疾病为-1");
        mr.body.getJSONObject("order").replace("minor_disease_id", "-1");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:minor_disease_id"), mdId, "次诉疾病为-1，不应该更新成功");
        logger.info("禁止更新次诉疾病为-1");

        logger.info("更新次诉疾病为0");
        mr.body.getJSONObject("order").replace("minor_disease_id", "0");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:minor_disease_id"), mdId, "次诉疾病为0，不应该更新成功");
        logger.info("禁止更新次诉疾病为0");

        logger.info("更新次诉疾病为30000000000");
        mr.body.getJSONObject("order").replace("minor_disease_id", "30000000000");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:minor_disease_id"), mdId, "次诉疾病为30000000000，不应该更新成功");
        logger.info("禁止更新次诉疾病为30000000000");

        String key = UT.randomKey(KB.kb_disease);
        logger.info("更新次诉疾病为"+key);
        mr.body.getJSONObject("order").replace("minor_disease_id", key);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:minor_disease_id"), key, "次诉疾病"+key+"，未更新成功");
        logger.info("更新次诉疾病为"+key+"成功");

        key = UT.randomKey(KB.kb_disease);
        logger.info("更新次诉疾病为"+key);
        mr.body.getJSONObject("order").replace("minor_disease_id", key);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:minor_disease_id"), key, "次诉疾病"+key+"，未更新成功");
        logger.info("更新次诉疾病为40成功");

    }

    @Test
    public void test_03_更新病例_患者性别() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String gender = UT.parseJson(data, "order:patient_gender");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新性别为-1");
        mr.body.getJSONObject("order").replace("patient_gender", "-1");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_gender"), gender, "性别为-1，不应该更新成功");

        logger.info("更新性别为0");
        mr.body.getJSONObject("order").replace("patient_gender", "0");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_gender"), gender, "性别为0，不应该更新成功");


        logger.info("更新性别为3");
        mr.body.getJSONObject("order").replace("patient_gender", "3");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_gender"), gender, "性别为3，不应该更新成功");

        logger.info("更新性别为2");
        mr.body.getJSONObject("order").replace("patient_gender", "2");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_gender"), "2", "性别2未更新成功");

        logger.info("更新性别为1");
        mr.body.getJSONObject("order").replace("patient_gender", "1");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_gender"), "1", "性别1未更新成功");
    }

    @Test
    public void test_04_更新病例_病例描述() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String diag = UT.parseJson(data, "order:diagnosis");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新病例描述");
        mr.body.getJSONObject("order").replace("diagnosis", "思考房价的问题，关键在于逻辑。\n" +
                "但中国房地产市场最缺的恰恰是逻辑，人们对房价的判断，大多基于一种群羊效应，价格上涨的时候追涨，价格下跌的时候抛售。\n" +
                "以本轮房地产的周期为例，本轮房地产的反转行情最早始于2014年9月30日，在全国房价开始全面下跌之时，中央政府开始出台救市政策，继而在2015年“3.30”房地产政策回到2008年救市模式，在这种情况下，基于对中国房价走势与政策周期之间强烈的相关性，以及房地产市场基本面的判断，我们认为，在房地产基本面已经发生变化的情况下，一线城市、热点城市、大城市需求持续旺盛，其实不存在库存问题，在各种政策的红利下，我们认为买房的时间窗口已经打开，呼吁大家买房，甚至在一些文章中提出：“如果政府丧心病狂的救市，你一定要同样丧心病狂出手买房”的极端话语，但在市场低迷的情况下，无论你如何展示房价的逻辑，市场的参与者是不会轻易出手买房的。同样的道理，在市场陷入疯狂模式之后，动物精神远超人的理性，各种投机因素将使市场的表现大大超越任何原理和规律的范畴。这轮房价的上涨我们有预判，但上涨如此迅猛，持续的时间如此之长，甚至在去年“9.30”之后，在高层三令五申“房子是住的，不是炒的”基调下，一些热点城市仍然疯狂炒作，逼迫政策不断加码。但市场陷入房价不会下跌的幻觉的时候，人们只会看到眼前的疯狂，而看不到逻辑在悄然起作用。\n" +
                "我们讲中国房价的逻辑，有三个维度：一是政策的维度，中国房地产市场本质上政策市，市场周期受制于政策周期的影响，房价的表现也受制于政策的变化，在政策一松一紧之间，房价都会出现剧烈的反弹，中国的房地产政策，只有真假之分，没有见效不见效这一说，政策如果动真格，每次都是见效的，只是在下一次反弹的时候，很多人忘记了房价在政策的影响下曾经跌过；二是供需的维度，供需规律是决定中国房地产市场长期趋势的最基本逻辑。经过近20年的发展，住宅市场供需基本平衡，但因为中国大城市与其他城市之间的资源鸿沟，必然出现中小城市供应过度而大城市供应长期不足的矛盾，再加上中国大城市错误的限制人口，以及中国以中小城市为中心的错误的城镇化的方向，人为控制大城市的人口和建设用地，必然导致供需矛盾长期存在。我一直强调，库存不是中国房地产的真问题，真问题是资源错配，是土地制度和城市发展的大方向违背城镇化的基本规律，从而导致出现人为的短缺；三是价格的逻辑，房价的绝对值已经很高，这是不争的事实。价格尽管是一个历史和时空概念。在每一个时点上，衡量价格高低的标准是不一样的，美国房地产市场1940年以来，从长期趋势看，一直是上涨的，但几乎每十年都要经历一次调整。很多人现在经常以1989年人民日报一篇报道来嘲笑那些认为中国房价已经很高的人，当时房价只有1900元，大家惊呼房价太高。其实，在那个时点，按照各个元素分析，1900元一平米的房价的确很高，现在北京房价均价过了6万，如果按照人均收入，房价中位数，房价收入比，租售比等一系列的指标看，目前的房价不仅绝对值处于高位，而且按照很多指标来权衡，泡沫化程度已经很高。\n" +
                "基于以上三个逻辑来判断，我们认为，中国房地产市场正在出现前所未有的变化：政策为什么如此严厉？一轮又一轮的重拳在传导什么信号？北京为什么对商住房赶尽杀绝？厦门等热点城市为什么出现了“两年之内不能卖出”的限卖规定，一些城市在一个月内甚至不断出台加码的政策，如果对这一系列的信号仍然置若罔闻，一定会犯非常严重的错误。目前政策信号最清楚不过的表明，高层对房地产泡沫风险的担心超过了任何时候：包括张高丽副总理、周小川行长及发改委主任何立峰最近都公开表态，要警惕房地产泡沫风险。张高丽在“中国高层发展论坛”上表示，坚持“房子是用来住的，不是用来炒的”的定位，分类指导，因城施策，重点消化三四线城市房地产的过量库存。并把房地产泡沫列为今年主要的风险点；周小川最近表示，中国金融体系总体健康，但也存在总体杠杆率偏高、债市房市风险和跨市场影子银行业务活跃等风险。国家发改委主任何立峰在“中国高层发展论坛”上将房地产和实体经济的失衡列为当前中国经济发展面临三大结构性的失衡之一，指出“有大量资金涌入房地产市场，曾经一度带动了一线城市和热点二线城市的房价过快的上涨，进一步推高了实体经济发展的成本。”提出要控制信贷资金过度流向房地产业。高层如此密集的对房地产泡沫进行表态，极为罕见，后面透露出的政策信号不容小觑。而且我们看到，高层不仅仅在喊话，而且采取实质性的举措。央行之前加急下发的《做好信贷政策工作的意见》明确要求：人民银行各分支机构要加强对商业银行窗口指导，督促其优化信贷结构，合理控制房贷比和增速，有效防控信贷风险。收紧杠杆，断粮的决心非常坚决。\n" +
                "我之前讲过，不要低估市场的决心，不要漠视市场的变化，六月份房地产市场要变天，都是基于这个基本逻辑的判断。当房地产政策的目标从抑制房价过快上涨过度到防止房地产泡沫风险的时候，短期出现降温是必然的，在限购限贷等政策的举措下，一些热点城市价格出现调整也是大概率。尽管高层的意图是不想让房价兴风作浪，但也不希望房价出现明显的下跌，但这需要高超的政策智慧，至少，我们目前不具备这种智慧。\n" +
                "在市场逻辑出现变化的情况下，市场应该冷静，从狂躁回归理性，很显然，政策不欢迎投资性的需求，投资性的需求在目前的环境下入市面临各种风险。未来的市场怎么看，还是我上篇文章中提出的，回到房地产的基本逻辑分析：短期看政策，中期看城镇化进程，长期看人口转折点。就城镇化进程和人口而言，热点城市的房价可能会经历短期波动，但长期仍然具有向上的动力。但在调控周期下，市场安全的范围一定会继续收缩，我之前提出的绝大多数城市选择买房可以等到六月份市场和政策明朗之后再做决策。除了学区房，除了我之前讲过的核心城市的基本居住需求，现在你有足够的时间为你的决策进行慎重的思考。千万不要陷入市场永远上涨的幻觉。英国著名历史学家保罗.约翰逊的话今天仍然具有重大的价值和意义：\n" +
                "研究历史，是医治当代人傲慢气焰的一剂猛药。当我们发现我们原以为何等新奇、何等言之凿凿的肤浅论断，原来早已被人类付出的巨大代价不止一次地验证为彻底的错误时，尽管他们可能呈现出无数不同的伪装，我们才能感觉到羞愧而谦卑！\n" +
                "面对历史上哪些一再发生的泡沫灾难，中国人的确还需要一次真正的价格调整来学会谦卑！");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:diagnosis"), "思考房价的问题，关键在于逻辑。\n" +
                "但中国房地产市场最缺的恰恰是逻辑，人们对房价的判断，大多基于一种群羊效应，价格上涨的时候追涨，价格下跌的时候抛售。\n" +
                "以本轮房地产的周期为例，本轮房地产的反转行情最早始于2014年9月30日，在全国房价开始全面下跌之时，中央政府开始出台救市政策，继而在2015年“3.30”房地产政策回到2008年救市模式，在这种情况下，基于对中国房价走势与政策周期之间强烈的相关性，以及房地产市场基本面的判断，我们认为，在房地产基本面已经发生变化的情况下，一线城市、热点城市、大城市需求持续旺盛，其实不存在库存问题，在各种政策的红利下，我们认为买房的时间窗口已经打开，呼吁大家买房，甚至在一些文章中提出：“如果政府丧心病狂的救市，你一定要同样丧心病狂出手买房”的极端话语，但在市场低迷的情况下，无论你如何展示房价的逻辑，市场的参与者是不会轻易出手买房的。同样的道理，在市场陷入疯狂模式之后，动物精神远超人的理性，各种投机因素将使市场的表现大大超越任何原理和规律的范畴。这轮房价的上涨我们有预判，但上涨如此迅猛，持续的时间如此之长，甚至在去年“9.30”之后，在高层三令五申“房子是住的，不是炒的”基调下，一些热点城市仍然疯狂炒作，逼迫政策不断加码。但市场陷入房价不会下跌的幻觉的时候，人们只会看到眼前的疯狂，而看不到逻辑在悄然起作用。\n" +
                "我们讲中国房价的逻辑，有三个维度：一是政策的维度，中国房地产市场本质上政策市，市场周期受制于政策周期的影响，房价的表现也受制于政策的变化，在政策一松一紧之间，房价都会出现剧烈的反弹，中国的房地产政策，只有真假之分，没有见效不见效这一说，政策如果动真格，每次都是见效的，只是在下一次反弹的时候，很多人忘记了房价在政策的影响下曾经跌过；二是供需的维度，供需规律是决定中国房地产市场长期趋势的最基本逻辑。经过近20年的发展，住宅市场供需基本平衡，但因为中国大城市与其他城市之间的资源鸿沟，必然出现中小城市供应过度而大城市供应长期不足的矛盾，再加上中国大城市错误的限制人口，以及中国以中小城市为中心的错误的城镇化的方向，人为控制大城市的人口和建设用地，必然导致供需矛盾长期存在。我一直强调，库存不是中国房地产的真问题，真问题是资源错配，是土地制度和城市发展的大方向违背城镇化的基本规律，从而导致出现人为的短缺；三是价格的逻辑，房价的绝对值已经很高，这是不争的事实。价格尽管是一个历史和时空概念。在每一个时点上，衡量价格高低的标准是不一样的，美国房地产市场1940年以来，从长期趋势看，一直是上涨的，但几乎每十年都要经历一次调整。很多人现在经常以1989年人民日报一篇报道来嘲笑那些认为中国房价已经很高的人，当时房价只有1900元，大家惊呼房价太高。其实，在那个时点，按照各个元素分析，1900元一平米的房价的确很高，现在北京房价均价过了6万，如果按照人均收入，房价中位数，房价收入比，租售比等一系列的指标看，目前的房价不仅绝对值处于高位，而且按照很多指标来权衡，泡沫化程度已经很高。\n" +
                "基于以上三个逻辑来判断，我们认为，中国房地产市场正在出现前所未有的变化：政策为什么如此严厉？一轮又一轮的重拳在传导什么信号？北京为什么对商住房赶尽杀绝？厦门等热点城市为什么出现了“两年之内不能卖出”的限卖规定，一些城市在一个月内甚至不断出台加码的政策，如果对这一系列的信号仍然置若罔闻，一定会犯非常严重的错误。目前政策信号最清楚不过的表明，高层对房地产泡沫风险的担心超过了任何时候：包括张高丽副总理、周小川行长及发改委主任何立峰最近都公开表态，要警惕房地产泡沫风险。张高丽在“中国高层发展论坛”上表示，坚持“房子是用来住的，不是用来炒的”的定位，分类指导，因城施策，重点消化三四线城市房地产的过量库存。并把房地产泡沫列为今年主要的风险点；周小川最近表示，中国金融体系总体健康，但也存在总体杠杆率偏高、债市房市风险和跨市场影子银行业务活跃等风险。国家发改委主任何立峰在“中国高层发展论坛”上将房地产和实体经济的失衡列为当前中国经济发展面临三大结构性的失衡之一，指出“有大量资金涌入房地产市场，曾经一度带动了一线城市和热点二线城市的房价过快的上涨，进一步推高了实体经济发展的成本。”提出要控制信贷资金过度流向房地产业。高层如此密集的对房地产泡沫进行表态，极为罕见，后面透露出的政策信号不容小觑。而且我们看到，高层不仅仅在喊话，而且采取实质性的举措。央行之前加急下发的《做好信贷政策工作的意见》明确要求：人民银行各分支机构要加强对商业银行窗口指导，督促其优化信贷结构，合理控制房贷比和增速，有效防控信贷风险。收紧杠杆，断粮的决心非常坚决。\n" +
                "我之前讲过，不要低估市场的决心，不要漠视市场的变化，六月份房地产市场要变天，都是基于这个基本逻辑的判断。当房地产政策的目标从抑制房价过快上涨过度到防止房地产泡沫风险的时候，短期出现降温是必然的，在限购限贷等政策的举措下，一些热点城市价格出现调整也是大概率。尽管高层的意图是不想让房价兴风作浪，但也不希望房价出现明显的下跌，但这需要高超的政策智慧，至少，我们目前不具备这种智慧。\n" +
                "在市场逻辑出现变化的情况下，市场应该冷静，从狂躁回归理性，很显然，政策不欢迎投资性的需求，投资性的需求在目前的环境下入市面临各种风险。未来的市场怎么看，还是我上篇文章中提出的，回到房地产的基本逻辑分析：短期看政策，中期看城镇化进程，长期看人口转折点。就城镇化进程和人口而言，热点城市的房价可能会经历短期波动，但长期仍然具有向上的动力。但在调控周期下，市场安全的范围一定会继续收缩，我之前提出的绝大多数城市选择买房可以等到六月份市场和政策明朗之后再做决策。除了学区房，除了我之前讲过的核心城市的基本居住需求，现在你有足够的时间为你的决策进行慎重的思考。千万不要陷入市场永远上涨的幻觉。英国著名历史学家保罗.约翰逊的话今天仍然具有重大的价值和意义：\n" +
                "研究历史，是医治当代人傲慢气焰的一剂猛药。当我们发现我们原以为何等新奇、何等言之凿凿的肤浅论断，原来早已被人类付出的巨大代价不止一次地验证为彻底的错误时，尽管他们可能呈现出无数不同的伪装，我们才能感觉到羞愧而谦卑！\n" +
                "面对历史上哪些一再发生的泡沫灾难，中国人的确还需要一次真正的价格调整来学会谦卑！", "病例描述未更新成功");

        logger.info("更新病例描述");
        mr.body.getJSONObject("order").replace("diagnosis", "思考房价的问题，关键在于逻辑。\n" +
                "但中国房地产市场最缺的恰恰是逻辑，人们对房价的判断，大多基于一种群羊效应，价格上涨的时候追涨，价格下跌的时候抛售。\n" +
                "以本轮房地产的周期为例，本轮房地产的反转行情最早始于2014年9月30日，在全国房价开始全面下跌之时，中央政府开始出台救市政策，继而在2015年“3.30”房地产政策回到2008年救市模式，在这种情况下，基于对中国房价走势与政策周期之间强烈的相关性，以及房地产市场基本面的判断，我们认为，在房地产基本面已经发生变化的情况下，一线城市、热点城市、大城市需求持续旺盛，其实不存在库存问题，在各种政策的红利下，我们认为买房的时间窗口已经打开，呼吁大家买房，甚至在一些文章中提出：“如果政府丧心病狂的救市，你一定要同样丧心病狂出手买房”的极端话语，但在市场低迷的情况下，无论你如何展示房价的逻辑，市场的参与者是不会轻易出手买房的。同样的道理，在市场陷入疯狂模式之后，动物精神远超人的理性，各种投机因素将使市场的表现大大超越任何原理和规律的范畴。这轮房价的上涨我们有预判，但上涨如此迅猛，持续的时间如此之长，甚至在去年“9.30”之后，在高层三令五申“房子是住的，不是炒的”基调下，一些热点城市仍然疯狂炒作，逼迫政策不断加码。但市场陷入房价不会下跌的幻觉的时候，人们只会看到眼前的疯狂，而看不到逻辑在悄然起作用。\n" +
                "我们讲中国房价的逻辑，有三个维度：一是政策的维度，中国房地产市场本质上政策市，市场周期受制于政策周期的影响，房价的表现也受制于政策的变化，在政策一松一紧之间，房价都会出现剧烈的反弹，中国的房地产政策，只有真假之分，没有见效不见效这一说，政策如果动真格，每次都是见效的，只是在下一次反弹的时候，很多人忘记了房价在政策的影响下曾经跌过；二是供需的维度，供需规律是决定中国房地产市场长期趋势的最基本逻辑。经过近20年的发展，住宅市场供需基本平衡，但因为中国大城市与其他城市之间的资源鸿沟，必然出现中小城市供应过度而大城市供应长期不足的矛盾，再加上中国大城市错误的限制人口，以及中国以中小城市为中心的错误的城镇化的方向，人为控制大城市的人口和建设用地，必然导致供需矛盾长期存在。我一直强调，库存不是中国房地产的真问题，真问题是资源错配，是土地制度和城市发展的大方向违背城镇化的基本规律，从而导致出现人为的短缺；三是价格的逻辑，房价的绝对值已经很高，这是不争的事实。价格尽管是一个历史和时空概念。在每一个时点上，衡量价格高低的标准是不一样的，美国房地产市场1940年以来，从长期趋势看，一直是上涨的，但几乎每十年都要经历一次调整。很多人现在经常以1989年人民日报一篇报道来嘲笑那些认为中国房价已经很高的人，当时房价只有1900元，大家惊呼房价太高。其实，在那个时点，按照各个元素分析，1900元一平米的房价的确很高，现在北京房价均价过了6万，如果按照人均收入，房价中位数，房价收入比，租售比等一系列的指标看，目前的房价不仅绝对值处于高位，而且按照很多指标来权衡，泡沫化程度已经很高。");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:diagnosis"), "思考房价的问题，关键在于逻辑。\n" +
                "但中国房地产市场最缺的恰恰是逻辑，人们对房价的判断，大多基于一种群羊效应，价格上涨的时候追涨，价格下跌的时候抛售。\n" +
                "以本轮房地产的周期为例，本轮房地产的反转行情最早始于2014年9月30日，在全国房价开始全面下跌之时，中央政府开始出台救市政策，继而在2015年“3.30”房地产政策回到2008年救市模式，在这种情况下，基于对中国房价走势与政策周期之间强烈的相关性，以及房地产市场基本面的判断，我们认为，在房地产基本面已经发生变化的情况下，一线城市、热点城市、大城市需求持续旺盛，其实不存在库存问题，在各种政策的红利下，我们认为买房的时间窗口已经打开，呼吁大家买房，甚至在一些文章中提出：“如果政府丧心病狂的救市，你一定要同样丧心病狂出手买房”的极端话语，但在市场低迷的情况下，无论你如何展示房价的逻辑，市场的参与者是不会轻易出手买房的。同样的道理，在市场陷入疯狂模式之后，动物精神远超人的理性，各种投机因素将使市场的表现大大超越任何原理和规律的范畴。这轮房价的上涨我们有预判，但上涨如此迅猛，持续的时间如此之长，甚至在去年“9.30”之后，在高层三令五申“房子是住的，不是炒的”基调下，一些热点城市仍然疯狂炒作，逼迫政策不断加码。但市场陷入房价不会下跌的幻觉的时候，人们只会看到眼前的疯狂，而看不到逻辑在悄然起作用。\n" +
                "我们讲中国房价的逻辑，有三个维度：一是政策的维度，中国房地产市场本质上政策市，市场周期受制于政策周期的影响，房价的表现也受制于政策的变化，在政策一松一紧之间，房价都会出现剧烈的反弹，中国的房地产政策，只有真假之分，没有见效不见效这一说，政策如果动真格，每次都是见效的，只是在下一次反弹的时候，很多人忘记了房价在政策的影响下曾经跌过；二是供需的维度，供需规律是决定中国房地产市场长期趋势的最基本逻辑。经过近20年的发展，住宅市场供需基本平衡，但因为中国大城市与其他城市之间的资源鸿沟，必然出现中小城市供应过度而大城市供应长期不足的矛盾，再加上中国大城市错误的限制人口，以及中国以中小城市为中心的错误的城镇化的方向，人为控制大城市的人口和建设用地，必然导致供需矛盾长期存在。我一直强调，库存不是中国房地产的真问题，真问题是资源错配，是土地制度和城市发展的大方向违背城镇化的基本规律，从而导致出现人为的短缺；三是价格的逻辑，房价的绝对值已经很高，这是不争的事实。价格尽管是一个历史和时空概念。在每一个时点上，衡量价格高低的标准是不一样的，美国房地产市场1940年以来，从长期趋势看，一直是上涨的，但几乎每十年都要经历一次调整。很多人现在经常以1989年人民日报一篇报道来嘲笑那些认为中国房价已经很高的人，当时房价只有1900元，大家惊呼房价太高。其实，在那个时点，按照各个元素分析，1900元一平米的房价的确很高，现在北京房价均价过了6万，如果按照人均收入，房价中位数，房价收入比，租售比等一系列的指标看，目前的房价不仅绝对值处于高位，而且按照很多指标来权衡，泡沫化程度已经很高。", "病例描述未更新成功");

    }

    @Test
    public void test_05_更新病例_更新图片() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
//        String diag = parseJson(data, "order:medical_record_pictures()");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);


        logger.info("新增一张图片。。。");
        MedicalRecords mr = new MedicalRecords(true); //默认的record中已经新增了一张
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(0):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(1):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(1):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(2):key"), "2017/05/04/4ab279ba-4626-4491-abee-25029d2341d6/WechatIMG560.jpeg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(2):url"), "没有图片URL");

        logger.info("再新增一张图片。。。");
        mr.body.getJSONObject("order").getJSONArray("medical_record_pictures").add(0, JSONObject.fromObject("{'key':'abc';'type':'1'}"));
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(0):key"), "abc");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(0):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(1):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(1):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(2):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(2):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(3):key"), "2017/05/04/4ab279ba-4626-4491-abee-25029d2341d6/WechatIMG560.jpeg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(3):url"), "没有图片URL");

        logger.info("删除二张图片。。。");
        mr.body.getJSONObject("order").getJSONArray("medical_record_pictures").remove(3);
        mr.body.getJSONObject("order").getJSONArray("medical_record_pictures").remove(0);
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(0):url"), "没有图片URL");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(1):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(UT.parseJson(data, "order:medical_record_pictures(1):url"), "没有图片URL");

        logger.info("删除所有图片。。。");
        mr.body.getJSONObject("order").getJSONArray("medical_record_pictures").clear();
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures()"), "0");
    }

    @Test
    public void test_06_更新病例_期望手术医院() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            Assert.fail("创建订单失败");
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String expectedSurgeryHospitalId = UT.parseJson(data, "order:expected_surgery_hospital_id");
        String expectedSurgeryHospitalName = UT.parseJson(data, "order:expected_surgery_hospital_name");
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新期望手术医院ID为空");
        mr.body.getJSONObject("order").replace("expected_surgery_hospital_id", "");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_id"), expectedSurgeryHospitalId, "期望手术医院未更新成功");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_name"), expectedSurgeryHospitalName, "期望手术医院未更新成功");

        logger.info("更新期望手术医院ID=0");
        mr.body.getJSONObject("order").replace("expected_surgery_hospital_id", "");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_id"), expectedSurgeryHospitalId, "期望手术医院未更新成功");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_name"), expectedSurgeryHospitalName, "期望手术医院未更新成功");

        String key = UT.randomKey(KB.kb_hospital);
        logger.info("更新期望手术医院ID="+key);
        mr.body.getJSONObject("order").replace("expected_surgery_hospital_id", key);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_id"), key, "期望手术医院未更新成功");
        Assert.assertEquals(UT.parseJson(data, "order:expected_surgery_hospital_name"), KB.kb_hospital.get(key), "期望手术医院未更新成功");
    }

    @Test
    public void test_07_更新病例_患者姓名() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String name = UT.parseJson(data, "order:patient_name");
        MedicalRecords mr = new MedicalRecords(true);

        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);

        logger.info("姓名为中文");
        mr.body.getJSONObject("order").replace("patient_name", "大头猪");
        try {
            res = HttpRequest.sendPut(host_doc +mock+uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新订单失败");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_name"), "大头猪", "patient_name未更新");

        logger.info("姓名为中文字母组合");
        mr.body.getJSONObject("order").replace("patient_name", "方超xyz");
        try {
            res = HttpRequest.sendPut(host_doc +mock+uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新订单失败");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_name"), "方超xyz", "patient_name未更新");

        logger.info("姓名为长字符：大于前端控制");
        mr.body.getJSONObject("order").replace("patient_name", "方超xyz惺惺惜惺惺想寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻");
        try {
            res = HttpRequest.sendPut(host_doc +mock+uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新订单失败");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_name"), "方超xyz惺惺惜惺惺想寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻", "patient_name未更新");

    }

    @Test
    public void test_08_更新病例_患者年龄() {

        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        String age = UT.parseJson(data, "order:patient_age");

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        MedicalRecords mr = new MedicalRecords(true);

        logger.info("更新年龄为-1");
        mr.body.getJSONObject("order").replace("patient_age", "-1");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_age"), age, "年龄为-1，不应该更新成功");
        logger.info("禁止更新年龄为-1");

        logger.info("更新年龄为0");
        mr.body.getJSONObject("order").replace("patient_age", "0");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_age"), age, "年龄为0，不应该更新成功");
        logger.info("禁止更新年龄为0");

        logger.info("更新年龄为:1000000009999999999900000000000000000000000000000000000000");
        mr.body.getJSONObject("order").replace("patient_age", "1000000009999999999900000000000000000000000000000000000000");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "更新异常数据成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_age"), age, "年龄为1000000009999999999900000000000000000000000000000000000000，不应该更新成功");
        logger.info("禁止更新年龄为:1000000009999999999900000000000000000000000000000000000000");

        logger.info("更新年龄为103。。。");
        mr.body.getJSONObject("order").replace("patient_age", "103");
        try {
            res = HttpRequest.sendPut(host_doc + mock + uri, mr.body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "更新正常数据未成功");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_age"), "103", "年龄不为103，更新未成功");
        logger.info("更新年龄为103成功");
    }

    // 医生端2.1.1
    @Test
    public void test_09_仅仅更新病例图片() {
        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken, new OrderDetail(true));
        if (orderId.isEmpty()) {
            logger.error("创建订单失败");
            fail();
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        logger.info("添加病例图片。");
        JSONObject mr = new JSONObject();
        JSONObject order  = new JSONObject();
        order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'1'}"));
        order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg';'type':'1'}"));
        mr.put("order", order);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "仅仅更新图片信息失败");
        logger.info("查看刚刚更新的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertEquals(UT.parseJson(data, "order:medical_record_pictures(1):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg");
    }

    @Test
    public void test_10_更新二次手术字段() {
        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken, new OrderDetail(true));
        if (orderId.isEmpty()) {
            Assert.fail("创建订单失败，退出执行");
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        JSONObject mr = new JSONObject();
        JSONObject order  = new JSONObject();
        order.put("is_reoperation", "0");
        mr.put("order", order);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:is_reoperation"), "false");

        order.put("is_reoperation", "1");
        mr.replace("order", order);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:is_reoperation"), "true");
    }

    @Test
    public void test_11_更新患者电话字段() {
        String res = "";
        logger.info("创建一个新订单");
        String orderId = CreateOrder.CreateOrder(mainToken, new OrderDetail(true));
        if (orderId.isEmpty()) {
            Assert.fail("创建订单失败，退出执行");
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        JSONObject mr = new JSONObject();
        JSONObject order  = new JSONObject();
        order.put("patient_phone", "13899991234");
        mr.put("order", order);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_phone"), "13899991234");

        order.put("patient_phone", "13899991235");
        mr.replace("order", order);
        try {
            res = HttpRequest.sendPut(host_doc + uri, mr.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "查看订单失败");
        Assert.assertEquals(UT.parseJson(data, "order:patient_phone"), "13899991235");
    }
}
