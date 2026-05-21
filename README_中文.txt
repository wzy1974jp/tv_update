M3U8 Copy Hide v7

功能：
1. 启动后把 assets/address.m3u8 静默复制/覆盖到：
   /storage/emulated/0/Download/address.m3u8
2. 不弹出自定义确认窗口。
3. 复制完成后自动隐藏桌面/应用面板图标。
4. 自动退出。
5. 以后发布新版 APK 时，只要保持 applicationId 和签名不变、versionCode 递增，即可覆盖安装。
6. 覆盖安装后即使图标仍然隐藏，也会通过 MY_PACKAGE_REPLACED 广播自动重新复制新版 address.m3u8。

重要限制：
- 普通第三方 APK 不能无确认静默安装/覆盖安装其它 APK。
- 覆盖安装本身仍由系统安装器处理，是否弹出安装确认框取决于设备系统。
- 真正无弹窗静默安装需要 Root、ADB install 权限、Device Owner、系统签名或企业 MDM。

复制目标只保留：
/storage/emulated/0/Download/address.m3u8
