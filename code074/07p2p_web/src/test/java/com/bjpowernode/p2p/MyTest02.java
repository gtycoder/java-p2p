package com.bjpowernode.p2p;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyTest02 {
    /**
     * 使用Google的包生成二维码
     */
    public static void main(String[] args) throws WriterException, IOException {
        Map<EncodeHintType,String> encodeMap=new HashMap<>();
        encodeMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //创建一个矩形对象,带有字符集的构造方法
        BitMatrix bitMatrix = new MultiFormatWriter().encode("weixin://wxpay/bizpayurl?pr=Uv3wXRT", BarcodeFormat.QR_CODE, 200, 200, encodeMap);
        //创建一个输出流,输出到哪里
        String path = "E://";
        String name = "test.jpg";

        Path path1 = FileSystems.getDefault().getPath(path, name);
        //实测png这个参数根本就没用
        MatrixToImageWriter.writeToPath(bitMatrix, "png", path1);

        System.out.println("已经生成了");

    }

}
