package com.example.cameraxproject.activity.driver

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cameraxproject.R
import com.example.cameraxproject.util.CameraConstant
import com.example.cameraxproject.util.CameraParam
import com.permissionx.guolindev.PermissionX

class DriverActivity : AppCompatActivity() {
    private var tv_camera: TextView? = null
    private var img_picture: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_driver)

//        startActivity(new Intent(this, BitmapSizeActivity.class));
        tv_camera = findViewById(R.id.tv_camera)
        img_picture = findViewById(R.id.img_picture)

        //!!!必选要有权限拍照和文件存储权限
        tv_camera!!.setOnClickListener(View.OnClickListener { v: View? ->
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                    if (allGranted) {
                        val mCameraParam: CameraParam = CameraParam.Builder()
                            .setActivity(this@DriverActivity)
                            .build()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "These permissions are denied: \$deniedList",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //requestCode默认是CameraConstant.REQUEST_CODE ，当然也可以在上面的CameraParam创建的时候
        //调用setRequestCode修改
        if (requestCode == CameraConstant.REQUEST_CODE && resultCode == RESULT_OK) {
            //获取图片路径
            val picturePath = data!!.getStringExtra(CameraConstant.PICTURE_PATH_KEY)
            //显示出来
            img_picture!!.visibility = View.VISIBLE
            img_picture!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }
}