package cn.edu.sicnu.cs.stu.chenhaoran.third

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 二级 Activity 1：接收 MainActivity 正向传递过来的参数并显示。
 */
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        supportActionBar?.title = getString(R.string.second_title)

        // 取出正向传递的参数
        val received = intent.getStringExtra(MainActivity.EXTRA_FORWARD_MESSAGE)

        val textReceived = findViewById<TextView>(R.id.text_received)
        textReceived.text = if (received.isNullOrEmpty()) {
            getString(R.string.second_none)
        } else {
            getString(R.string.second_received_format, received)
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }
}
