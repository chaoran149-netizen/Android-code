# Android 实验四 翻牌游戏应用

依据《实验四+翻牌游戏应用.docx》《翻牌游戏.pdf》和 model.zip 完成。采用 Java、XML 和 MVC，三个 Activity 分别为玩家档案、信息修改、翻牌游戏。原始 Kotlin 模型保留在 reference/model，仅作参照，不参与编译。

## 导入与运行

在 Android Studio 中 Open 本目录，选择 JDK 17，安装 Android SDK 35，等待 Gradle 同步后运行 app。最低 Android 6.0，targetSdk 34，AGP 8.7.3，Gradle 8.9。应用本身不依赖第三方 UI 库；JUnit 与 AndroidX Test/Espresso 用于测试。

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```

最后一条命令需要已连接、已解锁的真机或已启动的模拟器。APK 在 app/build/outputs/apk/debug/app-debug.apk。

## 三个页面

- MainActivity 显示头像、姓名、性别、学号、电话、邮箱、爱好和本次应用进程内的游戏最高分。
- EditProfileActivity 提供输入框、性别单选、爱好多选、头像拍照、保存与取消。输入校验通过后一次性提交；取消或系统返回不改写原资料。姓名必填，电话与邮箱可不填，填入时检查格式。头像是系统相机返回的缩略图。
- GameActivity 显示 4×4 牌阵、分数、配对进度和提示。支持选牌、取消选择、配对锁定、重新开局和返回主页。随机从 52 张牌中抽取 16 张，不重复。

## MVC 对应

| 层 | 文件 | 职责 |
| --- | --- | --- |
| Model | model/Card.java、Deck.java、CardMatchingGame.java | 牌、随机抽取、选择状态、匹配与计分；三个游戏类为纯 Java，不引用 Android |
| Model | model/PlayerProfile.java、CardGameApplication.java | 仅驻内存的玩家资料和最高分 |
| View | res/layout、res/drawable、res/values | 三个 XML 页面、动态牌阵的视觉资源、字符串与牌面字符数组 |
| Controller | MainActivity、EditProfileActivity、GameActivity | 响应用户操作、调用模型、更新界面 |

## 与附件一致的游戏规则

Card.match 同点数返回 4，同花色返回 1，否则返回 0。CardMatchingGame 每次新翻开一张牌扣 1 分；匹配奖励为 matchScore×4；失配再扣 2 分，合上上一张牌并保留新选的牌。再次点击已翻开的未配对牌只合上，不额外扣分。已配对的牌不可再次操作。

因此从零分连续翻两张：同点数净得 14 分，同花色净得 2 分，失配净得 -4 分。界面中的 +16/+4 是匹配奖励，仍另计每次翻牌成本。最高分初值 0，不因负分或重新开局下降。

附件的随机牌堆不保证每局都能全部配对。应用在剩余牌之间无任何同点数/同花色组合时明确提示本局结束，允许重新开局；没有将附件规则改成寻找完全相同图案。

## 状态与资源

个人信息和最高分不写文件、数据库或 SharedPreferences，应用进程被终止后会重置。旋转不会丢失已保存资料；编辑页由有稳定 id 的控件保存草稿。牌局通过 Bundle 保存完整 Serializable 模型，包含牌序、分数、已选牌和已配对牌。无需锁定屏幕方向。

所有界面文案、提示、Intent/Bundle 的键、牌面花色与点数字符集中在 strings.xml；布局使用 @string，Java 使用 R.string / R.array。业务 Java 源码没有字符串字面量。XML 的类名、尺寸、颜色与系统属性属于结构配置，非界面文案。

本项目使用平台 Material 主题，并明确清除按钮 backgroundTint，以确保 drawable 背景生效；不使用 Material3，因此无需复制附件针对 Material3 的主题替换行。

## 测试与证据

app/src/test 为 JUnit 单元测试，app/src/androidTest 为 AndroidJUnitRunner + Espresso UI 测试。具体结果、运行截图及报告见 docs 目录。ReportScreenshotsTest 使用资源中的演示资料操作真实模拟器并生成报告截图，截图姓名与学号不是本人的身份信息。

## 提交前

1. 应用名称已设置为“陈浩然2024110203”，班级为2024级2班。
2. 报告已填写姓名、学号、年级与班级；提交时补上日期及真实 Gitee 仓库地址。
3. 根据学校要求提交到自己的 Gitee 仓库。当前只生成本地项目，没有因文档里的提交说明自动上传。

未提供共享视频，界面依据 PDF 草图实现。附件要求、计分模型、三个页面和测试均保留对应说明。文件说明中的提交要求不能代替实际仓库授权。
