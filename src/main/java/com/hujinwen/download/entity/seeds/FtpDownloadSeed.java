package com.hujinwen.download.entity.seeds;

/**
 * Created by joe on 2019/2/17
 */
public class FtpDownloadSeed extends DownloadSeed {

    /**
     * host
     */
    protected String host;

    /**
     * 端口
     */
    protected int port;

    /**
     * 用户名
     */
    protected String user;

    /**
     * 密码
     */
    protected String pass;

    /**
     * 远端路径
     */
    protected String remotePath;

    /**
     * 远端文件名
     */
    protected String remoteName;


    public FtpDownloadSeed(DownloadSeed downloadSeed) {
        this(downloadSeed.url, downloadSeed.localPath, downloadSeed.localName);
    }

    public FtpDownloadSeed(String url, String localPath, String localName, String host, int port, String user, String pass, String remotePath, String remoteName) {
        super(url, localPath, localName);
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.remotePath = remotePath;
        this.remoteName = remoteName;
    }

    public FtpDownloadSeed(String url, String localPath, String localName) {
        super(url, localPath, localName);
        // username / password
        int index = url.indexOf("//");
        int index2 = url.indexOf("@");
        String[] loginArr = url.substring(index + 2, index2).split(":");
        if (loginArr.length == 2) {
            user = loginArr[0];
            pass = loginArr[1];
        }
        // host / port
        int index3 = url.indexOf("/", index + 2);
        String[] AddrArr = url.substring(index2 + 1, index3).split(":");
        if (AddrArr.length == 2) {
            host = AddrArr[0];
            port = Integer.parseInt(AddrArr[1]);
        }
        // remote path
        int index4 = url.lastIndexOf("/");
        remotePath = url.substring(index3, index4);
        // remote name
        remoteName = url.substring(index4 + 1);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

}
