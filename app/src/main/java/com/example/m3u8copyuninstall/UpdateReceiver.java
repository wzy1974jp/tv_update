package com.example.m3u8copyuninstall;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            return;
        }

        try {
            CopyHelper.copyAssetToDownload(context);
        } catch (Exception ignored) {
            // 广播里不弹窗，避免干扰用户。失败时可手动到设置中打开应用后重试。
        }

        // 覆盖安装后继续保持图标隐藏。
        try {
            ComponentName alias = new ComponentName(context, context.getPackageName() + ".LauncherAlias");
            context.getPackageManager().setComponentEnabledSetting(
                    alias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            );
        } catch (Exception ignored) {}
    }
}
