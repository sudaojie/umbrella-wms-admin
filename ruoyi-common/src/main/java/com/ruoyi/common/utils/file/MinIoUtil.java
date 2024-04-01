package com.ruoyi.common.utils.file;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.exception.ServiceException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MinIoUtil {

    @Autowired(required = false)
    private MinioClient minioClient;

    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    /**
     * 临时
     */
    public static final String TEMPORARY = "temporary";

    /**
     * 正式
     */
    public static final String FORMAL = "formal";


    /**
     * 外部附件
     */
    public static final String EXTERNAL_SUPPLY = "other";

    /**
     * 静态目录名称
     */
    public static final String STATIC = "static";

    /**
     * 桶占位符
     */
    private static final String BUCKET_PARAM = "${bucket}";
    /**
     * bucket权限-只读
     */
    private static final String READ_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-只读
     */
    private static final String WRITE_ONLY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
    /**
     * bucket权限-读写
     */
    private static final String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";


    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean bucketExists(String bucketName) {
        boolean flag = false;
        try {
            flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            return true;
        }
        return false;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean makeBucket(String bucketName)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            setBucketPolicy(bucketName, "read-write");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public List<String> listBucketNames() throws InvalidKeyException, ErrorResponseException, IllegalArgumentException,
            InsufficientDataException, InternalException, InvalidResponseException,
            NoSuchAlgorithmException, XmlParserException, IOException, Exception {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public List<Bucket> listBuckets() throws InvalidKeyException, ErrorResponseException, IllegalArgumentException,
            InsufficientDataException, InternalException, InvalidResponseException,
            NoSuchAlgorithmException, XmlParserException, IOException, Exception {
        return minioClient.listBuckets();
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean removeBucket(String bucketName) throws InvalidKeyException, ErrorResponseException,
            IllegalArgumentException, InsufficientDataException, InternalException,
            InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public List<String> listObjectNames(String bucketName) throws InvalidKeyException, ErrorResponseException,
            IllegalArgumentException, InsufficientDataException, InternalException,
            InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException, Exception {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public Iterable<Result<Item>> listObjects(String bucketName) throws InvalidKeyException, ErrorResponseException,
            IllegalArgumentException, InsufficientDataException, InternalException,
            InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean putObject(String bucketName, String objectName, InputStream stream, String contentType)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, -1, 10485760).contentType(contentType).build());
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            if (statObjectResponse != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean putObject(String bucketName, String objectName, InputStream stream)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {

            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, -1, 10485760).contentType("application/octet-stream").build());
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            if (statObjectResponse != null && statObjectResponse.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public InputStream getObject(String bucketName, String objectName)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            if (statObjectResponse != null && statObjectResponse.size() > 0) {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build());
                {
                    // Read data from stream
                }
                return stream;
            }
        }
        return null;
    }


    /**
     * 将临时桶中的数据复制到正式桶中
     *
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public String copeToFormal(String objectName) {
        {
            try {
                // 判断正式桶是否存在 不存在就创建一个桶
                if (!bucketExists(FORMAL)) {
                    makeBucket(FORMAL);
                }
                // 5. 将TEMPORARY文件复制到FORMAL对象
                minioClient.copyObject(CopyObjectArgs.builder()
                        .bucket(FORMAL)
                        .object(objectName)
                        .source(CopySource.builder().bucket(TEMPORARY).object(objectName).build())
                        .build());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return getObjectUrl(FORMAL, objectName);
        }
    }


    /**
     * 以流的形式获取一个文件对象（断点下载）
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public InputStream getObject(String bucketName, String objectName, long offset, Long length)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            if (statObject != null && statObject.size() > 0) {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .offset(offset)
                                .length(length)
                                .build());
                return stream;
            }

        }
        return null;
    }


    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public boolean removeObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        try {
            if (flag) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
     *
     * @param bucketName  存储桶名称
     * @param objectNames 含有要删除的多个object名称的迭代器对象
     * @return
     * @throws InvalidKeyException
     * @throws ErrorResponseException
     * @throws IllegalArgumentException
     * @throws InsufficientDataException
     * @throws InternalException
     * @throws InvalidResponseException
     * @throws NoSuchAlgorithmException
     * @throws XmlParserException
     * @throws IOException
     */
    public List<String> removeObject(String bucketName, List<DeleteObject> objectNames)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        List<String> deleteErrorNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder().bucket(bucketName).objects(objectNames).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                deleteErrorNames.add(error.objectName());
            }
        }
        return deleteErrorNames;
    }

    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public StatObjectResponse statObject(String bucketName, String objectName)
            throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
            InternalException, InvalidResponseException, NoSuchAlgorithmException,
            XmlParserException, IOException, Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());

            return statObject;
        }
        return null;
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws IOException
     * @throws XmlParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidResponseException
     * @throws InternalException
     * @throws InsufficientDataException
     * @throws IllegalArgumentException
     * @throws ErrorResponseException
     * @throws InvalidKeyException
     */
    public String getObjectUrl(String bucketName, String objectName) {
        try {
            boolean flag = bucketExists(bucketName);
            String url = "";
            if (flag) {
                url = File.separator + bucketName + File.separator + objectName;
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 检查文件是否在临时目录
     *
     * @param path 文件路径
     * @return true/false
     */
    public static boolean inTemporaryPath(String path) {
        return path.contains(TEMPORARY);
    }


    /**
     * 获取日期格式目录
     *
     * @return 目录 eg: 2020/01/01 或 2020\01\01
     */
    public static String getDatePath() {
        Date now = new Date();
        return DateUtil.year(now) + "/" + (DateUtil.month(now) + 1) + "/" + DateUtil.dayOfMonth(now);
    }


    /**
     * 根据路径判断文件在桶内是否存在
     *
     * @param path
     * @return
     */
    public boolean objectExists(String path) {
        String objectName = "";
        StatObjectResponse statObject = null;
        try {
            // 文件是否在正式桶内
            if (path.contains(FORMAL)) {
                objectName = path.substring(path.indexOf(FORMAL) + 7);
                statObject = minioClient.statObject(StatObjectArgs.builder().bucket(FORMAL).object(objectName).build());
                // 文件是否在测试桶内
            } else if (path.contains(TEMPORARY)) {
                objectName = path.substring(path.indexOf(TEMPORARY) + 10);
                statObject = minioClient.statObject(StatObjectArgs.builder().bucket(TEMPORARY).object(objectName).build());
                // 文件不存在
            } else if (path.contains(STATIC)) {
                objectName = path.substring(path.indexOf(STATIC) + 7);
                statObject = minioClient.statObject(StatObjectArgs.builder().bucket(STATIC).object(objectName).build());
                // 文件不存在
            } else {
                return false;
            }
            if (statObject == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据路径获取文件名
     *
     * @param path
     * @return
     */
    public String getObjectName(String path) {
        String objectName = "";
        try {
            // 文件是否在正式桶内
            if (path.contains(FORMAL)) {
                objectName = path.substring(path.indexOf(FORMAL) + 7);
                // 文件是否在测试桶内
            } else if (path.contains(TEMPORARY)) {
                objectName = path.substring(path.indexOf(TEMPORARY) + 10);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            return objectName;
        }
        return objectName;
    }


    /**
     * 更新桶权限策略
     *
     * @param bucket 桶
     * @param policy 权限
     */
    public void setBucketPolicy(String bucket, String policy) throws Exception {
        switch (policy) {
            case "read-only":
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "write-only":
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(WRITE_ONLY.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "read-write":
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(READ_WRITE.replace(BUCKET_PARAM, bucket)).build());
                break;
            case "none":
            default:
                break;
        }
    }

    /**
     * 获取预览地址
     *
     * @param bucket    存储的仓库.
     * @param objectUrl 路径.
     * @return
     */
    public String getPreviewUrl(String bucket, String objectUrl) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    //.expiry(expireSeconrd)
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectUrl.replace("\\" + bucket + "\\", "")).build());
        } catch (Exception e) {
            throw new ServiceException("获取预览地址错误!");
        }
    }

}
