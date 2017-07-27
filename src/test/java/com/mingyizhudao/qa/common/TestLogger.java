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

    public static synchronized void logMessage(String jobName, String message)
    {
        Logger l = getTestLogger(jobName);
        l.info(message);
        Reporter.log(message); // also log to TestNG report
    }

    /**
     * this is our custom logger which stores custom log file name based on unique
     * name for each test
     *
     * @param testName
     * @return
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
