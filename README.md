# Jobtain
> java下载程序

[TOC]

## 简介

### 规划

希望用java程序实现一个下载器，它支持以下功能：

* 断点续传

  已下载过的部分无需重复下载。

* 多线程下载（尽可能的跑满你的带宽）

  在服务主机允许的情况下，将下载速度尽可能的达到你所设置的值。

* 多种格式支持

  一个好用的下载程序，需要尽可能的支持更多的格式。

暂时完成了程序雏形，前路漫漫！同志任需努力。

### 支持的格式

目前支持的下载格式有：

1. http

2. m3u8

3. ftp

4. magnet

5. thunder（部分）

计划未来支持的有：

1. m4s
2. ……



## 使用

* 直接下载

  ```java
  import com.hujinwen.download.DownloadManager
  
  final DownloadWorker worker = DownloadManager.download(downloadSeed, true);
  ```

* 下载的同时获取下载进度

  ```java
  import com.hujinwen.download.DownloadManager
  
  @Test
  void war3Download() throws IOException, InterruptedException {
    String url = "http://dldir1.qq.com/qqfile/QQforMac/QQ_6.6.7.dmg";
    String localPath = "/Users/hujinwen/Desktop/Download";
    String localName = "";
  
    final HttpDownloadSeed downloadSeed = new HttpDownloadSeed(url, localPath, localName);
  
    final DownloadWorker worker = DownloadManager.download(downloadSeed, false);
    while (!worker.isFinish()) {
      logger.debug("progress -> {}%, speed -> {}kb/s", worker.getProgress() * 100, worker.getSpeed());
      Thread.sleep(1000);
    }
  }
  ```

* 详细请看DownloadManager



## TODO 
1. bug fixed

2. 下载图片（小文件）到字节数组

3. 编译成各平台客户端（win、linux、mac）

4. 添加对 YouTube 视频下载（视频和音频分离）

5. 添加对哔哩哔哩/youtube类型下载（视频和音频分离）

   * bilibili

     * 视频

       ```
       https://2ptbko0.yfcalc.com:8340/upos-dash-mirrorks3u.bilivideo.com/bilibilidash_1a9891871ccf5848b48059cf351dffafc65d4e7f/173233842_da2-1-30112.m4s?scuid=HFiK2vrEYYRS9G8McVff&timeout=1593850521&check=3977169348&sttype=90&yfdspt=1593245721584&yfpri=150&yfopt=17&yfskip=1&yfreqid=AEyyliECJDvcAGZAAM&yftt=100&yfhost=9kalbf3.yfcache.com&yfpm=1
       ```

     * 音频

       ```
       https://vqr1li1.yfcalc.com:6857/upos-dash-mirrorks3u.bilivideo.com/bilibilidash_361fd8034dfbaada5e7533ddfcd2d443dfc40d6d/173233842_da2-1-30280.m4s?scuid=HFiK2s48kxnQVCWwBBU6&timeout=1593850521&check=527352451&sttype=90&yfdspt=1593245721581&yfpri=150&yfopt=25&yfskip=1&yfreqid=AEyyliECJDvcAGZAAK&yftt=100&yfhost=9kalbf3.yfcache.com&yfpm=1
       ```

6. 所有需要使用http请求的继承httpWorker，seed继承httpSeed。请求头和cookie是http请求中独有的

7. findWorker时，发现时httpSeed就直接返回HttpWorker

8. 新增小文件下载（图片），小文件下载返回byte数组。要限制"小"的大小。防止过大占用内存

## BUG FIX

2. http多线程下载时，如果先下载完的close了httpClient，会报错。（哔哩哔哩下载）




