package kr.tangram.smartgym.util

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class BackgroundRoundShape {

    companion object {

        fun fill(colorString:String):MaterialShapeDrawable {
            val shapeAppearanceModel = ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, R.attr.radius.toFloat()).build()
            val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

            val states = arrayOf(
                intArrayOf(R.attr.state_enabled),
                intArrayOf(-R.attr.state_enabled),
                intArrayOf(-R.attr.state_checked),
                intArrayOf(R.attr.state_pressed)
            )
            val colors = intArrayOf(
                Color.parseColor(colorString),
                Color.parseColor("#AAAAAA"),
                Color.GREEN,
                Color.BLUE
            )
            shapeDrawable.fillColor = ColorStateList(states, colors)//parent.context.getColorStateList(colur)


            return shapeDrawable
        }

        fun fill(colorInt:Int):MaterialShapeDrawable {
            val shapeAppearanceModel = ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, R.attr.radius.toFloat()).build()
            val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

            val states = arrayOf(
                intArrayOf(R.attr.state_enabled),
                intArrayOf(-R.attr.state_enabled),
                intArrayOf(-R.attr.state_checked),
                intArrayOf(R.attr.state_pressed)
            )
            val colors = intArrayOf(
                colorInt,
                Color.parseColor("#AAAAAA"),
                Color.GREEN,
                Color.BLUE
            )
            shapeDrawable.fillColor = ColorStateList(states, colors)//parent.context.getColorStateList(colur)


            return shapeDrawable
        }

        fun fillStroke(fillColorString:String,strokeColorString:String,strokeWidth:Float):MaterialShapeDrawable {
            val shapeAppearanceModel = ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, R.attr.radius.toFloat()).build()
            val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

            val states = arrayOf(
                intArrayOf(R.attr.state_enabled),
                intArrayOf(-R.attr.state_enabled),
                intArrayOf(-R.attr.state_checked),
                intArrayOf(R.attr.state_pressed)
            )
            val colors = intArrayOf(
                Color.parseColor(fillColorString),
                Color.parseColor("#AAAAAA"),
                Color.GREEN,
                Color.BLUE
            )
            shapeDrawable.fillColor = ColorStateList(states, colors)//parent.context.getColorStateList(colur)

            val states2 = arrayOf(
                intArrayOf(R.attr.state_enabled),
                intArrayOf(-R.attr.state_enabled),
                intArrayOf(-R.attr.state_checked),
                intArrayOf(R.attr.state_pressed)
            )
            val colors2 = intArrayOf(
                Color.parseColor(strokeColorString),
                Color.parseColor("#AAAAAA"),
                Color.GREEN,
                Color.BLUE
            )
            shapeDrawable.strokeWidth = strokeWidth
            shapeDrawable.strokeColor = ColorStateList(states2, colors2)

            return shapeDrawable
        }


    }


}