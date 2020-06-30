# JDer
> joe's Downloader 我的下载器

[TOC]

* 支持断点续传
* 支持多线程下载（跑满你的带宽）
* 支持迅雷磁链下载（aria2c支持）
* 支持ftp下载

**暂时现在只实现了基本功能，还有很多的BUG，待优化**

## TODO 
1. bug fixed

2. 编译成各平台客户端（win、linux、mac）

3. 添加对哔哩哔哩/youtube类型下载（视频和音频分离）

   * bilibili

     * 视频

       ```
       https://2ptbko0.yfcalc.com:8340/upos-dash-mirrorks3u.bilivideo.com/bilibilidash_1a9891871ccf5848b48059cf351dffafc65d4e7f/173233842_da2-1-30112.m4s?scuid=HFiK2vrEYYRS9G8McVff&timeout=1593850521&check=3977169348&sttype=90&yfdspt=1593245721584&yfpri=150&yfopt=17&yfskip=1&yfreqid=AEyyliECJDvcAGZAAM&yftt=100&yfhost=9kalbf3.yfcache.com&yfpm=1
       ```

     * 音频

       ```
       https://vqr1li1.yfcalc.com:6857/upos-dash-mirrorks3u.bilivideo.com/bilibilidash_361fd8034dfbaada5e7533ddfcd2d443dfc40d6d/173233842_da2-1-30280.m4s?scuid=HFiK2s48kxnQVCWwBBU6&timeout=1593850521&check=527352451&sttype=90&yfdspt=1593245721581&yfpri=150&yfopt=25&yfskip=1&yfreqid=AEyyliECJDvcAGZAAK&yftt=100&yfhost=9kalbf3.yfcache.com&yfpm=1
       ```
   
4. 所有需要使用http请求的继承httpWorker，seed继承httpSeed。请求头和cookie是http请求中独有的

5. findWorker时，发现时httpSeed就直接返回HttpWorker

## BUG FIX

1. 断点续传时，已完成的分段（没有temp文件），会重新下载。比较浪费资源
2. http多线程下载时，如果先下载完的close了httpClient，会报错。（哔哩哔哩下载）



