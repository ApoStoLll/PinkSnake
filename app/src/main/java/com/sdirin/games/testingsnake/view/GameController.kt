package com.sdirin.games.testingsnake.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sdirin.games.testingsnake.model.ClickableArea
import com.sdirin.games.testingsnake.model.Direction

/**
 * Created by SDirin on 28-Dec-17.
 */
class GameController @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)  {

    lateinit var topClickable : ClickableArea
    lateinit var rightClickable : ClickableArea
    lateinit var bottomClickable : ClickableArea
    lateinit var leftClickable : ClickableArea

    lateinit var onDirectionChange: (dir: Direction)->Unit

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.alpha = 0x00
        canvas?.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        paint.color = Color.RED
        paint.alpha = 0x60
        if (topClickable.isVisible){
            canvas?.drawPath(topClickable.getPath(), paint)
        }
        if (rightClickable.isVisible){
            canvas?.drawPath(rightClickable.getPath(), paint)
        }
        if (bottomClickable.isVisible){
            canvas?.drawPath(bottomClickable.getPath(), paint)
        }
        if (leftClickable.isVisible){
            canvas?.drawPath(leftClickable.getPath(), paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != 0 && h != 0){
            topClickable = ClickableArea(h,w, Direction.TOP)
            rightClickable = ClickableArea(h,w, Direction.RIGHT)
            bottomClickable = ClickableArea(h,w, Direction.DOWN)
            leftClickable = ClickableArea(h,w, Direction.LEFT)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        if (!this::onDirectionChange.isInitialized){
            return false
        }
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                //top area
                if (topClickable.inRect(x,y)){
                    onDirectionChange(Direction.TOP)
                    topClickable.isVisible = true
                    invalidate()
                }
                //right area
                if (rightClickable.inRect(x,y)){
                    onDirectionChange(Direction.RIGHT)
                    rightClickable.isVisible = true
                    invalidate()
                }
                //bottom area
                if (bottomClickable.inRect(x,y)){
                    onDirectionChange(Direction.DOWN)
                    bottomClickable.isVisible = true
                    invalidate()
                }
                //left area
                if (leftClickable.inRect(x,y)){
                    onDirectionChange(Direction.LEFT)
                    leftClickable.isVisible = true
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                topClickable.isVisible = false
                rightClickable.isVisible = false
                bottomClickable.isVisible = false
                leftClickable.isVisible = false
                invalidate()
            }
        }
        return true
    }
}