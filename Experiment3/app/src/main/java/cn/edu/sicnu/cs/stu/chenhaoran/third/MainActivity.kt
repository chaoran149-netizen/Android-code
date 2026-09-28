package cn.edu.sicnu.cs.stu.chenhaoran.third

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 主 Activity：提供 4 个按钮，分别启动 4 个二级 Activity。
 *
 *  按钮1：正向传参（putExtra）→ SecondActivity 显示接收到的参数
 *  按钮2：startActivityForResult → ThirdActivity 输入内容并返回（setResult）
 *  按钮3：隐式 Intent 调系统相机 → FourthActivity 拍照并显示
 *  按钮4：StopWatch → FifthActivity 横竖屏连续计时
 */
class MainActivity : AppCompatActivity() {

    companion object {
        /** 正向传参的 key */
        const val EXTRA_FORWARD_MESSAGE = "extra_forward_message"

        /** 二级 Activity 2 返回内容的 key */
        const val EXTRA_REPLY = "extra_reply"

        /** 请求码 */
        const val REQUEST_CODE_THIRD = 1001
    }

    private lateinit var textReturned: TextView

    /** 二级 Activity 2 返回的原始内容（用于横竖屏恢复） */
    private var returnedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textReturned = findViewById(R.id.text_returned)

        // 横竖屏切换后恢复已显示的返回结果
        savedInstanceState?.getString(EXTRA_REPLY)?.let {
            returnedText = it
            textReturned.text = getString(R.string.main_returned_format, it)
        }

        // 按钮1：正向传参
        findViewById<Button>(R.id.btn_1).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra(EXTRA_FORWARD_MESSAGE, getString(R.string.main_forward_message))
            startActivity(intent)
        }

        // 按钮2：启动并等待返回结果
        findViewById<Button>(R.id.btn_2).setOnClickListener {
            startActivityForResult(
                Intent(this, ThirdActivity::class.java),
                REQUEST_CODE_THIRD
            )
        }

        // 按钮3：隐式调用相机
        findViewById<Button>(R.id.btn_3).setOnClickListener {
            startActivity(Intent(this, FourthActivity::class.java))
        }

        // 按钮4：秒表
        findViewById<Button>(R.id.btn_4).setOnClickListener {
            startActivity(Intent(this, FifthActivity::class.java))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_THIRD && resultCode == Activity.RESULT_OK) {
            returnedText = data?.getStringExtra(EXTRA_REPLY).orEmpty()
            textReturned.text = getString(R.string.main_returned_format, returnedText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存返回结果，横竖屏切换后不丢失
        outState.putString(EXTRA_REPLY, returnedText)
    }
}
