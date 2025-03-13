package com.example.SuperChain.util;
import java.math.BigInteger;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @author shizhishi
 */
public class BigTimestampConverter {
    public static String bigIntegerToDateString(String bigTimestampStr) {
        // 假设这是从Solidity函数得到的大整数时间戳
        if (bigTimestampStr == null || bigTimestampStr.isEmpty()) {
            return "2023-04-01";
        }
        BigInteger bigTimestamp = new BigInteger(bigTimestampStr);

        // 如果时间戳是以秒为单位，则乘以1000转换为毫秒
        BigInteger milliseconds = bigTimestamp.multiply(BigInteger.valueOf(1000));

        // 将BigInteger转换为long，这里假设它可以被安全地转换
        long timestampMillis = milliseconds.longValue();

        // 创建Date对象
        Date date = new Date(timestampMillis);

        // 使用SimpleDateFormat定义输出格式
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 转换并打印出字符串
        return sdf.format(date);
    }
}