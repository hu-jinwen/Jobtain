package com.hujinwen.download.workers;

import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.download.entity.seeds.FtpDownloadSeed;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


/**
 * Created by joe on 2019/2/15
 */
public class FtpWorker extends DownloadWorker {
    private static final Logger logger = LogManager.getLogger(FtpWorker.class);

    public FtpWorker(DownloadSeed seed) throws IOException {
        super(seed instanceof FtpDownloadSeed ? seed : new FtpDownloadSeed(seed), false);
    }

    @Override
    protected void init() throws IOException {
    }

    @Override
    protected void download() throws IOException {
        FtpDownloadSeed seed = (FtpDownloadSeed) this.seed;
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(seed.getHost(), seed.getPort());
        ftpClient.login(seed.getUser(), seed.getPass());
        ftpClient.setControlEncoding("GBK");

        if (ftpClient.getReplyCode() != 230) {
            logger.error("+++++>> Login failed, connection closed!");
            ftpClient.disconnect();
        }

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setKeepAlive(true);
//        ftpClient.setControlKeepAliveTimeout(60 * 60);
        ftpClient.setDataTimeout(60 * 60 * 2);  // 设置下载超时时间，单位秒

        ftpClient.enterLocalPassiveMode();
        FTPFile[] ftpFiles = ftpClient.listFiles(seed.getRemotePath(), (FTPFile ftpFile) -> seed.getRemoteName().equals(ftpFile.getName()));
        if (ftpFiles.length == 1) {
            FTPFile ftpFile = ftpFiles[0];
            // 获取文件大小
            info.fileSize = ftpFile.getSize();
            String remoteFile = new String((seed.getRemotePath() + "/" + seed.getRemoteName()).getBytes("GBK"), "ISO-8859-1");
            try (
                    InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
                    RandomAccessFile outputFile = new RandomAccessFile(seed.getLocalPath() + "/" + seed.getLocalName(), "rw")
            ) {
                downloadFromInputStream(inputStream, outputFile, "skip");
            }
        } else {
            logger.error("+++++>> The file could not found on the remote, filename -> " + seed.getRemoteName());
        }
        ftpClient.disconnect();
    }
}
