package com.sdirin.games.testingsnake.model

import android.graphics.Path
import android.graphics.Rect



/**
 * Created by SDirin on 27-Dec-17.
 */
class ClickableArea(val height:Int, val width:Int, val direction: Direction) {
    val rect = when(direction){
        Direction.TOP -> Rect((width * .2f).toInt(),0,(width * .8f).toInt(),(height * .2f).toInt())
        Direction.RIGHT -> Rect((width * .7f).toInt(),(height * .2f).toInt(),width,(height * .8f).toInt())
        Direction.DOWN -> Rect((width * .2f).toInt(),(height * .8f).toInt(),(width * .8f).toInt(),height)
        Direction.LEFT -> Rect(0,(height * .2f).toInt(),(width * .3f).toInt(),(height * .8f).toInt())
    }

    var isVisible = false;

    fun inRect(x:Int,y:Int) = rect.contains(x,y)

    fun getPath():Path{
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        when (direction) {
            Direction.TOP -> {
                path.moveTo(rect.left.toFloat(), rect.bottom.toFloat())
                path.lineTo(rect.left.toFloat()+(rect.right.toFloat()-rect.left.toFloat())/2, rect.top.toFloat())
                path.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
                path.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
            }
            Direction.RIGHT -> {
                path.moveTo(rect.left.toFloat(), rect.top.toFloat())
                path.lineTo(rect.right.toFloat(), rect.top.toFloat()+(rect.bottom.toFloat()-rect.top.toFloat())/2)
                path.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
                path.lineTo(rect.left.toFloat(), rect.top.toFloat())
            }
            Direction.DOWN -> {
                path.moveTo(rect.left.toFloat(), rect.top.toFloat())
                path.lineTo(rect.left.toFloat()+(rect.right.toFloat()-rect.left.toFloat())/2, rect.bottom.toFloat())
                path.lineTo(rect.right.toFloat(), rect.top.toFloat())
                path.lineTo(rect.left.toFloat(), rect.top.toFloat())
            }
            Direction.LEFT -> {
                path.moveTo(rect.right.toFloat(), rect.top.toFloat())
                path.lineTo(rect.left.toFloat(), rect.top.toFloat()+(rect.bottom.toFloat()-rect.top.toFloat())/2)
                path.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
                path.lineTo(rect.right.toFloat(), rect.top.toFloat())
            }
        }
        path.close()
        return path
    }
}