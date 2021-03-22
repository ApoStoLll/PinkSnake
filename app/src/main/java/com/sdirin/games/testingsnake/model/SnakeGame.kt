package com.sdirin.games.testingsnake.model

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.util.*

/**
 * Created by SDirin on 22-Dec-17.
 */

const val TAG = "SnakeApp"
const val SNAKE_KEY = "snake_key"
const val FIELD_KEY = "field_key"
const val DIR_KEY = "direction_key"
const val SPEED_KEY = "speed_key"
const val SCORE_KEY = "score_key"

private const val GAME_KEY = "snake_game"
enum class Direction(val code: String) {
    TOP("0"),
    RIGHT("1"),
    DOWN("2"),
    LEFT("3");

    companion object {
        private val map = Direction.values().associateBy(Direction::code)
        fun fromString(type: String) = map[type]
    }
}
enum class CellType(val code: String) {
    EMPTY("0"),
    SNAKE_BODY("1"),
    OUT_OF_FIELD("2"),
    FOOD("3"),
    DEAD_BODY("4"),
    OBSTACLE("5");

    companion object {
        private val map = CellType.values().associateBy(CellType::code)
        fun fromString(type: String) = map[type]
    }
}
enum class GameState {
    RUNNING,
    GAME_OVER,
    PAUSED
}
fun ClosedRange<Int>.random() =
        Random().nextInt(endInclusive - start) +  start

class SnakeGame(val height: Int, val width: Int) {

    var snakeDirection = Direction.RIGHT
        set(value) {
            if (value == snakeBackDirection) return
            field = value
        }

    var snakeBackDirection = Direction.LEFT

    private var snake: MutableList<Point> = mutableListOf()

    var state: GameState = GameState.RUNNING
    var foods = 0
    private var scorePerFood = 13
    var score = 0

    private val foodToObstacle = 3

    private var snakePos = Point(0,0)
    private var snakeEnd = Point(0, 0)
    class FieldSizeException(override var message:String): Exception()
    class OutOfFieldException(override var message:String): Exception()
    class GameOverException(override var message:String): Exception()
    init {
        if (height <= 0 || width <= 0) {
            throw FieldSizeException("Field size have to be more that 0")
        }
    }
    var field:Array<Array<CellType>> = Array(width) { Array<CellType>(height,{ CellType.EMPTY }) }

    lateinit var onEndGame: ()->Unit
    lateinit var onEatFood: (score: Int)->Unit

    private fun checkOutOfField(x: Int, y: Int){
        if (x < 0 || y < 0 || x >= field.size || y >= field[0].size){
            throw OutOfFieldException("Snake can not be created out of field")
        }
    }

    fun createSnake(x: Int, y: Int) {
        checkOutOfField(x,y)
        field[x][y] = CellType.SNAKE_BODY
        snake.add(Point(x,y))
    }
    fun findAt(x: Int, y: Int): CellType {
        if (x < 0 || y < 0 || x >= field.size || y >= field[0].size){
            return CellType.OUT_OF_FIELD
        }
        return field[x][y]
    }

