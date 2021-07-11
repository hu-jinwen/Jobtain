package com.hujinwen.download.core;

import com.hujinwen.client.http.HttpClient;
import com.hujinwen.download.entity.ConfEntry;
import com.hujinwen.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Create by joe on 2019/2/12
 */
public class DownloadInit {
    private static final Logger logger = LogManager.getLogger(DownloadInit.class);

    /**
     * aria home
     */
    public static File ARIA_HOME;

    /**
     * 单任务最大线程数
     */
    public static int THREAD_NUM;

    /**
     * 下载失败最大重试次数
     */
    public static int RETRY_TIMES;

    /**
     * 目标位置已存在相同名称文件，是否自动命名
     */
    public static boolean AUTO_RENAME;

    /**
     * 是否更新aria tracker
     */
    private static boolean UPDATE_TRACKER;

    /**
     * 自定义 worker factory
     */
    public static String WORKER_FACTORY;


    public static void init() {
        try {
            loadConf();
            if (UPDATE_TRACKER) {
                trackerUpdate();
            }
        } catch (IOException e) {
            throw new RuntimeException("Download module initialization failed!");
        } catch (Exception e) {
            logger.warn("The aria tracker update failed!", e);
        }
    }

    /**
     * 加载配置文件
     */
    private static void loadConf() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = DownloadInit.class.getResourceAsStream(ConfEntry.CONF_FILE);
        properties.load(inputStream);
        ARIA_HOME = new File(properties.getProperty(ConfEntry.ARIA_HOME));
        THREAD_NUM = Integer.parseInt(properties.getProperty(ConfEntry.TASK_THREAD_NAME, "1"));
        RETRY_TIMES = Integer.parseInt(properties.getProperty(ConfEntry.RETRY_TIME, "1"));
        UPDATE_TRACKER = Boolean.parseBoolean(properties.getProperty(ConfEntry.UPDATE_TRACKER, "false"));
        AUTO_RENAME = Boolean.parseBoolean(properties.getProperty(ConfEntry.AUTO_RENAME, "true"));
        WORKER_FACTORY = properties.getProperty(ConfEntry.FACTORY);
    }

    /**
     * BT tracker 服务器更新
     */
    private static void trackerUpdate() throws IOException {
        // FIXME 此处待优化
        final HttpClient httpClient = HttpClient.createDefault();
        String content = httpClient.doGetAsStr("https://raw.githubusercontent.com/ngosang/trackerslist/master/trackers_best.txt");
        //

        if (StringUtils.isBlank(content)) {
            return;
        }

        File confFile = new File(ARIA_HOME.getPath() + "/aria2c.conf");
        String separator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(confFile, "rw")
        ) {
            sb.append("# updated time: ").append(DateUtils.convertTime(String.valueOf(System.currentTimeMillis())))
                    .append(separator);
            String line;
            while ((line = randomAccessFile.readLine()) != null) {
                if (line.startsWith("# updated time:")) {
                    continue;
                }
                if (line.startsWith("bt-tracker=")) {
                    line = "bt-tracker=" + content.replaceAll("\n+", ",");
                }
                sb.append(line).append(separator);
            }
            randomAccessFile.setLength(0);
            randomAccessFile.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        }
    }

}
