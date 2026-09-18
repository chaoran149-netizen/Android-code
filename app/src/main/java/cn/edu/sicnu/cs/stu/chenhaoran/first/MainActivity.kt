package cn.edu.sicnu.cs.stu.chenhaoran.first

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * 实验一：多语言版本 Hello World
 * 界面全部使用 Kotlin 代码实现（不使用任何 XML 布局文件）。
 * 点击底部按钮可切换 中文 / English / 日本語 三种语言，同时切换对应国旗。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var flagView: ImageView
    private lateinit var greetingView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------- 纯代码构建界面：根布局 ----------
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(60, 60, 60, 60)
            setBackgroundColor(Color.WHITE)
        }

        // ---------- 顶部标题：姓名 + 学号 ----------
        val titleView = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 20f
            setTextColor(Color.parseColor("#333333"))
            gravity = Gravity.CENTER
        }

        // ---------- 国旗图片 ----------
        flagView = ImageView(this).apply {
            setImageResource(R.drawable.flag_zh)
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(dp(180), dp(120)).apply {
                topMargin = dp(24)
            }
        }

        // ---------- 多语言问候语 ----------
        greetingView = TextView(this).apply {
            text = localizedString(R.string.hello_world, "zh")
            textSize = 32f
            setTextColor(Color.parseColor("#1565C0"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(24) }
        }

        root.addView(titleView)
        root.addView(flagView)
        root.addView(greetingView)

        // ---------- 三个语言切换按钮（交互） ----------
        root.addView(makeButton(R.string.btn_zh, "zh"))
        root.addView(makeButton(R.string.btn_en, "en"))
        root.addView(makeButton(R.string.btn_ja, "ja"))

        setContentView(root)
    }

    /** 纯代码创建按钮 */
    private fun makeButton(labelResId: Int, lang: String): Button {
        return Button(this).apply {
            setText(labelResId)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(16) }
            setOnClickListener { switchLanguage(lang) }
        }
    }

    /** 切换语言：更新问候语与国旗 */
    private fun switchLanguage(lang: String) {
        greetingView.text = localizedString(R.string.hello_world, lang)
        val flagResId = when (lang) {
            "zh" -> R.drawable.flag_zh
            "en" -> R.drawable.flag_en
            else -> R.drawable.flag_ja
        }
        flagView.setImageResource(flagResId)
    }

    /** 根据语言代码读取对应 values-xx/strings.xml 中的字符串 */
    private fun localizedString(resId: Int, lang: String): String {
        val locale = when (lang) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "ja" -> Locale.JAPANESE
            else -> Locale.ENGLISH
        }
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        return createConfigurationContext(config).getString(resId)
    }

    /** dp 转 px */
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
