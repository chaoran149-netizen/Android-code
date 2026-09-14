# 实验一：多语言版本 Hello World

Android 移动应用开发 实验一 —— 基于纯代码（不使用任何 XML 布局文件）实现的多语言交互式 Hello World App。

## 一、功能说明

- 支持 **中文 / English / 日本語** 三种语言
- 点击底部按钮可实时切换问候语与对应国旗图片（交互式）
- APP 标题为「姓名 + 学号」
- 自定义应用图标（自适应图标 + 多密度 PNG）
- 界面全部使用 Kotlin 代码构建，**未使用任何布局 XML**

## 二、环境

| 项目 | 版本 |
| ---- | ---- |
| JDK | 17 |
| Android Studio | 2026.1.4 (Quail 4) |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

## 三、项目结构

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/helloworld/
│   └── MainActivity.kt            # 纯代码界面（核心）
└── res/
    ├── drawable/
    │   ├── flag_zh.png            # 国旗：中国
    │   ├── flag_en.png            # 国旗：英国
    │   ├── flag_ja.png            # 国旗：日本
    │   └── ic_launcher_foreground.xml
    ├── mipmap-*/                  # 自定义应用图标（多密度）
    ├── mipmap-anydpi-v26/         # 自适应图标
    ├── values/strings.xml         # 默认语言（中文）
    ├── values-en/strings.xml      # 英文
    └── values-ja/strings.xml      # 日文
```

## 四、核心实现

`MainActivity.kt` 中通过 `LinearLayout`、`TextView`、`ImageView`、`Button`
等控件在代码中动态构建界面；多语言通过 `createConfigurationContext` 切换
`Locale` 读取对应的 `values-xx/strings.xml` 实现。

## 五、adb 调试命令

```bash
# 1. 安装 APP（adb shell 方式）
adb push app-debug.apk /data/local/tmp/app-debug.apk
adb shell pm install -r /data/local/tmp/app-debug.apk

# 2. 启动 APP
adb shell am start -n com.example.helloworld/.MainActivity

# 3. 模拟触摸（点击 English 按钮）
adb shell input tap 540 1527

# 4. 进入模拟器根目录
adb shell "cd / && pwd && ls -l /"
```

## 六、Gitee 地址

https://gitee.com/________________________

## 七、运行截图

见 `screenshots/` 目录：
- `01_zh.png` 中文
- `02_en.png` English
- `03_ja.png` 日本語
- `adb_root.txt` adb 显示根目录输出
