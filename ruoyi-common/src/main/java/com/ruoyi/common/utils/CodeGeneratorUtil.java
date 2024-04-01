package com.ruoyi.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ruoyi.common.utils.file.FileUploadUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 二维码、条形码生成工具类
 */
public class CodeGeneratorUtil {

    /**
     * 生成二维码
     * @param text 内容
     * @param width 宽
     * @param height 高
     * @param filePath 生成路径(完整路径加文件名，例如：/home/xxx.png)
     * @throws WriterException
     * @throws IOException
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);

        File file = new File(filePath.substring(0,filePath.lastIndexOf("/")));
        // 路径为文件且不为空则进行删除
        if (!file.exists()) {
            file.mkdir();
        }

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    }

    /**
     * 生成条形码（一维码Code 128）
     * @param content 内容
     * @param paths 生成的图片路径
     * @throws Exception
     */
    public static void generateBarCode128(String content, String paths) throws Exception{
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, 100, 50);
        Path path = Paths.get(paths);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 生成条形码(一维码Code 39)
     * @param content 内容
     * @param paths 生成的图片路径
     * @throws Exception
     */
    public static void generateBarCode39(String content, String paths) throws Exception{
        Code39Writer writer = new Code39Writer();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_39, 100, 50);
        Path path = Paths.get(paths);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


}
