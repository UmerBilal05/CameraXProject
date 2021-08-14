package com.example.cameraxproject.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.example.cameraxproject.R
import com.example.cameraxproject.activity.CameraActivity
import java.io.File

class CameraParam : Parcelable {
    var isFront //相机方向,试试前置摄像头
            : Boolean
         set
    var picturePath //最终保存路径
            : String
         set
     var pictureTempPath //拍照的临时路径
            : String=""
    var isShowMask //是否显示裁剪框
            : Boolean
         set
    var isShowSwitch //是否显示切换相机正反面按钮
            : Boolean
         set
    var backText //返回键的文字颜色尺寸
            : String?
         set
    var backColor //
            : Int
         set
    var backSize //
            : Int
         set
    var switchSize //切换正反面按钮的大小，上左间距
            : Int
         set
    var switchTop //
            : Int
         set
    var switchLeft //
            : Int
         set
    var takePhotoSize //下面几个icon的尺寸
            : Int
         set
    var maskMarginLeftAndRight //剪切框左右边距
            : Int
         set
    var maskMarginTop //剪切框上下边距
            : Int
         set
    var maskRatioW //剪切框的宽高比,比如h,9:16，这里的值是16/9
            : Int
         set
    var maskRatioH //
            : Int
         set
    var focusViewSize //焦点框的大小
            : Int
         set
    var focusViewColor //焦点框的颜色
            : Int
         set
    var focusViewTime //焦点框显示的时长
            : Int
         set
    var focusViewStrokeSize //焦点框线条的尺寸
            : Int
         set
    var cornerViewSize //焦点框圆角尺寸
            : Int
         set

    //icon
    var maskImgId //
            : Int
         set
    var switchImgId //
            : Int
         set
    var cancelImgId //
            : Int
         set
    var saveImgId //
            : Int
         set
    var takePhotoImgId //
            : Int
         set
    var resultBottom //拍照到下面的距离
            : Int
         set
    var resultLeftAndRight //拍照到左右距离
            : Int
         set
    var backLeft //返回键到左边的距离
            : Int
         set
     var focusSuccessTips //聚焦成功提示
            : String=""
     var focusFailTips //聚焦失败提示
            : String?
     var mActivity //
            : Activity? = null
    var isShowFocusTips //是否显示聚焦成功的提示
            : Boolean
         set
    var requestCode: Int
         set

     constructor(mBuilder: Builder) {
        isFront = mBuilder.front
        picturePath = mBuilder.picturePath
        isShowMask = mBuilder.showMask
        isShowSwitch = mBuilder.showSwitch
        backText = mBuilder.backText
        backColor = mBuilder.backColor
        backSize = mBuilder.backSize
        switchSize = mBuilder.switchSize
        switchTop = mBuilder.switchTop
        switchLeft = mBuilder.switchLeft
        takePhotoSize = mBuilder.takePhotoSize
        maskMarginLeftAndRight = mBuilder.maskMarginLeftAndRight
        maskMarginTop = mBuilder.maskMarginTop
        maskRatioW = mBuilder.maskRatioW
        maskRatioH = mBuilder.maskRatioH
        focusViewSize = mBuilder.focusViewSize
        focusViewColor = mBuilder.focusViewColor
        focusViewTime = mBuilder.focusViewTime
        focusViewStrokeSize = mBuilder.focusViewStrokeSize
        cornerViewSize = mBuilder.cornerViewSize
        maskImgId = mBuilder.maskImgId
        switchImgId = mBuilder.switchImgId
        cancelImgId = mBuilder.cancelImgId
        saveImgId = mBuilder.saveImgId
        takePhotoImgId = mBuilder.takePhotoImgId
        resultBottom = mBuilder.resultBottom
        resultLeftAndRight = mBuilder.resultLeftAndRight
        backLeft = mBuilder.backLeft
        focusSuccessTips = mBuilder.focusSuccessTips
        focusFailTips = mBuilder.focusFailTips
        mActivity = mBuilder.mActivity
        isShowFocusTips = mBuilder.showFocusTips
        requestCode = mBuilder.requestCode
        if (mActivity == null) {
            throw NullPointerException("Activity param is null")
        }
    }

     fun startActivity(requestCode: Int): CameraParam {
        val intent = Intent(mActivity, CameraActivity::class.java)
        intent.putExtra(CameraConstant.CAMERA_PARAM_KEY, this)
        mActivity!!.startActivityForResult(intent, requestCode)
        return this
    }

