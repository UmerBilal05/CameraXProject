package com.example.cameraxproject.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.cameraxproject.activity.MyApplication
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Tools private constructor() {
    companion object {
        private fun getDisplayMetrics(mContext: Context): DisplayMetrics {
            return mContext.resources.displayMetrics
        }

        fun getScreenwidth(mContext: Context): Int {
            return getDisplayMetrics(mContext).widthPixels
        }

        fun getScreenHeight(mContext: Context): Int {
            return getDisplayMetrics(mContext).heightPixels
        }

        fun hasBackCamera(cameraProvider: ProcessCameraProvider?): Boolean {
            try {
                return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
            } catch (e: CameraInfoUnavailableException) {
                e.printStackTrace()
            }
            return false
        }

        fun hasFrontCamera(cameraProvider: ProcessCameraProvider?): Boolean {
            try {
                return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
            } catch (e: CameraInfoUnavailableException) {
                e.printStackTrace()
            }
            return false
        }

        fun aspectRatio(mContext: Context): Int {
            val width = getScreenwidth(mContext)
            val height = getScreenHeight(mContext)
            val previewRatio = Math.max(width, height) * 1.0 / Math.min(width, height)
            return if (Math.abs(previewRatio - CameraConstant.RATIO_4_3_VALUE) <= Math.abs(
                    previewRatio - CameraConstant.RATIO_16_9_VALUE
                )
            ) {
                AspectRatio.RATIO_4_3
            } else AspectRatio.RATIO_16_9
        }

        //        return new File(mContext.getExternalFilesDir("images"), ".jpg");
        @JvmStatic
        val picturePath: String
            get() {

       // return new File(mContext.getExternalFilesDir("images"), ".jpg");
//                val cameraPath =
//                    Environment.getExternalStorageDirectory().path + File.separator + "DCIM" + File.separator + "CameraXX"
//
                val cameraPath =
                    MyApplication.temFilePath + File.separator + "DCIM" + File.separator + "CameraXX"

                val cameraFolder = File(cameraPath)
                if (!cameraFolder.exists()) {
                    cameraFolder.mkdirs()
                }
                val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
                return cameraFolder.absolutePath + File.separator + "IMG_" + simpleDateFormat.format(
                    Date()
                ) + ".jpg"
            }

        private fun pictureDegree(imgPath: String, front: Boolean): Matrix {
            val matrix = Matrix()
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imgPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (exif == null) return matrix
            var degree = 0
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> {
                }
            }
            matrix.postRotate(degree.toFloat())
            if (front) {
                matrix.postScale(-1f, 1f)
            }
            return matrix
        }

        fun bitmapClip(mContext: Context, imgPath: String, front: Boolean): Bitmap {
            var bitmap = BitmapFactory.decodeFile(imgPath)
            Log.d("wld__________bitmap", "width:" + bitmap.width + "--->height:" + bitmap.height)
            val matrix = pictureDegree(imgPath, front)
            val bitmapRatio = bitmap.height * 1.0 / bitmap.width //基本上都是16/9
            val width = getScreenwidth(mContext)
            val height = getScreenHeight(mContext)
            val screenRatio = height * 1.0 / width //屏幕的宽高比
            bitmap = if (bitmapRatio > screenRatio) { //胖的手机
                val clipHeight = (bitmap.width * screenRatio).toInt()
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.height - clipHeight shr 1,
                    bitmap.width,
                    clipHeight,
                    matrix,
                    true
                )
            } else { //瘦长的手机
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
            return bitmap
        }

        fun saveBitmap(
            mContext: Context,
            originPath: String,
            savePath: String,
            rect: Rect?,
            front: Boolean
        ): Boolean {
            val matrix = pictureDegree(originPath, front)
            var clipBitmap = BitmapFactory.decodeFile(originPath)
            clipBitmap = Bitmap.createBitmap(
                clipBitmap,
                0,
                0,
                clipBitmap.width,
                clipBitmap.height,
                matrix,
                true
            )
            if (rect != null) {
                val bitmapRatio = clipBitmap.height * 1.0 / clipBitmap.width //基本上都是16/9
                val width = getScreenwidth(mContext)
                val height = getScreenHeight(mContext)
                val screenRatio = height * 1.0 / width
                if (bitmapRatio > screenRatio) { //胖的手机
                    val clipHeight = (clipBitmap.width * screenRatio).toInt()
                    clipBitmap = Bitmap.createBitmap(
                        clipBitmap,
                        0,
                        clipBitmap.height - clipHeight shr 1,
                        clipBitmap.width,
                        clipHeight,
                        null,
                        true
                    )
                    scalRect(rect, clipBitmap.width * 1.0 / getScreenwidth(mContext))
                } else { //瘦长的手机
                    val marginTop = ((height - width * bitmapRatio) / 2).toInt()
                    rect.top = rect.top - marginTop
                    scalRect(rect, clipBitmap.width * 1.0 / getScreenwidth(mContext))
                }
                clipBitmap = Bitmap.createBitmap(
                    clipBitmap,
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom,
                    null,
                    true
                )
            }
            return saveBitmap(clipBitmap, savePath)
        }

        private fun saveBitmap(bitmap: Bitmap, savePath: String): Boolean {
            try {
                val file = File(savePath)
                val parent = file.parentFile
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                val fos = FileOutputStream(file)
                val b = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                return b
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }

        private fun scalRect(rect: Rect, scale: Double) {
            rect.left = (rect.left * scale).toInt()
            rect.top = (rect.top * scale).toInt()
            rect.right = (rect.right * scale).toInt()
            rect.bottom = (rect.bottom * scale).toInt()
        }

        @JvmStatic
        fun dp2px(mContext: Context, dipValue: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                getDisplayMetrics(mContext)
            ).toInt()
        }

        fun reflectPreviewRatio(view: View, @AspectRatio.Ratio ratio: Int) {
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            val cls: Class<*> = layoutParams.javaClass
            try {
                val dimensionRatioValue = cls.getDeclaredField("dimensionRatioValue")
                dimensionRatioValue.isAccessible = true
                if (ratio == AspectRatio.RATIO_4_3) {
                    dimensionRatioValue[layoutParams] = (4 * 1.0 / 3).toFloat()
                } else {
                    dimensionRatioValue[layoutParams] = (16 * 1.0 / 9).toFloat()
                }
                val dimensionRatioSide = cls.getDeclaredField("dimensionRatioSide")
                dimensionRatioSide.isAccessible = true
                dimensionRatioSide[layoutParams] = 1
                val dimensionRatio = cls.getDeclaredField("dimensionRatio")
                dimensionRatio.isAccessible = true
                if (ratio == AspectRatio.RATIO_4_3) {
                    dimensionRatio[layoutParams] = "h,3:4"
                } else {
                    dimensionRatio[layoutParams] = "h,9:16"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                layoutParams.width = getScreenwidth(view.context) - 2 * dp2px(
                    view.context,
                    layoutParams.leftMargin.toFloat()
                )
                if (ratio == AspectRatio.RATIO_4_3) {
                    layoutParams.height = (layoutParams.width * 4 / 3)
                } else {
                    layoutParams.height = (layoutParams.width * 16 / 9)
                }
            }
            view.layoutParams = layoutParams
        }

        fun reflectMaskRatio(view: View, w: Int, h: Int) {
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            val cls: Class<*> = layoutParams.javaClass
            try {
                val dimensionRatioValue = cls.getDeclaredField("dimensionRatioValue")
                dimensionRatioValue.isAccessible = true
                dimensionRatioValue[layoutParams] = (h * 1.0 / w).toFloat()
                val dimensionRatioSide = cls.getDeclaredField("dimensionRatioSide")
                dimensionRatioSide.isAccessible = true
                dimensionRatioSide[layoutParams] = 1
                val dimensionRatio = cls.getDeclaredField("dimensionRatio")
                dimensionRatio.isAccessible = true
                dimensionRatio[layoutParams] = "h,$w:$h"
            } catch (e: Exception) {
                e.printStackTrace()
                layoutParams.width = getScreenwidth(view.context) - 2 * dp2px(
                    view.context,
                    layoutParams.leftMargin.toFloat()
                )
                layoutParams.height = (layoutParams.width * h / w)
            }
            view.layoutParams = layoutParams
        }

        fun deletTempFile(tempPath: String?) {
            val file = File(tempPath)
            file.delete()
        }

        fun getViewLocal(view: View): IntArray {
            val outLocation = IntArray(2)
            view.getLocationInWindow(outLocation)
            return outLocation
        }

        fun checkPermission(context: Context): Boolean {
            val permissions = cameraPermission()
            for (i in permissions.indices) {
                if (!isGranted(context, permissions[i])) return false
            }
            return true
        }

        private fun cameraPermission(): Array<String> {
            return arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }

        private fun isGranted(context: Context, permission: String): Boolean {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true else ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    init {
        throw AssertionError()
    }
}