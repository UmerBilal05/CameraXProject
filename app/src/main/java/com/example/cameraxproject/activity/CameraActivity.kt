package com.example.cameraxproject.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.internal.annotation.CameraExecutor
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.cameraxproject.R
import com.example.cameraxproject.util.*
import com.example.cameraxproject.util.overlay.GraphicOverlay
import com.example.cameraxproject.util.overlay.PreferenceUtils
import com.google.mlkit.md.barcodedetection.BarcodeReticleGraphic
import com.google.mlkit.md.camera.CameraReticleAnimator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CameraActivity : AppCompatActivity() {
    private var previewView: PreviewView? = null
    private var img_switch: ImageView? = null
    private var _ll_picture_parent: LinearLayout? = null
    val ll_picture_parent get() = _ll_picture_parent!!
    private var _img_picture: ImageView? = null
    val img_picture get() = _img_picture!!
    private var focus_view: FocusView? = null
    private var view_mask: View? = null
    private var _rl_result_picture: RelativeLayout? = null
    val rl_result_picture get() = _rl_result_picture!!
    private var img_picture_cancel: ImageView? = null
    private var _img_picture_save: ImageView? = null
    val img_picture_save get() = _img_picture_save!!

    private var _rl_start: RelativeLayout? = null
    val rl_start get() = _rl_start!!
    private var tv_back: TextView? = null
    private var img_take_photo: ImageView? = null
    private var imageCapture: ImageCapture? = null
    private var mCameraControl: CameraControl? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var mCameraParam: CameraParam? = null
    private var front = false
    lateinit var graphicOverlay: GraphicOverlay
    private lateinit var cameraReticleAnimator: CameraReticleAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_camera)
        mCameraParam = intent.getParcelableExtra(CameraConstant.CAMERA_PARAM_KEY)
        requireNotNull(mCameraParam) { "CameraParam is null" }
        if (!Tools.checkPermission(this)) {
            throw NoPermissionException("需要有拍照权限和存储权限")
        }
        front = mCameraParam!!.isFront
        initView()
        setViewParam()
        intCamera()

    }

    private fun setViewParam() {
        //是否显示切换按钮
        if (mCameraParam!!.isShowSwitch) {
            img_switch!!.visibility = View.VISIBLE
            if (mCameraParam!!.switchSize != -1 || mCameraParam!!.switchLeft != -1 || mCameraParam!!.switchTop != -1) {
                val layoutParams = img_switch!!.layoutParams as ConstraintLayout.LayoutParams
                if (mCameraParam!!.switchSize != -1) {
                    layoutParams.height = mCameraParam!!.switchSize
                    layoutParams.width = layoutParams.height
                }
                if (mCameraParam!!.switchLeft != -1) {
                    layoutParams.leftMargin = mCameraParam!!.switchLeft
                }
                if (mCameraParam!!.switchTop != -1) {
                    layoutParams.topMargin = mCameraParam!!.switchTop
                }
                img_switch!!.layoutParams = layoutParams
            }
            if (mCameraParam!!.switchImgId != -1) {
                img_switch!!.setImageResource(mCameraParam!!.switchImgId)
            }
        } else {
            img_switch!!.visibility = View.GONE
        }

        //是否显示裁剪框
        if (mCameraParam!!.isShowMask) {
            view_mask!!.visibility = View.VISIBLE
            if (mCameraParam!!.maskMarginLeftAndRight != -1 || mCameraParam!!.maskMarginTop != -1 || mCameraParam!!.maskRatioH != -1) {
                val layoutParams = view_mask!!.layoutParams as ConstraintLayout.LayoutParams
                if (mCameraParam!!.maskMarginLeftAndRight != -1) {
                    layoutParams.rightMargin = mCameraParam!!.maskMarginLeftAndRight
                    layoutParams.leftMargin = layoutParams.rightMargin
                }
                if (mCameraParam!!.maskMarginTop != -1) {
                    layoutParams.topMargin = mCameraParam!!.maskMarginTop
                }
                if (mCameraParam!!.maskRatioH != -1) {
                    Tools.reflectMaskRatio(
                        view_mask!!,
                        mCameraParam!!.maskRatioW,
                        mCameraParam!!.maskRatioH
                    )
                }
                view_mask!!.layoutParams = layoutParams
            }
            if (mCameraParam!!.maskImgId != -1) {
                view_mask!!.setBackgroundResource(mCameraParam!!.maskImgId)
            }
        } else {
            view_mask!!.visibility = View.GONE
        }
        if (mCameraParam!!.backText != null) {
            tv_back!!.text = mCameraParam!!.backText
        }
        if (mCameraParam!!.backColor != -1) {
            tv_back!!.setTextColor(mCameraParam!!.backColor)
        }
        if (mCameraParam!!.backSize != -1) {
            tv_back!!.textSize = mCameraParam!!.backSize.toFloat()
        }
        if (mCameraParam!!.takePhotoSize != -1) {
            val size = mCameraParam!!.takePhotoSize
            val pictureCancelParams = img_picture_cancel!!.layoutParams
            pictureCancelParams.height = size
            pictureCancelParams.width = pictureCancelParams.height
            img_picture_cancel!!.layoutParams = pictureCancelParams
            val pictureSaveParams = img_picture_save!!.layoutParams
            pictureSaveParams.height = size
            pictureSaveParams.width = pictureSaveParams.height
            img_picture_save!!.layoutParams = pictureSaveParams
            val takePhotoParams = img_take_photo!!.layoutParams
            takePhotoParams.height = size
            takePhotoParams.width = takePhotoParams.height
            img_take_photo!!.layoutParams = takePhotoParams
        }
        focus_view!!.setParam(
            mCameraParam!!.focusViewSize,
            mCameraParam!!.focusViewColor,
            mCameraParam!!.focusViewTime,
            mCameraParam!!.focusViewStrokeSize,
            mCameraParam!!.cornerViewSize
        )
        if (mCameraParam!!.cancelImgId != -1) {
            img_picture_cancel!!.setImageResource(mCameraParam!!.cancelImgId)
        }
        if (mCameraParam!!.saveImgId != -1) {
            img_picture_save!!.setImageResource(mCameraParam!!.saveImgId)
        }
        if (mCameraParam!!.takePhotoImgId != -1) {
            img_take_photo!!.setImageResource(mCameraParam!!.takePhotoImgId)
        }
        if (mCameraParam!!.resultBottom != -1) {
            val resultPictureParams =
                rl_result_picture!!.layoutParams as ConstraintLayout.LayoutParams
            resultPictureParams.bottomMargin = mCameraParam!!.resultBottom
            rl_result_picture!!.layoutParams = resultPictureParams
            val startParams = rl_start!!.layoutParams as ConstraintLayout.LayoutParams
            startParams.bottomMargin = mCameraParam!!.resultBottom
            rl_start!!.layoutParams = startParams
        }
        if (mCameraParam!!.resultLeftAndRight != -1) {
            val pictureCancelParams =
                img_picture_cancel!!.layoutParams as RelativeLayout.LayoutParams
            pictureCancelParams.leftMargin = mCameraParam!!.resultLeftAndRight
            img_picture_cancel!!.layoutParams = pictureCancelParams
            val pictureSaveParams = img_picture_save!!.layoutParams as RelativeLayout.LayoutParams
            pictureSaveParams.rightMargin = mCameraParam!!.resultLeftAndRight
            img_picture_save!!.layoutParams = pictureSaveParams
        }
        if (mCameraParam!!.backLeft != -1) {
            val layoutParams = tv_back!!.layoutParams as RelativeLayout.LayoutParams
            layoutParams.leftMargin = mCameraParam!!.backLeft
            tv_back!!.layoutParams = layoutParams
        }
        Tools.reflectPreviewRatio(previewView!!, Tools.aspectRatio(this))
    }

    private fun initView() {
        graphicOverlay = findViewById(R.id.graphicoverlay)
        previewView = findViewById(R.id.previewView)
        img_switch = findViewById(R.id.img_switch)
        _ll_picture_parent = findViewById(R.id.ll_picture_parent)
        _img_picture = findViewById(R.id.img_picture)
        focus_view = findViewById(R.id.focus_view)
        view_mask = findViewById(R.id.view_mask)
        _rl_result_picture = findViewById(R.id.rl_result_picture)
        img_picture_cancel = findViewById(R.id.img_picture_cancel)
        _img_picture_save = findViewById(R.id.img_picture_save)
        _rl_start = findViewById(R.id.rl_start)
        tv_back = findViewById(R.id.tv_back)
        img_take_photo = findViewById(R.id.img_take_photo)

        //切换相机
        img_switch!!.setOnClickListener(View.OnClickListener { v: View? ->
            switchOrition()
            bindCameraUseCases()
        })

        //拍照成功然后点取消
        img_picture_cancel!!.setOnClickListener(View.OnClickListener { v: View? ->
            img_picture.setImageBitmap(null)
            rl_start.setVisibility(View.VISIBLE)
            rl_result_picture.setVisibility(View.GONE)
            ll_picture_parent.setVisibility(View.GONE)
        })
        //拍照成功然后点保存
        img_picture_save.setOnClickListener(View.OnClickListener { v: View? -> savePicture() })
        //还没拍照就点取消
        tv_back!!.setOnClickListener(View.OnClickListener { v: View? -> finish() })
        //点击拍照
        img_take_photo!!.setOnClickListener(View.OnClickListener { v: View? ->
            takePhoto(mCameraParam!!.pictureTempPath)
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            autoFocus(event.x.toInt(), event.y.toInt(), false)
        }
        return super.onTouchEvent(event)
    }

    private fun switchOrition() {
        front = if (front) {
            false
        } else {
            true
        }
    }

    private fun intCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.d("wld________", e.toString())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        cameraReticleAnimator = CameraReticleAnimator(previewView!!)
        ll_picture_parent.viewTreeObserver.addOnDrawListener {
            graphicOverlay.clear()
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(previewView!!, cameraReticleAnimator))
            graphicOverlay.invalidate()
            val externalrect=PreferenceUtils.getBarcodeReticleBox(previewView!!)
            with(view_mask!!){
                left=externalrect.left.toInt()
                top=externalrect.top.toInt()
                right=externalrect.right.toInt()
                bottom=externalrect.bottom.toInt()
            }
            view_mask!!.invalidate()
        }
        val screenAspectRatio = Tools.aspectRatio(this)
        val rotation =
            if (previewView!!.display == null) Surface.ROTATION_0 else previewView!!.display.rotation
        val preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder() //优化捕获速度，可能降低图片质量
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
//        val imganalysis=ImageAnalysis.Builder()
//            .setTargetResolution(Size(1280, 720))
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
//        imganalysis.setAnalyzer(ContextCompat.getMainExecutor(this)){
//            graphicOverlay.clear()
//            Handler(Looper.getMainLooper()).postDelayed({
//                cameraReticleAnimator.start()
//                graphicOverlay.add(BarcodeReticleGraphic(previewView!!,cameraReticleAnimator))
//                graphicOverlay.invalidate()
//            },1000)
//
//        }
        // 在重新绑定之前取消绑定用例
        cameraProvider!!.unbindAll()
        val cameraOrition =
            if (front) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraOrition).build()
        val camera = cameraProvider!!.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        mCameraControl = camera.cameraControl
        //        mCameraInfo = camera.getCameraInfo();
        val outLocation = Tools.getViewLocal(view_mask!!)
        autoFocus(
            outLocation[0] + view_mask!!.measuredWidth / 2,
            outLocation[1] + view_mask!!.measuredHeight / 2,
            true
        )
    }

    private fun takePhoto(photoFile: String?) {
        // 保证相机可用
        if (imageCapture == null) return
//        val file=File(filesDir,"CAmerx")
//        file.mkdir()
//         val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
//      val filepath= File.separator + "IMG_" + simpleDateFormat.format(
//            Date()
//        ) + ".jpg"
//        val finalFilepath=File(file,filepath)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(File(photoFile)).build()

        //  设置图像捕获监听器，在拍照后触发
        imageCapture!!.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    rl_start!!.visibility = View.GONE
                    rl_result_picture!!.visibility = View.VISIBLE
                    ll_picture_parent!!.visibility = View.VISIBLE
                    val bitmap = Tools.bitmapClip(this@CameraActivity, photoFile!!, front)
                    img_picture!!.setImageBitmap(bitmap)
//                    mCameraParam!!.pictureTempPath=finalFilepath.absolutePath
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("wld_____", "Photo capture failed: \${exc.message}", exception)
                }
            })
    }

    private fun savePicture() {
        var rect: Rect? = null
        if (mCameraParam!!.isShowMask) {
            val outLocation = Tools.getViewLocal(view_mask!!)
            rect = Rect(
                outLocation[0], outLocation[1],
                view_mask!!.measuredWidth, view_mask!!.measuredHeight
            )
        }
        Tools.saveBitmap(
            this,
            mCameraParam!!.pictureTempPath!!,
            mCameraParam!!.picturePath,
            rect,
            front
        )
        Tools.deletTempFile(mCameraParam!!.pictureTempPath)
        val intent = Intent()
        intent.putExtra(CameraConstant.PICTURE_PATH_KEY, mCameraParam!!.picturePath)
        setResult(RESULT_OK, intent)
        finish()
    }

    //https://developer.android.com/training/camerax/configuration
    private fun autoFocus(x: Int, y: Int, first: Boolean) {
//        MeteringPointFactory factory = previewView.getMeteringPointFactory();
        val factory: MeteringPointFactory =
            SurfaceOrientedMeteringPointFactory(x.toFloat(), y.toFloat())
        val point = factory.createPoint(x.toFloat(), y.toFloat())
        val action = FocusMeteringAction.Builder(
            point,
            FocusMeteringAction.FLAG_AF
        ) //                .disableAutoCancel()
            //                .addPoint(point2, FocusMeteringAction.FLAG_AE)
            // 3秒内自动调用取消对焦
            .setAutoCancelDuration(mCameraParam!!.focusViewTime.toLong(), TimeUnit.SECONDS)
            .build()
        //        mCameraControl.cancelFocusAndMetering();
        val future = mCameraControl!!.startFocusAndMetering(action)
        future.addListener({
            try {
                val result = future.get()
                if (result.isFocusSuccessful) {
                    focus_view!!.showFocusView(x, y)
                    if (!first && mCameraParam!!.isShowFocusTips) {
                        val mToast = Toast.makeText(
                            applicationContext,
                            mCameraParam!!.getFocusSuccessTips(this),
                            Toast.LENGTH_LONG
                        )
                        mToast.setGravity(Gravity.CENTER, 0, 0)
                        mToast.show()
                    }
                } else {
                    if (mCameraParam!!.isShowFocusTips) {
                        val mToast = Toast.makeText(
                            applicationContext,
                            mCameraParam!!.getFocusFailTips(this),
                            Toast.LENGTH_LONG
                        )
                        mToast.setGravity(Gravity.CENTER, 0, 0)
                        mToast.show()
                    }
                    focus_view!!.hideFocusView()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                focus_view!!.hideFocusView()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider!!.unbindAll()
        //        if (cameraExecutor != null)
//            cameraExecutor.shutdown();
    }
}