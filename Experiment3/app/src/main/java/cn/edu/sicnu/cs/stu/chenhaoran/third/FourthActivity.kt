package cn.edu.sicnu.cs.stu.chenhaoran.third

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

/**
 * 二级 Activity 3：用【隐式 Intent】调用系统相机拍照，拍完显示照片。
 *
 * 关键点：
 *  1. Intent(MediaStore.ACTION_IMAGE_CAPTURE)  隐式调用，不需要知道相机应用的类名
 *  2. 通过 FileProvider 给相机应用一个可写的文件 URI（EXTRA_OUTPUT）
 *  3. onActivityResult 里拿到结果后，用 ImageView 显示该照片
 */
class FourthActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_TAKE_PHOTO = 2001
        const val STATE_PHOTO_URI = "state_photo_uri"
    }

    private lateinit var imagePhoto: ImageView
    private lateinit var textNoPhoto: TextView

    /** 相机拍照后照片保存的位置 */
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)
        supportActionBar?.title = getString(R.string.fourth_title)

        imagePhoto = findViewById(R.id.image_photo)
        textNoPhoto = findViewById(R.id.text_no_photo)

        // 横竖屏切换后恢复已拍的照片
        savedInstanceState?.getString(STATE_PHOTO_URI)?.let {
            showPhoto(Uri.parse(it))
        }

        findViewById<Button>(R.id.btn_take_photo).setOnClickListener { takePhoto() }
    }

    /** 隐式 Intent 调用系统相机 */
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, R.string.fourth_no_camera, Toast.LENGTH_SHORT).show()
            return
        }

        // 创建照片文件，并生成 FileProvider 的 content:// URI
        val photoFile = createPhotoFile()
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            photoFile
        )
        photoUri = uri

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        try {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.fourth_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPhotoFile(): File {
        val dir = File(cacheDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "photo_${System.currentTimeMillis()}.jpg")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri?.let { showPhoto(it) }
            } else {
                Toast.makeText(this, R.string.fourth_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPhoto(uri: Uri) {
        photoUri = uri
        imagePhoto.setImageURI(uri)
        imagePhoto.visibility = View.VISIBLE
        textNoPhoto.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        photoUri?.let { outState.putString(STATE_PHOTO_URI, it.toString()) }
    }
}
