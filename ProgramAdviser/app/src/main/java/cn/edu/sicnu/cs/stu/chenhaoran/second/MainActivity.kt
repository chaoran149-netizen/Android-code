package cn.edu.sicnu.cs.stu.chenhaoran.second

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.chenhaoran.second.model.ProgramAdviserModel

/**
 * ==================== MVC 中的 View + Controller ====================
 *
 *  - View（视图）：activity_main.xml 中的界面 + 下方绑定的各个控件
 *  - Controller（控制器）：两个按钮的点击事件，负责调用 Model 并更新 View
 *  - Model（模型）：ProgramAdviserModel（数据处理与查询逻辑）
 */
class MainActivity : AppCompatActivity() {

    // ---------- Model ----------
    private val adviserModel = ProgramAdviserModel()

    // ---------- View ----------
    private lateinit var editAdd: EditText
    private lateinit var editQuery: EditText
    private lateinit var containerDynamic: LinearLayout
    private lateinit var textAdvice: TextView

    /** 已动态添加的 TextView 计数 */
    private var addedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 绑定控件（View）
        editAdd = findViewById(R.id.edit_add)
        editQuery = findViewById(R.id.edit_query)
        containerDynamic = findViewById(R.id.container_dynamic)
        textAdvice = findViewById(R.id.text_advice)
        val btnAdd: Button = findViewById(R.id.btn_add)
        val btnQuery: Button = findViewById(R.id.btn_query)

        // ---------- Controller：功能区1，代码动态生成控件 ----------
        btnAdd.setOnClickListener {
            addDynamicTextView()
        }

        // ---------- Controller：功能区2，查询 Model 并返回结果 ----------
        btnQuery.setOnClickListener {
            val input = editQuery.text.toString()
            val adviceResId = adviserModel.query(input)   // 调用 Model
            textAdvice.setText(adviceResId)               // 更新 View
        }
    }

    /**
     * 功能区 1 核心：用代码动态创建一个 TextView，
     * 并添加到 ScrollView 内嵌的 LinearLayout 中，实现滚动显示。
     */
    private fun addDynamicTextView() {
        val content = editAdd.text.toString().trim()
        if (content.isEmpty()) {
            editAdd.error = getString(R.string.section1_empty)
            return
        }

        addedCount++

        val dynamicTv = TextView(this).apply {
            text = getString(R.string.section1_added_item, addedCount, content)
            textSize = 16f
            setPadding(28, 22, 28, 22)
            setBackgroundColor(getColor(R.color.item_bg))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 8 }
        }

        // 动态添加到 LinearLayout
        containerDynamic.addView(dynamicTv)

        // 自动滚动到底部，方便看到新添加的控件
        (containerDynamic.parent as? ScrollView)?.post {
            (containerDynamic.parent as? ScrollView)?.fullScroll(ScrollView.FOCUS_DOWN)
        }

        editAdd.text.clear()
    }
}
