package com.example.bkapi.common.util;

import com.example.bkapi.common.entity.Minio;
import io.minio.*;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @program: bkapi
 * @ClassName MinioUtils
 * @description:
 * @author: zzm
 * @create: 2020-05-12 14:17
 * @Version 1.0
 **/
//@EnableConfigurationProperties(Minio.class)
@Component
public class MinioUtils {

    private static Minio minio;


    @Autowired
    public Minio minioTmp;

//    private static  String endpoint = minio.getEndpoint();
//
//    private static  String accessKey = minio.getAccessKey();
//
//    private static  String secretKey = minio.getSecretKey();
//
//    private static  String endpointIn = minio.getEndpointIn();

//    @PostConstruct注解可以在依赖关系注入完毕之后立即执行,可以用这个来对类进行初始化
    @PostConstruct
    private void init() {
        minio = this.minioTmp;
    }


    /**
     * 创建minioClient
     * @return
     * @throws InvalidPortException
     * @throws InvalidEndpointException
     */
    public static MinioClient createMinioClient() throws InvalidPortException, InvalidEndpointException {
        MinioClient minioClient = new MinioClient(minio.getEndpoint(), minio.getAccessKey(), minio.getSecretKey());
        return minioClient;
    }

    /**
     * 创建内网minioClient
     * @return
     * @throws InvalidPortException
     * @throws InvalidEndpointException
     */
    public static MinioClient createInMinioClient() throws InvalidPortException, InvalidEndpointException {
        MinioClient minioClient = new MinioClient(minio.getEndpointIn(), minio.getAccessKey(), minio.getSecretKey());
        return minioClient;
    }

