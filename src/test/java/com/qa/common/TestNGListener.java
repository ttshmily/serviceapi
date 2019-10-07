package com.qa.common;

import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.*;

public class TestNGListener extends TestListenerAdapter {

    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        packageTrace(tr);
    }

    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        packageTrace(tr);
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult tr) {
        super.onTestFailedButWithinSuccessPercentage(tr);
        packageTrace(tr);
    }

    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
    }

    public void onFinish(ITestContext var1) {

    }

    /**
     * 打印网络传输内容
     * @param tr
     */
    @Attachment(value = "网络传输内容：")
    private byte[] packageTrace(ITestResult tr) {

        return FileToBytes(TestLogger.dir+tr.getTestClass().getRealClass().getCanonicalName()+".log", BaseTest.p);
    }

    /**
     * 文件转换为字节流
     * @param filePath 日志文件路径
     * @param position 从position的位置开始读取该测试用例的日志
     */
    private byte[] FileToBytes(String filePath, int position) {

        File logfile = new File(filePath);
        if (!logfile.exists()) return null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(logfile);
            byte[] b = new byte[fis.available()];

            while (fis.read(b) != -1) {
                bos.write(b, position, b.length-position);
            }
            fis.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打印测试结果
     * @param tr
     */
    @Attachment(value = "期望结果如下：")
    public String exceptedResult(ITestResult tr){
        String result = "显示查询结果";
        return result;
    }

}
