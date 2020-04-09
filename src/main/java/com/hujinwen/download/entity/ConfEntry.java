package com.hujinwen.download.entity;

/**
 * Create by hjw on 2019/3/11
 */
public interface ConfEntry {
    /**
     * 配置文件名
     */
    String CONF_FILE = "/conf.properties";

    /**
     * aria 安装路径
     */
    String ARIA_HOME = "aria2c_home";

    /**
     * 单任务线程数
     */
    String TASK_THREAD_NAME = "task_thread_num";

    /**
     * 任务失败重试次数
     */
    String RETRY_TIME = "retry_times";

    /**
     * 是否更新 BT tracker
     */
    String UPDATE_TRACKER = "update_tracker";

    /**
     * 目标目录有同名文件，是否自动命名
     */
    String AUTO_RENAME = "auto_rename";

    /**
     * 自定义 worker factory 名称
     */
    String FACTORY = "factory";
}
