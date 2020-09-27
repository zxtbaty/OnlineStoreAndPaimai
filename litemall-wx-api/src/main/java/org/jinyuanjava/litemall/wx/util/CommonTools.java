package org.jinyuanjava.litemall.wx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Filename: CommonTools.java
 * @Version: 1.0
 * @Author: zhaihl
 * @Email: zhaihl_0@126.com
 *
 */
public final class CommonTools {

    public static final String inputStream2String(InputStream in) throws UnsupportedEncodingException,
                                                                  IOException {
        if (in == null)
            return "";

        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n, "UTF-8"));
        }
        return out.toString();
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
    * 获取当前时间 yyyyMMddHHmmss
    * @return String
    */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

    /**
    * 取出一个指定长度大小的随机正整数.
    *
    * @param length
    *            int 设定所取出随机数的长度。length小于11
    * @return int 返回生成的随机数。
    */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

}

