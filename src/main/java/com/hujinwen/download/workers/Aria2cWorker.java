package com.hujinwen.download.workers;

import com.hujinwen.download.core.DownloadInit;
import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.utils.ByteLenUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * Create by hjw on 2019/1/29
 */
public class Aria2cWorker extends DownloadWorker {
    private static final Logger logger = LogManager.getLogger(Aria2cWorker.class);

    private static final MessageFormat ARIA2C_TEMP = new MessageFormat("cmd /c aria2c.exe --conf-path=aria2c.conf -d {0} -o {1} {2}");

    private final StringBuilder ARIA_RESP = new StringBuilder();

    public Aria2cWorker(DownloadSeed seed) throws IOException {
        super(seed, false);
    }

    @Override
    protected void init() throws IOException {
    }

    @Override
    public void run() {
        try {
            download();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 下载
     */
    @Override
    protected void download() throws IOException {
        Process process = Runtime.getRuntime().exec(
                ARIA2C_TEMP.format(new Object[]{seed.getLocalPath(), seed.getLocalName(), seed.getUrl()}),
                null,
                DownloadInit.ARIA_HOME
        );
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("ETA:")) {
                    ARIA_RESP.setLength(0);
                    ARIA_RESP.append(line);
                }
            }
        } finally {
            process.destroy();
        }
    }

    @Override
    public double getProgress() {
        if (ARIA_RESP.length() == 0) {
            return 0.0;
        }
        return Integer.parseInt(ARIA_RESP.substring(ARIA_RESP.indexOf("(") + 1, ARIA_RESP.indexOf("%"))) / 100.0;
    }

    @Override
    public double getSpeed() {
        if (ARIA_RESP.length() == 0) {
            return 0.0;
        }
        return ByteLenUtils.toKb(ARIA_RESP.substring(ARIA_RESP.indexOf("DL:"), ARIA_RESP.indexOf("ETA:")));
    }
}
