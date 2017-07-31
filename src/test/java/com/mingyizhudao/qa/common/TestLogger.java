package com.mingyizhudao.qa.common;

import org.testng.Reporter;
import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * custom logger class to create separate log files based on test name
 */
public class TestLogger
{
    private static Map testLoggers = new ConcurrentHashMap<String,Logger>();
    private static String dir = "logs"; // Root log directory
    private static Layout layout;
    private static Logger rootLogger = Logger.getRootLogger();
    private String jobName;

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public TestLogger() {
        this.jobName = "Default";
    }

    public TestLogger(String jobName) {
        this.jobName = jobName;
    }

    public static synchronized void info(String jobName, String message) {
        Logger l = getTestLogger(jobName);
        l.info(message);
        Reporter.log("[INFO] "+message); // also log to TestNG report
    }
    public synchronized void info(String message) {
        Logger l = getTestLogger(jobName);
        l.info(message);
        Reporter.log("[INFO] "+message); // also log to TestNG report
    }

    public static synchronized void error(String jobName, String message) {
        Logger l = getTestLogger(jobName);
        l.error(message);
        Reporter.log("[ERROR] "+message); // also log to TestNG report
    }
    public synchronized void error(String message) {
        Logger l = getTestLogger(jobName);
        l.error(message);
        Reporter.log("[ERROR] "+message); // also log to TestNG report
    }
    public synchronized void error(Object message) {
        Logger l = getTestLogger(jobName);
        l.error(message.toString());
        Reporter.log("[ERROR] "+message.toString()); // also log to TestNG report
    }

    public static synchronized void debug(String jobName, String message) {
        Logger l = getTestLogger(jobName);
        l.debug(message);
        Reporter.log("[DEBUG] "+message); // also log to TestNG report
    }
    public synchronized void debug(String message) {
        Logger l = getTestLogger(jobName);
        l.debug(message);
        Reporter.log("[DEBUG] "+message); // also log to TestNG report
    }
    public synchronized void debug(Object message) {
        Logger l = getTestLogger(jobName);
        l.error(message.toString());
        Reporter.log("[DEBUG] "+message.toString()); // also log to TestNG report
    }

    /**
     * this is our custom logger which stores custom log file name based on unique
     * name for each test
     */
    private static Logger getTestLogger(String testName) {
        Logger logger = (Logger) testLoggers.get(testName);
        if (dir == null || layout == null) {
            try {
                initialize();
            } catch (Exception e) {
                rootLogger.info("error getting file appender for custom logger");
                return rootLogger;
            }
        }
        if (logger == null) {
            logger = Logger.getLogger(testName);
            testLoggers.put(testName, logger);
            logger.setLevel(Level.INFO);
            try {
                File file = new File(dir);
                file.mkdirs();
                file = new File(dir, testName + ".log");
                FileAppender appender = new FileAppender(layout,
                        file.getAbsolutePath(), false);
                logger.removeAllAppenders();
                logger.addAppender(appender);
                rootLogger.info("file absolute path is " + file.getAbsolutePath());
            } catch (Exception e) {
                rootLogger.info("error getting custom logger , return root logger");
                logger = rootLogger;
            }
        }
        return logger;
    }

    private static void initialize() throws Exception {
        Enumeration enumeration = Logger.getRootLogger().getAllAppenders();
        while (enumeration.hasMoreElements()) {
            Appender app = (Appender) enumeration.nextElement();
            if (app instanceof FileAppender) {
                layout = app.getLayout();
                File f = new File(((FileAppender) app).getFile());
                dir = f.getParent();
            }
        }
        if (dir == null) {
            throw new Exception("dir is null ");
        }

    }

}
