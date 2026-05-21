package com.example.m3u8copyuninstall;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQ_WRITE_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29 &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQ_WRITE_STORAGE);
        } else {
            copyHideAndExit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        copyHideAndExit();
    }

    private void copyHideAndExit() {
        try {
            CopyHelper.copyAssetToDownload(this);
            Toast.makeText(this, "导入完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "导入失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        hideLauncherIcon();
        finishAndRemoveTask();
    }

    private void hideLauncherIcon() {
        try {
            ComponentName alias = new ComponentName(this, getPackageName() + ".LauncherAlias");
            getPackageManager().setComponentEnabledSetting(
                    alias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            );
        } catch (Exception ignored) {}
    }
}
