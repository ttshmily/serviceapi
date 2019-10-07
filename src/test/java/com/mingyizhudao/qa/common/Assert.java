package common;

import io.qameta.allure.Step;

public class Assert extends org.testng.Assert {


    @Step("验证：{2}")
    public static void assertEquals(String var0, String var1, String var2) {
        org.testng.Assert.assertEquals(var0, var1, var2);
    }

    @Step("验证：{2}")
    public static void assertEquals(int var0, int var1, String var2) {
        org.testng.Assert.assertEquals(var0, var1, var2);
    }

    @Step("验证：{2}")
    public static void assertEquals(double var0, double var1, String var2) {
        org.testng.Assert.assertEquals(var0, var1, var2);
    }

    @Step("验证：{1}")
    public static void assertNotNull(Object var0, String var1) {
        org.testng.Assert.assertNotNull(var0, var1);
    }

    @Step("验证非空")
    public static void assertNotNull(Object var0) {
        org.testng.Assert.assertNotNull(var0);
    }

    @Step("验证：{1}")
    public static void assertNull(Object var0, String var1) {
        org.testng.Assert.assertNull(var0, var1);
    }

    @Step("验证为空")
    public static void assertNull(Object var0) {
        org.testng.Assert.assertNull(var0);
    }

    @Step("验证：{1}")
    public static void assertTrue(boolean var0, String s) {
        org.testng.Assert.assertTrue(var0, s);
    }

    @Step("验证：{1}")
    public static void assertFalse(boolean var0, String s) {
        org.testng.Assert.assertFalse(var0, s);
    }

}
