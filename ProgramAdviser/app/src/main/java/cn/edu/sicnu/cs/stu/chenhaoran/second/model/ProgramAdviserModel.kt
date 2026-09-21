package cn.edu.sicnu.cs.stu.chenhaoran.second.model

import cn.edu.sicnu.cs.stu.chenhaoran.second.R

/**
 * ==================== MVC 中的 Model 层 ====================
 * 负责【数据 + 业务逻辑】：ProgramAdviser 的"编程学习顾问"知识库。
 *
 * 说明：这里存放的"建议文本"全部以【字符串资源 id】的形式保存，
 *       模型本身不出现任何硬编码字符串，由 View 层负责解析显示。
 */
class ProgramAdviserModel {

    /** 关键词 -> 建议文本的资源 id */
    private val adviceTable: Map<String, Int> = mapOf(
        "java" to R.string.advice_java,
        "kotlin" to R.string.advice_kotlin,
        "python" to R.string.advice_python,
        "c" to R.string.advice_c,
        "c++" to R.string.advice_cpp,
        "cpp" to R.string.advice_cpp,
        "android" to R.string.advice_android,
        "javascript" to R.string.advice_javascript,
        "js" to R.string.advice_javascript,
        "html" to R.string.advice_html,
        "sql" to R.string.advice_sql,
        "git" to R.string.advice_git
    )

    /**
     * 查询建议（业务逻辑）
     *
     * @param input 用户输入的关键词
     * @return 匹配到的【字符串资源 id】；输入为空返回 advice_empty；
     *         未匹配返回 advice_not_found。View 层再用它去取真正的文本。
     */
    fun query(input: String?): Int {
        if (input.isNullOrBlank()) {
            return R.string.advice_empty
        }
        val key = input.trim().lowercase()

        // 1) 关键词精确匹配
        adviceTable[key]?.let { return it }

        // 2) 包含匹配（例如输入 "我爱kotlin" 也能命中 kotlin）
        for ((k, resId) in adviceTable) {
            if (key.contains(k)) return resId
        }

        // 3) 未命中
        return R.string.advice_not_found
    }
}
