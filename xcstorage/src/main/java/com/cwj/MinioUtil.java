package com.cwj;


import java.io.File;
import java.io.InputStream;

public interface MinioUtil {

    public String buildpath(String prefix, String filename);
    public String uploadImgFile(String prefix, String filename, InputStream inputStream);

    /**
     *  上传html文件
     * @param prefix  文件前缀
     * @param filename   文件名
     * @param inputStream  文件流
     * @return  文件全路径
     */
    //和普通图片不同的是会渲染
    public String uploadHtmlFile(String prefix, String filename,InputStream inputStream);

    /**
     * 删除文件
     * @param pathUrl  文件全路径
     */
    public void delete(String pathUrl);

    /**
     * 下载文件
     * @param pathUrl  文件全路径
     * @return
     *
     */
    public String  downLoadFile(String pathUrl,String tar);

    File downLoadFileByStream(String pathUrl);
}
