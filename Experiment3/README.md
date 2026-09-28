# 实验三：Intent、多 Activity、生命周期与横竖屏数据保存

Android 移动应用开发 实验三。

## 一、实验目的

1. 掌握 Intent 的用法
2. 掌握 Android 多 Activity 开发
3. 掌握 Activity 生命周期
4. 掌握横屏竖屏改变的时候信息的保存和恢复

## 二、实验内容

包含 **5 个 Activity**（1 个 MainActivity + 4 个二级 Activity）：

| 按钮 | 二级 Activity | 功能 | 关键技术 |
|------|---------------|------|----------|
| 1 | `SecondActivity` | 正向传参，显示接收到的参数 | `putExtra` / `getStringExtra` |
| 2 | `ThirdActivity` | 输入内容，确定后返回 MainActivity 并显示 | `startActivityForResult` / `setResult` / `onActivityResult` |
| 3 | `FourthActivity` | **隐式调用**系统相机拍照并显示照片 | `ACTION_IMAGE_CAPTURE` + `FileProvider` |
| 4 | `FifthActivity` | StopWatch 秒表，**横竖屏连续计时** | `onSaveInstanceState` / `SystemClock.elapsedRealtime` |

## 三、核心技术说明

### 1. 正向传参（按钮1）
```kotlin
// MainActivity
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra(EXTRA_FORWARD_MESSAGE, getString(R.string.main_forward_message))
startActivity(intent)

// SecondActivity
val received = intent.getStringExtra(MainActivity.EXTRA_FORWARD_MESSAGE)
```

### 2. 返回数据（按钮2）
```kotlin
// MainActivity
startActivityForResult(Intent(this, ThirdActivity::class.java), REQUEST_CODE_THIRD)

// ThirdActivity
setResult(Activity.RESULT_OK, Intent().putExtra(MainActivity.EXTRA_REPLY, content))
finish()

// MainActivity
override fun onActivityResult(requestCode, resultCode, data) { ... }
```

### 3. 隐式 Intent 调用相机（按钮3）
```kotlin
val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)     // 隐式 Intent
val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
startActivityForResult(intent, REQUEST_TAKE_PHOTO)
```
配套：`AndroidManifest.xml` 中注册 `FileProvider`，`res/xml/file_paths.xml` 声明可写目录。

### 4. 秒表横竖屏连续计时（按钮4）
横竖屏切换会**销毁并重建** Activity，所以计时状态必须保存：

| 状态 | 说明 |
|------|------|
| `elapsedMs` | 已累计的耗时 |
| `isRunning` | 是否正在计时 |
| `startRealtime` | 起点（`SystemClock.elapsedRealtime`，单调时钟） |

```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    outState.putLong(STATE_ELAPSED, if (isRunning) currentElapsed() else elapsedMs)
    outState.putBoolean(STATE_RUNNING, isRunning)
    outState.putLong(STATE_START_REALTIME, SystemClock.elapsedRealtime())
}
```
重建后在 `onCreate` 中恢复，`currentElapsed() = elapsedMs + (elapsedRealtime() - startRealtime)`，
所以旋转前后**无缝衔接、不归零**。

## 四、无硬编码字符串

所有界面文字（标题、按钮、提示、格式串）**全部放在 `res/values/strings.xml`**，
布局与 Kotlin 代码中统一通过 `@string/xxx` 与 `getString(R.string.xxx)` 引用。
APP 标题为 `陈浩然2024110203`。

## 五、环境

| 项目 | 版本 |
| ---- | ---- |
| JDK | 17 |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

## 六、项目结构

```
app/src/main/
├── AndroidManifest.xml                 # 注册 5 个 Activity + FileProvider
├── java/cn/edu/sicnu/cs/stu/chenhaoran/third/
│   ├── MainActivity.kt                 # 4 个按钮
│   ├── SecondActivity.kt               # 正向传参
│   ├── ThirdActivity.kt                # 返回数据
│   ├── FourthActivity.kt               # 隐式相机
│   └── FifthActivity.kt                # StopWatch
└── res/
    ├── layout/activity_main.xml / activity_second.xml / activity_third.xml
    │        / activity_fourth.xml / activity_fifth.xml
    ├── values/strings.xml              # 所有字符串资源
    ├── xml/file_paths.xml              # FileProvider 路径
    └── mipmap-*/                       # APP 图标
```

## 七、运行截图

见 `screenshots/`：

| 文件 | 内容 |
|------|------|
| `01_main.png` | MainActivity（4 个按钮） |
| `02_second_forward.png` | 二级1 显示接收到的参数 |
| `03_third_input.png` | 二级2 输入内容 |
| `04_main_returned.png` | MainActivity 显示返回结果 |
| `05_camera_photo.png` | 二级3 拍照后显示照片 |
| `06_stopwatch_portrait.png` | 二级4 竖屏计时 |
| `07_stopwatch_landscape.png` | 二级4 横屏计时（连续） |
| `code_*.png` | 核心代码截图 |

## 八、验证结果

| 要求 | 结果 |
|------|------|
| 按钮1 正向传参并显示 | ✅ 显示「这是 MainActivity 正向传递的参数：陈浩然 2024110203」 |
| 按钮2 返回数据并显示 | ✅ 输入 `Hello-Android` → MainActivity 显示该内容 |
| 按钮3 隐式调用相机并显示照片 | ✅ 相机拍摄 → 照片保存至 `cache/images/` 并显示 |
| 按钮4 横竖屏连续计时 | ✅ 旋转前 4.5s → 旋转后 13.9s，**未归零** |