    protected constructor(parcel: Parcel) {
        isFront = parcel.readByte().toInt() != 0
        picturePath = parcel.readString()!!
        pictureTempPath = parcel.readString()!!
        isShowMask = parcel.readByte().toInt() != 0
        isShowSwitch = parcel.readByte().toInt() != 0
        backText = parcel.readString()
        backColor = parcel.readInt()
        backSize = parcel.readInt()
        switchSize = parcel.readInt()
        switchTop = parcel.readInt()
        switchLeft = parcel.readInt()
        takePhotoSize = parcel.readInt()
        maskMarginLeftAndRight = parcel.readInt()
        maskMarginTop = parcel.readInt()
        maskRatioW = parcel.readInt()
        maskRatioH = parcel.readInt()
        focusViewSize = parcel.readInt()
        focusViewColor = parcel.readInt()
        focusViewTime = parcel.readInt()
        focusViewStrokeSize = parcel.readInt()
        cornerViewSize = parcel.readInt()
        maskImgId = parcel.readInt()
        switchImgId = parcel.readInt()
        cancelImgId = parcel.readInt()
        saveImgId = parcel.readInt()
        takePhotoImgId = parcel.readInt()
        resultBottom = parcel.readInt()
        resultLeftAndRight = parcel.readInt()
        backLeft = parcel.readInt()
        focusSuccessTips = parcel.readString()!!
        focusFailTips = parcel.readString()
        isShowFocusTips = parcel.readByte().toInt() != 0
        requestCode = parcel.readInt()
    }

    fun getPictureTempPatha():String {
        val file = File(picturePath)
        val pictureName: String = file.getName()
        var newName: String? = null
        if (pictureName.contains(".")) {
            val lastDotIndex = pictureName.lastIndexOf('.')
            newName = pictureName.substring(0, lastDotIndex) + "_temp" + pictureName.substring(
                lastDotIndex
            )
        }
        if (newName == null) {
            newName = pictureName
        }
        return file.getParent() + File.separator + newName
    }

    fun getFocusSuccessTips(context: Context): String {
        return if (focusSuccessTips == null) context.getString(R.string.focus_success) else focusSuccessTips
    }

    fun getFocusFailTips(context: Context): String {
        return if (focusFailTips == null) context.getString(R.string.focus_fail) else focusFailTips!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte((if (isFront) 1 else 0).toByte())
        dest.writeString(picturePath)
        dest.writeString(pictureTempPath)
        dest.writeByte((if (isShowMask) 1 else 0).toByte())
        dest.writeByte((if (isShowSwitch) 1 else 0).toByte())
        dest.writeString(backText)
        dest.writeInt(backColor)
        dest.writeInt(backSize)
        dest.writeInt(switchSize)
        dest.writeInt(switchTop)
        dest.writeInt(switchLeft)
        dest.writeInt(takePhotoSize)
        dest.writeInt(maskMarginLeftAndRight)
        dest.writeInt(maskMarginTop)
        dest.writeInt(maskRatioW)
        dest.writeInt(maskRatioH)
        dest.writeInt(focusViewSize)
        dest.writeInt(focusViewColor)
        dest.writeInt(focusViewTime)
        dest.writeInt(focusViewStrokeSize)
        dest.writeInt(cornerViewSize)
        dest.writeInt(maskImgId)
        dest.writeInt(switchImgId)
        dest.writeInt(cancelImgId)
        dest.writeInt(saveImgId)
        dest.writeInt(takePhotoImgId)
        dest.writeInt(resultBottom)
        dest.writeInt(resultLeftAndRight)
        dest.writeInt(backLeft)
        dest.writeString(focusSuccessTips)
        dest.writeString(focusFailTips)
        dest.writeByte((if (isShowFocusTips) 1 else 0).toByte())
        dest.writeInt(requestCode)
    }

    open class Builder {
        var front = false
         var picturePath = Tools.picturePath
         var showMask = true
         var showSwitch = false
         var backText: String? = null
         var backColor = -1
         var backSize = -1
         var switchSize = -1
         var switchTop = -1
         var switchLeft = -1
         var takePhotoSize = -1
         var maskMarginLeftAndRight = -1
         var maskMarginTop = -1
         var maskRatioW = -1
         var maskRatioH = -1
         var focusViewSize = -1 //焦点框的大小
         var focusViewColor = -1 //焦点框的颜色
         var focusViewTime = 3 //焦点框显示的时长
         var focusViewStrokeSize = -1 //焦点框线条的尺寸
         var cornerViewSize = -1
         var maskImgId = -1 //
         var switchImgId = -1 //
         var cancelImgId = -1 //
         var saveImgId = -1 //
         var takePhotoImgId = -1 //
         var resultBottom = -1 //拍照到下面的距离
         var resultLeftAndRight = -1 //拍照到左右距离
         var backLeft = -1 //返回键到左边的距离
         var focusSuccessTips //聚焦成功提示
                : String=""
         var focusFailTips //聚焦失败提示
                : String? = null
         var mActivity //
                : Activity? = null
         var showFocusTips = true
         var requestCode = CameraConstant.REQUEST_CODE
        fun setFront(front: Boolean): Builder {
            this.front = front
            return this
        }