    /**
     * 创建具有给定区域的新存储桶。
     * @param bucketName
     */
    public static void makeBucket(String bucketName){
        try {
            MinioClient minioClient = createInMinioClient();

            // Create bucket if it doesn't exist.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                System.out.println(bucketName + " already exists");
            } else {
                // Create bucket 'my-bucketname'.
                minioClient.makeBucket(bucketName);
                System.out.println(bucketName + " is created successfully");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出所有桶。
     * @return
     */
    public static List<Bucket> listBuckets(){
        List<Bucket> bucketList = null;
        try {
            MinioClient minioClient = createInMinioClient();

            // List buckets that have read access.
            bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                System.out.println(bucket.creationDate() + ", " + bucket.name());
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bucketList;
    }

    /**
     * 检查是否存在存储桶。
     * @param bucketName
     * @return
     */
    public static boolean bucketExists(String bucketName){

        boolean found = false;
        try {
            MinioClient minioClient = createInMinioClient();

            // Check whether 'my-bucketname' exists or not.
            found = minioClient.bucketExists(bucketName);
            if (found) {
                System.out.println(bucketName + " exists");
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return found;
    }

    /**
     * 删除一个桶。
     * 注意： - removeBucket不会删除存储桶内的对象。需要使用removeObject API删除对象。
     * @param bucketName
     */
    public static void removeBucket (String bucketName){
        try {
            MinioClient minioClient = createInMinioClient();
            // Check if my-bucket exists before removing it.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // Remove bucket my-bucketname. This operation will succeed only if the bucket is empty.
                minioClient.removeBucket(bucketName);
                System.out.println(bucketName + " is removed successfully");
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出给定存储桶中的对象信息。
     * @param bucketName
     * @return
     */
    public static Iterable<Result<Item>> listObjects(String bucketName){
        Iterable<Result<Item>> myObjects = null;
        try {
            MinioClient minioClient = createInMinioClient();

            // Check whether 'mybucket' exists or not.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // List objects from 'my-bucketname'
                myObjects = minioClient.listObjects(bucketName);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                }
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myObjects;
    }

    /**
     * 列出给定存储桶和前缀中的对象信息。
     * @param bucketName
     * @param prefix
     * @return
     */
    public static Iterable<Result<Item>> listObjects(String bucketName,String prefix){
        Iterable<Result<Item>> myObjects = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether 'mybucket' exists or not.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // List objects from 'my-bucketname'
                myObjects = minioClient.listObjects(bucketName,prefix);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                }
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myObjects;
    }

    /**
     * 将对象信息列为Iterable 在给定的桶，前缀和递归标志。
     * @param bucketName
     * @param prefix
     * @param recursive
     * @return
     */
    public static Iterable<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive){
        Iterable<Result<Item>> myObjects = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether 'mybucket' exists or not.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // List objects from 'my-bucketname'
                myObjects = minioClient.listObjects(bucketName,prefix,recursive);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                }
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myObjects;
    }

    public static Iterable<Result<Item>> listObjects(String bucketName,String prefix,boolean recursive,boolean useVersion1){
        Iterable<Result<Item>> myObjects = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether 'mybucket' exists or not.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // List objects from 'my-bucketname'
                myObjects = minioClient.listObjects(bucketName,prefix,recursive,useVersion1);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                }
            } else {
                System.out.println(bucketName + " does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myObjects;
    }

    /**
     * 在桶上设置生命周期。
     * @param bucketName
     * @param lifeCycle
     */
    public static void setBucketLifeCycle(String bucketName, String lifeCycle){
//        try {
//    /* Amazon S3: */
//            MinioClient minioClient = new MinioClient("https://s3.amazonaws.com", "YOUR-ACCESSKEYID",
//                    "YOUR-SECRETACCESSKEY");
//            String lifeCycle = "<LifecycleConfiguration><Rule><ID>expire-bucket</ID><Prefix></Prefix>"
//                    + "<Status>Enabled</Status><Expiration><Days>365</Days></Expiration>"
//                    + "</Rule></LifecycleConfiguration>";
//
//
//            minioClient.setBucketLifecycle("lifecycleminiotest", lifeCycle);
//        } catch (MinioException e) {
//            System.out.println("Error occurred: " + e);
//        }

    }

    /**
     * 获取存储桶的生命周期。
     * @param bucketName
     * @return
     */
    public static String getBucketLifeCycle(String bucketName){
//        try {
        /* Amazon S3: */
//            MinioClient minioClient = new MinioClient("https://s3.amazonaws.com", "YOUR-ACCESSKEYID",
//                    "YOUR-SECRETACCESSKEY");
//            String lifecycle = minioClient.getBucketLifecycle("my-bucketName" );
//            System.out.println(" Life Cycle is : " + lifecycle );
//        } catch (MinioException e) {
//            System.out.println("Error occurred: " + e);
//        }
        return null;
    }

    /**
     * 删除存储桶的生命周期。
     * @param bucketName
     */
    private void deleteBucketLifeCycle(String bucketName){
//        try {
//   /* Amazon S3: */
//            MinioClient minioClient = new MinioClient("https://s3.amazonaws.com", "YOUR-ACCESSKEYID",
//                    "YOUR-SECRETACCESSKEY");
//            minioClient.deleteBucketLifeCycle("my-bucketName" );
//        } catch (MinioException e) {
//            System.out.println("Error occurred: " + e);
//        }
    }


    /**
     * 对象操作
     */


    /**
     * 将对象下载为流。
     * @param bucketName
     * @param objectName
     * @return
     */
    public static InputStream getObject(String bucketName, String objectName){
        InputStream stream = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether the object exists using statObject().
            // If the object is not found, statObject() throws an exception,
            // else it means that the object exists.
            // Execution is successful.
            minioClient.statObject(bucketName, objectName);

            // Get input stream to have content of 'my-objectname' from 'my-bucketname'
            stream = minioClient.getObject(bucketName, objectName);

            // Read the input stream and print to the console till EOF.
//            byte[] buf = new byte[16384];
//            int bytesRead;
//            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
//                System.out.println(new String(buf, 0, bytesRead));
//            }

            // Close the input stream.
//            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static InputStream getObject(String bucketName, String objectName, long offset){
        InputStream stream = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether the object exists using statObject().
            // If the object is not found, statObject() throws an exception,
            // else it means that the object exists.
            // Execution is successful.
            minioClient.statObject(bucketName, objectName);

            // Get input stream to have content of 'my-objectname' from 'my-bucketname'
            stream = minioClient.getObject(bucketName,objectName, offset);

            // Read the input stream and print to the console till EOF.
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead));
            }

            // Close the input stream.
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * 将对象的指定范围字节作为流下载。
     * @param bucketName
     * @param objectName
     * @param offset
     * @param length
     * @return
     */
    public static InputStream getObject(String bucketName, String objectName, long offset, Long length){
        InputStream stream = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether the object exists using statObject().
            // If the object is not found, statObject() throws an exception,
            // else it means that the object exists.
            // Execution is successful.
            minioClient.statObject(bucketName, objectName);

            // Get input stream to have content of 'my-objectname' from 'my-bucketname'
            stream = minioClient.getObject(bucketName, objectName, offset, length);

            // Read the input stream and print to the console till EOF.
            byte[] buf = new byte[16384];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                System.out.println(new String(buf, 0, bytesRead));
            }

            // Close the input stream.
            stream.close();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * 将对象下载并保存为本地文件系统中的文件。
     * @param bucketName
     * @param objectName
     * @param fileName
     */
    public static void getObject(String bucketName, String objectName, String fileName){
        try {
            MinioClient minioClient = createInMinioClient();
            // Check whether the object exists using statObject().
            // If the object is not found, statObject() throws an exception,
            // else it means that the object exists.
            // Execution is successful.
            minioClient.statObject(bucketName, objectName);

            // Gets the object's data and stores it in photo.jpg
            minioClient.getObject(bucketName, objectName, fileName);

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过InputStream上传对象。
     * @param bucketName
     * @param objectName
     * @param stream
     * @param size
     * @param contentType  application/octet-stream
     */
    public static ObjectStat putObject(String bucketName, String objectName, InputStream stream, long size, String contentType){
        try {
            MinioClient minioClient = createInMinioClient();
            // 创建对象
            minioClient.putObject(bucketName, objectName, stream, size, contentType);

            System.out.println(objectName + " is uploaded successfully");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statObject(bucketName,objectName);
    }

    /**
     * 通过文件上传到对象中。
     * @param bucketName
     * @param objectName
     * @param fileName
     * @return 1-成功
     */
    public static int putObject(String bucketName, String objectName, String fileName){
        try {
            MinioClient minioClient = createInMinioClient();

            minioClient.putObject(bucketName,  objectName, fileName);
            System.out.println(objectName + " is uploaded successfully");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取对象的元数据。
     * @param bucketName
     * @param objectName
     * @return
     */
    public static ObjectStat statObject(String bucketName, String objectName){
        ObjectStat objectStat = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // 获得对象的元数据。
            objectStat = minioClient.statObject(bucketName, objectName);
            System.out.println(objectStat);
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectStat;
    }

    /**
     * 从objectName指定的对象中将数据拷贝到destObjectName指定的对象。
     * @param bucketName
     * @param objectName
     * @param destBucketName
     * @param destObjectName
     * @param cpConds  拷贝操作的一些条件Map。
     * @param metadata 给目标对象的元数据Map。
     */
    public static void copyObject(String bucketName, String objectName, String destBucketName, String destObjectName, CopyConditions cpConds, Map<String, String> metadata){
        try {
            MinioClient minioClient = createInMinioClient();

            CopyConditions copyConditions = new CopyConditions();
            copyConditions.setMatchETagNone("TestETag");

            minioClient.copyObject(bucketName,  objectName, destBucketName, destObjectName, copyConditions);//copyConditions 或 cpConds
            System.out.println(objectName + " is uploaded successfully");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一个对象。
     * @param bucketName
     * @param objectName
     */
    public static void removeObject(String bucketName, String objectName){
        try {
            MinioClient minioClient = createInMinioClient();

            // 从bucketName中删除objectName。
            minioClient.removeObject(bucketName, objectName);
            System.out.println("successfully removed " + bucketName + "/" +objectName);
        } catch (MinioException e) {
            System.out.println("Error: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除多个对象。
     * @param bucketName
     * @param objectNames
     * @return
     */
    public static Iterable<Result<DeleteError>> removeObject(String bucketName, Iterable<String> objectNames){
        Iterable<Result<DeleteError>> results = null;
        try {
            MinioClient minioClient = createInMinioClient();
            // 删除my-bucketname里的多个对象
            results = minioClient.removeObject(bucketName, objectNames);

            for (Result<DeleteError> errorResult: results) {
                DeleteError error = errorResult.get();
                System.out.println("Failed to remove '" + error.objectName() + "'. Error:" + error.message());
            }
        } catch (MinioException e) {
            System.out.println("Error: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 删除一个未完整上传的对象。
     * @param bucketName
     * @param objectName
     */
    public static void removeIncompleteUpload(String bucketName, String objectName){
        try {
            MinioClient minioClient = createInMinioClient();
            // 从存储桶中删除名为myobject的未完整上传的对象。
            minioClient.removeIncompleteUpload(bucketName, objectName);
            System.out.println("successfully removed all incomplete upload session of " + bucketName + "/" +objectName);
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  生成一个给HTTP GET请求用的presigned URL。浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。
     *  这个presigned URL可以设置一个失效时间，默认值是7天。
     *  一天：60 * 60 * 24
     */
    public static String presignedGetObject(String bucketName, String objectName, Integer expires){
        String url = null;
        try {
            MinioClient minioClient = createMinioClient();

            url = minioClient.presignedGetObject(bucketName, objectName, expires);
            System.out.println(url);
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 生成一个给HTTP PUT请求用的presigned URL。浏览器/移动端的客户端可以用这个URL进行上传，即使其所在的存储桶是私有的。
     * 这个presigned URL可以设置一个失效时间，默认值是7天。
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String presignedPutObject(String bucketName, String objectName, Integer expires){
        String url = null;
        try {
            MinioClient minioClient = createMinioClient();

            url = minioClient.presignedPutObject(bucketName, objectName, expires);
            System.out.println(url);
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 允许给POST请求的presigned URL设置策略，比如接收对象上传的存储桶名称的策略，key名称前缀，过期策略。
     * @param policy
     * @return
     */
    public static Map<String,String> presignedPostPolicy(PostPolicy policy){
        Map<String,String> formData = null;
        try {
            //PostPolicy policy = new PostPolicy("mybucket", "myobject", DateTime.now().plusDays(7));
            //policy.setContentType("image/png");
            MinioClient minioClient = createMinioClient();

            formData = minioClient.presignedPostPolicy(policy);
            System.out.print("curl -X POST ");
            for (Map.Entry<String,String> entry : formData.entrySet()) {
                System.out.print(" -F " + entry.getKey() + "=" + entry.getValue());
            }
            System.out.println(" -F file=@/tmp/userpic.png  https://play.minio.io:9000/mybucket");
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formData;
    }

    /**
     * 获取文件永久地址
     * @param bucketName   桶名称
     * @param objectName   文件名字
     * @return
     */
    public static String getObjectUrl(String bucketName, String objectName){
        String url = null;
        try {
            MinioClient minioClient = createInMinioClient();

            url = minioClient.getObjectUrl(bucketName, objectName);
            System.out.println(url);
        } catch(MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static void main(String[] args) {

        String fileName = "C:\\Users\\d\\Documents\\WeChat Files\\minzezheng\\FileStorage\\File\\2019-12\\电信.xlsx";
        int i = putObject("photo", "电信.xlsx", fileName);

//        String s = presignedGetObject("fsjcz-zidong", "ps路飞.png", 86400);
//        System.out.println("s = " + s);
    }
}
