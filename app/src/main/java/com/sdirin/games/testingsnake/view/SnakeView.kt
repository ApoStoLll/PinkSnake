package com.sdirin.games.testingsnake.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.sdirin.games.testingsnake.model.CellType
import com.sdirin.games.testingsnake.model.ClickableArea
import com.sdirin.games.testingsnake.model.SnakeGame


/**
 * Created by SDirin on 26-Dec-17.
 */
class SnakeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    lateinit var game: SnakeGame
    private var width = 0f
    private var height = 0f
    val cellSize = 50

    lateinit var topClickable : ClickableArea
    lateinit var rightClickable : ClickableArea
    lateinit var bottomClickable : ClickableArea
    lateinit var leftClickable : ClickableArea

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if (!this::game.isInitialized){
            return
        }

        val paint = Paint()
        paint.alpha = 0xFF
        paint.color = Color.RED
        canvas?.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        val cols = game.field.size
        val rows = game.field[0].size
        val gap = 1f
        width = canvas!!.width.toFloat()
        height = canvas.height.toFloat()
        val cell_width = cellSize//min(width / cols, height / rows)
        val cell_height = cellSize//min(width / cols, height / rows)
        val offset = (width - cell_width * cols) / 2

        for ((col, line) in game.field.withIndex()) {
            for ((row, cell) in line.withIndex()) {
                when (cell) {
                    CellType.EMPTY -> {
                        paint.color = Color.WHITE
                    }
                    CellType.SNAKE_BODY -> {
                        paint.color = Color.BLACK
                    }
                    CellType.FOOD -> {
                        paint.color = Color.GREEN
                    }
                    CellType.DEAD_BODY -> {
                        paint.color = Color.RED
                    }
                    CellType.OBSTACLE -> {
                        paint.color = Color.BLUE
                    }
                }

                canvas.drawRect(
                        col * cell_width + gap + offset,
                        row * cell_height + gap,
                        (col + 1) * cell_width - gap + offset,
                        (row + 1) * cell_height - gap,
                        paint)

            }
        }
    }

}