        fun setPicturePath(picturePath: String): Builder {
            this.picturePath = picturePath
            return this
        }

        fun setShowMask(showMask: Boolean): Builder {
            this.showMask = showMask
            return this
        }

        fun setShowSwitch(showSwitch: Boolean): Builder {
            this.showSwitch = showSwitch
            return this
        }

        fun setBackText(backText: String?): Builder {
            this.backText = backText
            return this
        }

        fun setBackColor(backColor: Int): Builder {
            this.backColor = backColor
            return this
        }

        fun setBackSize(backSize: Int): Builder {
            this.backSize = backSize
            return this
        }

        fun setSwitchSize(switchSize: Int): Builder {
            this.switchSize = switchSize
            return this
        }

        fun setSwitchTop(switchTop: Int): Builder {
            this.switchTop = switchTop
            return this
        }

        fun setSwitchLeft(switchLeft: Int): Builder {
            this.switchLeft = switchLeft
            return this
        }

        fun setTakePhotoSize(takePhotoSize: Int): Builder {
            this.takePhotoSize = takePhotoSize
            return this
        }

        fun setMaskMarginLeftAndRight(maskMarginLeftAndRight: Int): Builder {
            this.maskMarginLeftAndRight = maskMarginLeftAndRight
            return this
        }

        fun setMaskMarginTop(maskMarginTop: Int): Builder {
            this.maskMarginTop = maskMarginTop
            return this
        }

        fun setMaskRatioW(maskRatioW: Int): Builder {
            this.maskRatioW = maskRatioW
            return this
        }

        fun setMaskRatioH(maskRatioH: Int): Builder {
            this.maskRatioH = maskRatioH
            return this
        }

        fun setFocusViewSize(focusViewSize: Int): Builder {
            this.focusViewSize = focusViewSize
            return this
        }

        fun setFocusViewColor(focusViewColor: Int): Builder {
            this.focusViewColor = focusViewColor
            return this
        }

        fun setFocusViewTime(focusViewTime: Int): Builder {
            this.focusViewTime = focusViewTime
            return this
        }

        fun setFocusViewStrokeSize(focusViewStrokeSize: Int): Builder {
            this.focusViewStrokeSize = focusViewStrokeSize
            return this
        }

        fun setCornerViewSize(cornerViewSize: Int): Builder {
            this.cornerViewSize = cornerViewSize
            return this
        }

        fun setMaskImgId(maskImgId: Int): Builder {
            this.maskImgId = maskImgId
            return this
        }

        fun setSwitchImgId(switchImgId: Int): Builder {
            this.switchImgId = switchImgId
            return this
        }

        fun setCancelImgId(cancelImgId: Int): Builder {
            this.cancelImgId = cancelImgId
            return this
        }

        fun setSaveImgId(saveImgId: Int): Builder {
            this.saveImgId = saveImgId
            return this
        }

        fun setTakePhotoImgId(takePhotoImgId: Int): Builder {
            this.takePhotoImgId = takePhotoImgId
            return this
        }

        fun setResultBottom(resultBottom: Int): Builder {
            this.resultBottom = resultBottom
            return this
        }

        fun setResultLeftAndRight(resultLeftAndRight: Int): Builder {
            this.resultLeftAndRight = resultLeftAndRight
            return this
        }

        fun setBackLeft(backLeft: Int): Builder {
            this.backLeft = backLeft
            return this
        }

        fun setFocusSuccessTips(focusSuccessTips: String?): Builder {
            this.focusSuccessTips = focusSuccessTips!!
            return this
        }

        fun setFocusFailTips(focusFailTips: String?): Builder {
            this.focusFailTips = focusFailTips
            return this
        }

        fun setActivity(mActivity: Activity?): Builder {
            this.mActivity = mActivity
            return this
        }

        fun setShowFocusTips(showFocusTips: Boolean): Builder {
            this.showFocusTips = showFocusTips
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun build(): CameraParam {
            val campath= CameraParam(this)
             campath.pictureTempPath=getPictureTempPatha()
                campath.startActivity(requestCode)
            return campath
        }
        fun getPictureTempPatha():String {
            val file = File(picturePath)
            val pictureName: String = file.getName()
            var newName: String? = null
            if (pictureName.contains(".")) {
                val lastDotIndex = pictureName.lastIndexOf('.')
                newName = pictureName.substring(0, lastDotIndex) + "_temp" + pictureName.substring(
                    lastDotIndex
                )
            }
            if (newName == null) {
                newName = pictureName
            }
            return file.getParent() + File.separator + newName
        }
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CameraParam?> = object : Parcelable.Creator<CameraParam?> {
            override fun createFromParcel(ina: Parcel): CameraParam {
                return CameraParam(ina)
            }

            override fun newArray(size: Int): Array<CameraParam?> {
                return arrayOfNulls(size)
            }
        }
    }
}