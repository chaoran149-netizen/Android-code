package cn.edu.sicnu.cs.stu.chenhaoran.third

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 二级 Activity 4：秒表 StopWatch。
 *
 * 重点：横屏 / 竖屏切换时计时器【连续计时】不中断、不归零。
 *
 * 实现思路：
 *  - 累计时间 elapsedMs + 运行状态 isRunning + 起点 startRealtime（SystemClock.elapsedRealtime）
 *  - 真正显示的耗时 = elapsedMs + (elapsedRealtime() - startRealtime)   —— 用系统单调时钟，不受改系统时间影响
 *  - 横竖屏切换会销毁并重建 Activity，用 onSaveInstanceState 把上面 3 个状态存进 Bundle，
 *    在 onCreate / onRestoreInstanceState 里恢复，因此旋转后计时无缝衔接。
 */
class FifthActivity : AppCompatActivity() {

    companion object {
        private const val STATE_ELAPSED = "state_elapsed"
        private const val STATE_RUNNING = "state_running"
        private const val STATE_START_REALTIME = "state_start_realtime"

        /** 刷新间隔（毫秒） */
        private const val TICK_INTERVAL = 50L
    }

    private lateinit var textTime: TextView
    private lateinit var btnToggle: Button

    private val handler = Handler(Looper.getMainLooper())

    /** 已累计的耗时（暂停时结算） */
    private var elapsedMs = 0L

    /** 是否正在计时 */
    private var isRunning = false

    /** 本次开始计时的起点（SystemClock.elapsedRealtime） */
    private var startRealtime = 0L

    /** 定时刷新界面 */
    private val ticker = object : Runnable {
        override fun run() {
            updateDisplay()
            if (isRunning) {
                handler.postDelayed(this, TICK_INTERVAL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth)
        supportActionBar?.title = getString(R.string.fifth_title)

        textTime = findViewById(R.id.text_time)
        btnToggle = findViewById(R.id.btn_toggle)

        // 恢复横竖屏切换前的计时状态
        savedInstanceState?.let {
            elapsedMs = it.getLong(STATE_ELAPSED, 0L)
            isRunning = it.getBoolean(STATE_RUNNING, false)
            startRealtime = it.getLong(STATE_START_REALTIME, 0L)
        }

        btnToggle.setOnClickListener { if (isRunning) pause() else start() }
        findViewById<Button>(R.id.btn_reset).setOnClickListener { reset() }

        if (isRunning) {
            handler.post(ticker)
        } else {
            updateDisplay()
        }
        updateToggleText()
    }

    /** 当前真实耗时 */
    private fun currentElapsed(): Long =
        if (isRunning) elapsedMs + (SystemClock.elapsedRealtime() - startRealtime)
        else elapsedMs

    private fun start() {
        isRunning = true
        startRealtime = SystemClock.elapsedRealtime()
        handler.post(ticker)
        updateToggleText()
    }

    private fun pause() {
        elapsedMs = currentElapsed()
        isRunning = false
        handler.removeCallbacks(ticker)
        updateDisplay()
        updateToggleText()
    }

    private fun reset() {
        handler.removeCallbacks(ticker)
        elapsedMs = 0L
        isRunning = false
        updateDisplay()
        updateToggleText()
    }

    private fun updateToggleText() {
        btnToggle.setText(if (isRunning) R.string.fifth_pause else R.string.fifth_start)
    }

    private fun updateDisplay() {
        val ms = currentElapsed()
        val totalSec = ms / 1000
        val hour = (totalSec / 3600).toInt()
        val minute = ((totalSec % 3600) / 60).toInt()
        val second = (totalSec % 60).toInt()
        val tenth = ((ms % 1000) / 100).toInt()
        textTime.text = getString(R.string.fifth_time_format, hour, minute, second, tenth)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存：累计时间 + 运行状态 + 起点（起点用当前时刻，保证旋转前后无缝衔接）
        outState.putLong(STATE_ELAPSED, if (isRunning) currentElapsed() else elapsedMs)
        outState.putBoolean(STATE_RUNNING, isRunning)
        outState.putLong(STATE_START_REALTIME, SystemClock.elapsedRealtime())
    }

    override fun onStop() {
        super.onStop()
        // 界面不可见时停止刷新，避免资源浪费
        handler.removeCallbacks(ticker)
    }

    override fun onStart() {
        super.onStart()
        // 回到前台且仍在计时，继续刷新
        if (isRunning) handler.post(ticker)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }
}
