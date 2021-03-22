package com.sdirin.games.testingsnake

import com.sdirin.games.testingsnake.model.CellType
import com.sdirin.games.testingsnake.model.Direction
import com.sdirin.games.testingsnake.model.GameState
import com.sdirin.games.testingsnake.model.SnakeGame
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


/**
* Created by SDirin on 22-Dec-17.
*/


class SnakeTest {

    private lateinit var game: SnakeGame

    @Before
    fun setUp() {
        game = SnakeGame(5,7)
    }

    @Test
    fun createSize() {
        assertFailsWith(SnakeGame.FieldSizeException::class) {
            SnakeGame(-1,2)
        }
        assertFailsWith(SnakeGame.FieldSizeException::class) {
            SnakeGame(2,-1)
        }
        assertFailsWith(SnakeGame.FieldSizeException::class) {
            SnakeGame(0,0)
        }
    }

    @Test
    fun checkFieldSize() {
        Assert.assertEquals(5, game.height)
        Assert.assertEquals(7, game.width)
    }

    @Test
    fun findAtOutOfArray() {
        Assert.assertEquals(CellType.OUT_OF_FIELD, game.findAt(-1,3))
        Assert.assertEquals(CellType.OUT_OF_FIELD, game.findAt(3,-1))
        Assert.assertEquals(CellType.OUT_OF_FIELD, game.findAt(8,3))
        Assert.assertEquals(CellType.OUT_OF_FIELD, game.findAt(3,8))
    }

    @Test
    fun createSnake() {
        Assert.assertEquals(CellType.EMPTY, game.findAt(4,3))
        game.createSnake(4,3)
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(4,3))
    }

    @Test
    fun createSnakeOutOfField() {
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createSnake(-1,3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createSnake(3,-3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createSnake(15,3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createSnake(3,15)
        }
    }

    @Test
    fun moveSnake() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.tick()
        Assert.assertEquals( CellType.SNAKE_BODY, game.findAt(4,3))
        Assert.assertEquals(CellType.EMPTY, game.findAt(3,3))
    }

    @Test
    fun moveSnakeBack() {
        //disabled back move
//        game.createSnake(0,3)
//        game.snakeDirection = Direction.TOP
//        game.snakeDirection = Direction.LEFT
//        game.tick()
//        Assert.assertEquals( CellType.SNAKE_BODY, game.findAt(6,3))
//        Assert.assertEquals(CellType.EMPTY, game.findAt(0,3))
    }

    @Test
    fun moveOutBorders() {
        game.createSnake(6,3)
        game.snakeDirection = Direction.RIGHT
        game.tick()
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(0,3))
        Assert.assertEquals(CellType.EMPTY, game.findAt(6,3))
    }

    @Test
    fun verticalMove() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.DOWN
        game.tick()
        Assert.assertEquals( CellType.SNAKE_BODY, game.findAt(3,4))
        Assert.assertEquals(CellType.EMPTY, game.findAt(3,3))
        game.tick()
        Assert.assertEquals( CellType.SNAKE_BODY, game.findAt(3,0))
        Assert.assertEquals(CellType.EMPTY, game.findAt(3,4))
    }

    @Test
    fun createFood() {
        game.createFood(4,4)
        Assert.assertEquals(CellType.FOOD, game.findAt(4,4))
    }

    @Test
    fun addFoodOutOfField() {
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createFood(-1,3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createFood(3,-3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createFood(15,3)
        }
        assertFailsWith(SnakeGame.OutOfFieldException::class) {
            game.createFood(3,15)
        }
    }

    @Test
    fun eatFood() {
        var wasCalled = false
        game.onEatFood = {
            wasCalled = true
        }
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.tick()
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(3,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(4,3))
        Assert.assertTrue(wasCalled)
    }

    @Test
    fun moveBigSnake() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.tick()
        game.tick()
        Assert.assertEquals(CellType.EMPTY, game.findAt(3,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(4,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(5,3))
    }

    @Test
    fun moveOutBig() {
        game.createSnake(5,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(6,3)
        game.tick()
        game.tick()
        Assert.assertTrue(CellType.EMPTY == game.findAt(5,3) || CellType.FOOD == game.findAt(5,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(6,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(0,3))
    }

    @Test
    fun moveBigChangeDir() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.tick()
        game.snakeDirection = Direction.DOWN
        game.tick()
        Assert.assertEquals(CellType.EMPTY, game.findAt(3,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(4,3))
        Assert.assertEquals(CellType.SNAKE_BODY, game.findAt(4,4))
    }

    @Test
    fun newFoodGeneration() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.tick()
        Assert.assertEquals(1, game.getCellTypeCount(CellType.FOOD))
    }

    @Test
    fun eatSelf() {
        var wasCalled = false
        game.onEndGame = {
            wasCalled = true
        }
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.createFood(5,3)
        game.createFood(5,4)
        game.tick()
        game.tick()
        game.snakeDirection = Direction.DOWN
        game.tick()
        game.snakeDirection = Direction.LEFT
        game.tick()
        game.snakeDirection = Direction.TOP
        game.tick()
        Assert.assertEquals(GameState.GAME_OVER, game.state)
        assertFailsWith(SnakeGame.GameOverException::class) {
            game.tick()
        }
        assertTrue { wasCalled }
        Assert.assertEquals(CellType.DEAD_BODY, game.findAt(4,3))
    }

    @Test
    fun obstacle() {
        var wasCalled = false
        game.onEndGame = {
            wasCalled = true
        }
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createObstacle(4,3)
        game.tick()
        Assert.assertEquals(CellType.DEAD_BODY, game.findAt(4,3))
        Assert.assertTrue(wasCalled)
    }

    @Test
    fun newObstacleGeneration() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
//        game.foodToObstacle = 3
        game.createFood(4,3)
        game.createFood(5,3)
        game.createFood(6,3)
        game.tick()
        game.tick()
        game.tick()
        Assert.assertEquals(1, game.getCellTypeCount(CellType.OBSTACLE))
    }

    @Test
    fun score() {
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.createFood(4,3)
        game.tick()
        Assert.assertEquals(1, game.foods)
    }

    @Test
    fun checkDecreasingScore(){
        game.createSnake(4,4)
        game.snakeDirection = Direction.RIGHT
        game.createFood(5,0)
        game.tick()
        game.snakeDirection = Direction.DOWN
        game.tick()
        Assert.assertEquals(10, game.score)
    }

    @Test
    fun preventGoingBack(){
        game.createSnake(3,3)
        game.snakeDirection = Direction.RIGHT
        game.snakeDirection = Direction.LEFT
        Assert.assertEquals(Direction.RIGHT,game.snakeDirection)
    }
}















