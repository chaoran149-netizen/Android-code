# 实验二：UI 控件动态生成 + Android MVC（ProgramAdviser）

Android 移动应用开发 实验二。

## 一、实验目的

1. 掌握 UI 控件用代码进行动态生成
2. 掌握 Android MVC 开发模式

## 二、功能说明

界面分为两个功能区：

### 功能区 1：代码动态添加控件
- 输入文字后点击「添加 TextView」
- 用 **Kotlin 代码动态 new 出 TextView**，并 `addView` 到布局中
- 采用 **ScrollView 嵌套 LinearLayout**，实现多个 TextView 的滚动显示
- 每添加一项自动滚动到底部

### 功能区 2：ProgramAdviser（MVC 模式）
- 采用 **MVC 三层结构**：
  - **Model**：`model/ProgramAdviserModel.kt` —— 编程学习顾问知识库与查询逻辑
  - **View**：`res/layout/activity_main.xml` + 绑定的控件
  - **Controller**：`MainActivity.kt` 中的按钮事件，调用 Model 并更新 View
- 输入编程关键词（如 Java / Kotlin / Python），点击「查询建议」
- 控制器查询模型层，返回对应建议并显示在界面上

## 三、无硬编码字符串

所有界面文字（标题、按钮、提示、Model 的建议内容）**全部放在
`res/values/strings.xml`** 中，布局文件与 Kotlin 代码中均不出现硬编码字符串，
统一通过 `@string/xxx` 与 `getString(R.string.xxx)` 引用。

## 四、环境

| 项目 | 版本 |
| ---- | ---- |
| JDK | 17 |
| Android Studio | 2026.1.4 (Quail 4) |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

## 五、项目结构

```
app/src/main/
├── AndroidManifest.xml
├── java/cn/edu/sicnu/cs/stu/chenhaoran/second/
│   ├── MainActivity.kt              # View + Controller
│   └── model/
│       └── ProgramAdviserModel.kt   # Model
└── res/
    ├── layout/activity_main.xml
    ├── values/strings.xml           # 所有字符串资源
    ├── values/colors.xml
    ├── values/themes.xml
    └── mipmap-*/                    # APP 图标
```

## 六、运行截图

见 `screenshots/`：
- `01_init.png` —— 初始界面
- `02_added_textviews.png` —— 功能区1 动态添加多个 TextView
- `03_query_result.png` —— 功能区2 查询结果
- `code_*.png` —— 核心代码截图

## 七、代码仓库地址

https://github.com/chaoran149-netizen/Android-code/tree/main/ProgramAdviser
