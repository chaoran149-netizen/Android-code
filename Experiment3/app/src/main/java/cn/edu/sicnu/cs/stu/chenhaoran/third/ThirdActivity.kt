package cn.edu.sicnu.cs.stu.chenhaoran.third

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 * 二级 Activity 2：输入内容，确定后把内容返回给 MainActivity。
 */
class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        supportActionBar?.title = getString(R.string.third_title)

        val editReturn = findViewById<EditText>(R.id.edit_return)

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val content = editReturn.text.toString().trim()
            if (content.isEmpty()) {
                editReturn.error = getString(R.string.third_empty)
                return@setOnClickListener
            }

            // 把结果放进 Intent，通过 setResult 返回给 MainActivity
            val data = Intent().putExtra(MainActivity.EXTRA_REPLY, content)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}
