package com.qa.framework;

import io.qameta.allure.Step;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

@Listeners({TestNGListener.class})
public class BaseTest {

//    public static void main(String[] args) {
//        System.out.println("i am baseTest");
//    }

    static {
        PropertyConfigurator.configure(BaseTest.class.getClassLoader().getResource("log4j.properties"));
//        System.out.println("i am baseTest static" + BaseTest.class.getClassLoader().getResource("log4j.properties").toString());
    }

    public static Integer p = 0; // Record the STOP position of completed test case

//    @BeforeTest
//    public void setupTest(){
//
//    }
//
//    @AfterTest
//    public void cleanTest(){
//
//    }

    @BeforeClass
    public void setupClass(){
        String job = this.getClass().getCanonicalName();
        info(job, ">>> Begin test on CLASS " + job);
        p = 0;
    }

    @AfterClass
    public void tearDownClass(){
        String job = this.getClass().getCanonicalName();
        info(job, ">>> Complete test on CLASS " + job);
    }

    @BeforeMethod
    public void setupMethod(Method method){
        String job = this.getClass().getCanonicalName();
        info(job, ">>> Begin test on METHOD " + method.getName());
    }

    @AfterMethod
    public void tearDownMethod(Method method) {
        String job = this.getClass().getCanonicalName();
        info(job, ">>> Complete test on METHOD " + method.getName());
        p=logSize();

    }

    @Step(value = "{1}")
    public void debug(String jobName, Object msg) {
        TestLogger.info(jobName, msg.toString());
    }

    @Step(value = "{1}")
    public void info(String jobName, Object msg) {
        TestLogger.info(jobName, msg.toString());
    }

    private int logSize() {

        String job = this.getClass().getCanonicalName();
        int n = 0;
        File logfile = new File(TestLogger.dir, job + ".log");
        if (logfile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(logfile);
                n = fis.available();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return n;
    }
}
