package com.example.cameraxproject.util.overlay

import android.content.Context
import android.graphics.RectF
import androidx.camera.view.PreviewView

class PreferenceUtils {
    companion object{
        fun getBarcodeReticleBox(overlay: PreviewView): RectF {
            val context = overlay.context
            val overlayWidth = overlay.width.toFloat()
            val overlayHeight = overlay.height.toFloat()
//        val boxWidth = overlayWidth * getIntPref(context, R.string.pref_key_barcode_reticle_width, 80) / 100
//        val boxHeight = overlayHeight * getIntPref(context, R.string.pref_key_barcode_reticle_height, 35) / 100
            val boxWidth = overlayWidth * 0.75f
            val boxHeight = overlayHeight * 0.4f
            val cx = overlayWidth / 2
            val cy = overlayHeight / 2
            return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
        }
    }
}