GitHub Actions 自动打包说明

第一次使用：
1. 在 GitHub 新建一个空仓库。
2. 把本文件夹内所有内容上传到仓库根目录。
   注意：必须能看到 app、build.gradle、settings.gradle、.github 这几个项目。
3. 打开仓库页面 → Actions。
4. 左侧选择 Build Android APK。
5. 点击 Run workflow。
6. 等待构建完成后，进入本次运行结果页面。
7. 页面底部 Artifacts 下载：M3U8CopyHide-debug-apk。
8. 解压后得到 M3U8CopyHide-debug.apk。

以后更新 m3u8：
1. 替换 app/src/main/assets/address.m3u8。
2. 修改 app/build.gradle 里的 versionCode，例如 1 改成 2、3、4。
3. 提交到 GitHub。
4. Actions 会自动打包；或者手动 Run workflow。

重要：
- applicationId 必须保持不变，否则不能覆盖安装旧版。
- Debug APK 也可以覆盖安装，但每次必须由同一个工程打包，签名才一致。
- 如果以后换电脑或换工程，最好改用固定 release 签名。
