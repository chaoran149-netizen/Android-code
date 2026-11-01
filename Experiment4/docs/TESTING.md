# 实验四 测试记录

执行日期：2026-10-26。环境：Windows、JDK 17、Gradle 8.9、Android SDK 35、HelloWorldAVD / Android 15 (API 35)。

## 结果

| 检查 | 结果 |
| --- | --- |
| assembleDebug | BUILD SUCCESSFUL |
| JUnit 单元测试 | 12 / 12 通过，0 失败，0 跳过 |
| AndroidJUnitRunner + Espresso | 8 / 8 通过，0 失败，0 跳过 |
| 页面截图 | 4 张真实模拟器截图，已逐张检查 |

12 项单元测试覆盖配对优先级、52 张不重复抽牌、翻牌成本、取消选择不扣分、同点数净得14分、同花色净得2分、失配净扣4分、已配对不可重复得分、剩余无组合、随机16张不重复、序列化恢复、非法牌数/重复牌以及最高分不回落。其中多项测试在同一方法中验证相关断言，方法总数以 XML 为准。

8 项 UI 测试包括7项功能用例及1项带断言的完整演示截图流程：保存并显示全部资料、取消不改写、空姓名校验、错误邮箱校验、翻牌与 Activity 重建和重开、配对锁牌与最高分回传、编辑草稿重建恢复、截图演示。

报告与截图使用“实验玩家 / 20260001 / 10086 / player@example.com”演示，不代表真实用户信息。测试中的页面重建通过 ActivityScenario.recreate 触发；不将其描述为已完成真机全机型兼容测试。头像相机功能已实现，但没有纳入这8项自动化用例。

## 复现

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```

在 Android Studio 中也可右键 app/src/test 或 app/src/androidTest 的测试类运行。连接设备并解锁；建议在测试设备上关闭过渡动画后采集截图。XML 原始结果在 test-results/unit.xml 和 ui.xml，方法清单在 summary.json。

## 静态检查

清单中3个Activity；三个游戏模型类不引用Android；编译业务源码无字符串字面量；布局文案、提示和内容描述全部引用字符串资源。提供的reference/model是原始附件，不作为业务代码参与编译。
