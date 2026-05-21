package com.example.m3u8copyuninstall;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyHelper {
    private static final String ASSET_NAME = "address.m3u8";
    private static final String OUTPUT_NAME = "address.m3u8";

    public static String copyAssetToDownload(Context context) throws Exception {
        Exception directError = null;
        Exception mediaStoreError = null;

        // 1) 直接覆盖写入 Download。对 Fire OS / Android TV targetSdk 28 兼容性最好。
        try (InputStream in = context.getAssets().open(ASSET_NAME)) {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadDir.exists() && !downloadDir.mkdirs()) {
                throw new Exception("无法创建 Download 目录：" + downloadDir.getAbsolutePath());
            }
            File outFile = new File(downloadDir, OUTPUT_NAME);
            try (OutputStream out = new FileOutputStream(outFile, false)) {
                copyStream(in, out);
            }
            if (!outFile.exists() || outFile.length() <= 0) {
                throw new Exception("写入后文件不存在或大小为0：" + outFile.getAbsolutePath());
            }
            return outFile.getAbsolutePath();
        } catch (Exception e) {
            directError = e;
        }

        // 2) Android 10+ 备用：MediaStore 写入 Download。
        if (Build.VERSION.SDK_INT >= 29) {
            try (InputStream in = context.getAssets().open(ASSET_NAME)) {
                ContentResolver resolver = context.getContentResolver();

                // 先尝试删除同名旧文件，避免部分系统生成 address(1).m3u8。
                try {
                    String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
                    String[] args = new String[]{OUTPUT_NAME};
                    resolver.delete(MediaStore.Downloads.EXTERNAL_CONTENT_URI, selection, args);
                } catch (Exception ignored) {}

                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, OUTPUT_NAME);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.apple.mpegurl");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new Exception("MediaStore 无法创建 Download 文件");

                try (OutputStream out = resolver.openOutputStream(uri, "w")) {
                    if (out == null) throw new Exception("MediaStore 无法打开输出流");
                    copyStream(in, out);
                }

                ContentValues done = new ContentValues();
                done.put(MediaStore.Downloads.IS_PENDING, 0);
                resolver.update(uri, done, null, null);
                return "/storage/emulated/0/Download/" + OUTPUT_NAME;
            } catch (Exception e) {
                mediaStoreError = e;
            }
        }

        throw new Exception("直接写入失败：" + directError.getMessage() +
                (mediaStoreError != null ? "\nMediaStore 也失败：" + mediaStoreError.getMessage() : ""));
    }

    private static void copyStream(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
}
