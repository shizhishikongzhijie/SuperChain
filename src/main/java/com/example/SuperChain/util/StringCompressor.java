package com.example.SuperChain.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class StringCompressor {

    // 字符串gzip压缩
    public static String gzip(String primStr) {
        if (primStr == null || primStr.isEmpty()) {
            return primStr;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(primStr.getBytes(StandardCharsets.UTF_8));
            gzip.finish();
            byte[] compressedBytes = out.toByteArray();
            return Base64.getEncoder().encodeToString(compressedBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error while compressing string", e);
        }
    }

    // 字符串的解压
    public static String ungzip(String compressedStr) {
        if (compressedStr == null || compressedStr.isEmpty()) {
            return null;
        }

        try (ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(compressedStr));
             GZIPInputStream ginzip = new GZIPInputStream(in);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException("Error while decompressing string", e);
        }
    }
}