    fun tick() {
        if (state == GameState.GAME_OVER){
            throw GameOverException("Can not tick during game over")
        }
        var x = snake[0].x
        var y = snake[0].y
        snake.add(0,Point(x,y))
        snakeBackDirection = when (snakeDirection) {
            Direction.TOP -> Direction.DOWN
            Direction.RIGHT -> Direction.LEFT
            Direction.DOWN -> Direction.TOP
            Direction.LEFT -> Direction.RIGHT
        }
        when (snakeDirection) {
            Direction.TOP -> y--
            Direction.RIGHT -> x++
            Direction.DOWN -> y++
            Direction.LEFT -> x--
        }
        val oldX = x
        val oldY = y
        x %= field.size
        y %= field[0].size
        if (x<0) x = field.size - 1
        if (y<0) y = field[0].size - 1
        snake[0].x = x
        snake[0].y = y

        when (field[x][y]) {
            CellType.EMPTY -> {
                field[snake.last().x][snake.last().y] = CellType.EMPTY
                snake.removeAt(snake.size-1)
                field[x][y] = CellType.SNAKE_BODY
                scorePerFood--
                if (scorePerFood<0) scorePerFood=0
            }
            CellType.FOOD -> {
                foods ++
                score += scorePerFood
                if (this::onEatFood.isInitialized) {
                    onEatFood(scorePerFood)
                }
                if (foods % foodToObstacle == 0){
                    generateNew(CellType.OBSTACLE)
                }
                generateNew(CellType.FOOD)
                field[x][y] = CellType.SNAKE_BODY
            }
            CellType.SNAKE_BODY -> {
                //die
                field[x][y] = CellType.DEAD_BODY
                state = GameState.GAME_OVER
                if (this::onEndGame.isInitialized) {
                    onEndGame()
                }
            }
            CellType.OBSTACLE -> {
                //die
                field[x][y] = CellType.DEAD_BODY
                state = GameState.GAME_OVER
                if (this::onEndGame.isInitialized) {
                    onEndGame()
                }
            }
        }
    }
    fun generateNew(cellType: CellType) {
        var loop = true
        lateinit var p:Point
        while(loop){
            p = Point((0..field.size).random(),(0..field[0].size).random())
            if (field[p.x][p.y] == CellType.EMPTY){
                loop = false
                field[p.x][p.y] = cellType

            }
        }

        if (snake.size>0) {
            scorePerFood = dist(p, snake[0]) + field.size - 1
        } else {
            scorePerFood = 13
        }
    }

    fun dist(p1: Point, p2: Point): Int {
        //todo check for not direct dist
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y)
    }

    fun createFood(x:Int, y:Int) {
        checkOutOfField(x,y)
        field[x][y] = CellType.FOOD
        if (snake.size>0) {
            scorePerFood = dist(Point(x, y), snake[0]) + field.size - 1
        } else {
            scorePerFood = 13
        }
    }

    fun createObstacle(x:Int, y:Int) {
        checkOutOfField(x,y)
        field[x][y] = CellType.OBSTACLE
    }

    fun getCellTypeCount(cellType: CellType):Int {
        var foodCnt = 0
        for (line in field) {
            line
                .filter { it == cellType }
                .forEach { foodCnt++ }
        }
        return foodCnt
    }

    fun getData(speed: Int):String{
        //snake
        //field
        //direction
        //speed
        //score
        val jsnake = JsonArray<JsonObject>()
        snake.mapTo(jsnake) { JsonObject(hashMapOf("x" to it.x, "y" to it.y)) }
        val jfield = JsonArray<JsonArray<String>>()
//        field.mapTo(jfield) { JsonArray(JsonArray(it.))}
        for(row in field){
            val jrow = JsonArray<String>()
            row.mapTo(jrow) { it.code }
            jfield.add(jrow)
        }
        val jgame = JsonObject()
        jgame[SNAKE_KEY] = jsnake
        jgame[FIELD_KEY] = jfield
        jgame[DIR_KEY] = snakeDirection.code
        jgame[SPEED_KEY] = speed
        jgame[SCORE_KEY] = score
        return jgame.toJsonString()
    }

    fun resume(data:String):Int{
        val parser: Parser = Parser()
        val jgame = parser.parse(StringBuilder(data)) as JsonObject
        val jsnake = jgame[SNAKE_KEY] as JsonArray<JsonObject>
        snake.clear()
        for(part in jsnake){
            snake.add(Point(part["x"] as Int, part["y"] as Int))
        }
        val jfields = jgame[FIELD_KEY] as JsonArray<JsonArray<String>>
        field = Array(width) { Array(height,{ CellType.EMPTY }) }
        for ((x, row) in jfields.withIndex()){
            for ((y, cell) in row.withIndex()){
                if (field.size > x && field[0].size > y){
                    field[x][y]= CellType.fromString(cell)!!
                } else {

                }
            }
        }
        snakeDirection = Direction.fromString(jgame[DIR_KEY] as String)!!
        score = jgame[SCORE_KEY] as Int
        return jgame[SPEED_KEY] as Int
    }
}