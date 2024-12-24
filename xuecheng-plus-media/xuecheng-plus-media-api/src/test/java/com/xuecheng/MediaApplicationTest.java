package com.xuecheng;


import com.cwj.MinioConfig;
import com.cwj.MinioUtil;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.Min;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

@SpringBootTest
class MediaApplicationTest {

    @Autowired
    MinioUtil minioUtil;

    @Autowired
    MinioClient minioClient;

    @Test
    void test() {

//        String pic = minioUtil.buildpath("pic", "2.jpg");
//        System.out.println(pic);
        try {
            minioUtil.uploadImgFile("pic", "1.jpg", new FileInputStream("C:\\Users\\cheng\\Pictures\\Figure_1.png"));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private final static int bsize = 1024 * 1024;

    @Test
    void testchunk() {  //将本地视频切片
        String source = "D:\\1.mp4";
        try {
            RandomAccessFile raf_read = new RandomAccessFile(source, "r");


            long length = raf_read.length();
            long n = length % bsize == 0 ? length / bsize : length / bsize + 1; //个数

            System.out.println("分块数：" + n);
            int cnt = 0;
            //怎么存？
            byte[] b = new byte[bsize];

            String pa = "D://chunk/";
            File folder = new File(pa);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            for (int i = 0; i < n; i++) {

                String path = pa + i;
                File f = new File(path);
                if (f.exists()) {
                    f.delete();
                }
                boolean newFile = f.createNewFile();
                if (newFile) {
                    RandomAccessFile raf_write = new RandomAccessFile(f, "rw");
                    int len = -1;
                    while ((len = raf_read.read(b)) != -1) {
                        raf_write.write(b, 0, len);
                        //读到了bsize就停止
                        break;
                    }
                    raf_write.close();
                    System.out.println("分块" + i + "完成");
                }


            }

            raf_read.close();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    void testUploadFile() throws IOException {
        File sourceFile = new File("D://1.mp4");

        String fd = "D://chunk/"+sourceFile.getName()+"/";
        File fold=new File(fd);

        if(!fold.exists()) {
            boolean mkdirs = fold.mkdirs();
        }
        //对文件进行分块
        long bsize = 1 * 1024 * 1024;  //1mb
        long cnt = (long) Math.ceil(sourceFile.length() * 1.0 / bsize);
//        System.out.println(cnt);
        byte[] b = new byte[1024];
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        for (int i = 0; i < cnt; i++) {
            String path = fd + i;
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            boolean newFile = f.createNewFile();
            if (newFile) {
                RandomAccessFile raf_write = new RandomAccessFile(f, "rw");

                int len = -1;
                while ((len = raf_read.read(b)) != -1) {

                    raf_write.write(b, 0, len);
                    if (f.length() >= bsize) break;
                }
                raf_write.close();
            }

        }
        raf_read.close();


        File[] files = new File(fd).listFiles();

//        Arrays.sort(files, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                return 0;
//            }
//        });
//
        Arrays.sort(files,((o1, o2) ->Integer.compare(Integer.parseInt(o1.getName()),Integer.parseInt(o1.getName()))));


        for (File f : files) {
            String miniopath = "2024/"+sourceFile.getName()+"/" + f.getName();
            if (check(miniopath)) System.out.println("分块" + f.getName() + "已存在");
            else upload(miniopath, f);
        }
        System.out.println("全部上传");
    }


    boolean check(String p){
        GetObjectArgs mediafiles = GetObjectArgs.builder().bucket("mediafiles").object(p).build();
        InputStream object = null;
        try {
            object = minioClient.getObject(mediafiles);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return object != null;
    }

    void upload(String fp,File f) throws FileNotFoundException {
        PutObjectArgs p=PutObjectArgs.builder().bucket("mediafiles")
                .contentType("video/mp4").stream(new FileInputStream(f),f.length(),-1)
                .object(fp)
                .build();

        try {
            minioClient.putObject(p);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    @Test
    void merge() throws IOException {              //首先需要对文件夹中的文件进行排序

        String folder = "D://chunk";
        String target = "D://2.mp4";

        File file = new File(folder);
        File[] files = file.listFiles();

        Arrays.sort(files, Comparator.comparingInt(f -> Integer.valueOf(f.getName())));

        File tf = new File(target);
        if (tf.exists()) tf.delete();

        boolean newFile = tf.createNewFile();

        if (newFile) {

            RandomAccessFile raf_write = new RandomAccessFile(tf, "rw");
            raf_write.seek(0);

            byte[] b = new byte[bsize];

            for (File f : files) {
                RandomAccessFile raf_read = new RandomAccessFile(f, "r");
                int read = raf_read.read(b);
                if (read != -1) raf_write.write(b, 0, read);
                raf_read.close();
            }

            raf_write.close();

        }

        String originalFile = "D://1.mp4";
        String mergeFile = "D://2.mp4";
        try (

                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }

    }
}