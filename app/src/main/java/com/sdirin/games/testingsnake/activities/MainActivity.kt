package com.sdirin.games.testingsnake.activities

/*
* A game of SnakeGame to test TDD approach
*/

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sdirin.games.testingsnake.R
import com.sdirin.games.testingsnake.model.*
import com.sdirin.games.testingsnake.utils.TopScores
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer




const val TAG = "SnakeApp"
const val SNAKE_GAME = "snake_game"
class MainActivity : AppCompatActivity() {

    private lateinit var game: SnakeGame
    private lateinit var game_timer: Timer
    var width = 0
    var height = 0
    var gameSpeed = 0
    val maxSpeed = 300
    var skipFirst = false
    val topScores = TopScores(this)
    var showAnim = true
    lateinit var animArrow: ClickableArea

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        //todo safe slots
        //todo ui testing
        //todo sounds
        //todo in cell animation
    }

    fun newGame() {
        game = SnakeGame((height / game_view.cellSize).toInt(),(width / game_view.cellSize).toInt())
        game.createSnake(3,3)
        game.generateNew(CellType.FOOD)
        game.snakeDirection = Direction.RIGHT
        gameSpeed = 0
        game_view.game = game
        main_text_container.visibility = View.GONE
        tv_added_score.visibility = View.GONE
        tv_score.text = "0"
        game_view.invalidate()
        showAnimation()

        game.onEndGame = {
            stopGameLoop()
            game_view.invalidate()
            topScores.safeScore(game.score)
//            tv_main.visibility = View.VISIBLE
//            tv_main.text = "Game Over"
            val intent = Intent(this,GameOverActivity::class.java)
            this@MainActivity.startActivity(intent)
        }
        game.onEatFood = { score->
            stopGameLoop()
            gameSpeed += 5
            tv_score.text = game.score.toString()
            if (gameSpeed > maxSpeed) gameSpeed = maxSpeed
            skipFirst = true
            showAddedScore(score)
            startGameLoop()
        }
    }

    private fun showAddedScore(score: Int) {
        tv_added_score.text = score.toString()
        tv_added_score.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            tv_added_score.visibility = View.GONE
        }, 500)
    }

    private fun showAnimation() {
        stopGameLoop()
        animArrow = controller_view.topClickable

        animArrow.isVisible = true
        animationLoop()
    }
    fun animationLoop(){
        Handler().postDelayed(Runnable {
            runOnUiThread { showArrow() }
        }, 300)
    }
    fun showArrow(){
        when (animArrow) {
            controller_view.topClickable -> {
                animArrow.isVisible = false
                animArrow = controller_view.rightClickable
                animArrow.isVisible = true
                controller_view.invalidate()
                animationLoop()
            }
            controller_view.rightClickable -> {
                animArrow.isVisible = false
                animArrow = controller_view.bottomClickable
                animArrow.isVisible = true
                controller_view.invalidate()
                animationLoop()
            }
            controller_view.bottomClickable -> {
                animArrow.isVisible = false
                animArrow = controller_view.leftClickable
                animArrow.isVisible = true
                controller_view.invalidate()
                animationLoop()
            }
            controller_view.leftClickable -> {
                animArrow.isVisible = false
                controller_view.invalidate()
                startGameLoop()
            }
        }
    }

    private fun startGameLoop() {
        stopGameLoop()
        game_timer = timer("GameLoop",
                false,
                Date(),
                (500 - gameSpeed).toLong()
        ) {
            if (skipFirst) {
                skipFirst = false
            } else {
                runOnUiThread { update() }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        safeState()
        stopGameLoop()
    }

    private fun stopGameLoop() {
        if (this::game_timer.isInitialized) {
            game_timer.cancel()
            game_timer.purge()
        }
    }

    private fun onGameResume(){
        game_view.post(Runnable {

                width = game_view.measuredWidth
                height = game_view.measuredHeight

                newGame()

                val prefs = getSharedPreferences("game", Context.MODE_PRIVATE)
                try {

                    if (prefs.contains(SNAKE_GAME)){
                        gameSpeed = prefs.getString(SNAKE_GAME,"{}")?.let { game.resume(it) }!!
                        tv_score.text = (game.score).toString()
                    }
                } catch (e:Exception){
                    newGame()
                }

                controller_view.onDirectionChange = {
                    game.snakeDirection = it
                }

                game_view.setOnClickListener {
                    when (game.state) {
                        GameState.GAME_OVER -> newGame()
                        GameState.PAUSED -> {
                            game.state = GameState.RUNNING
                            main_text_container.visibility = View.GONE
                        }
                    }
                }
                main_text_container.setOnClickListener {
                    when (game.state) {
                        GameState.GAME_OVER -> newGame()
                        GameState.PAUSED -> {
                            game.state = GameState.RUNNING
                            main_text_container.visibility = View.GONE
                        }
                    }
                }
        })
    }

    override fun onResume() {
        super.onResume()
        onGameResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_menu -> {
                val intent = Intent(this,AboutActivity::class.java)
                this@MainActivity.startActivity(intent)
                true
            }
            R.id.license_menu -> {
                val intent = Intent(this,LicenseActivity::class.java)
                this@MainActivity.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun safeState() {
        if (this::game.isInitialized){
            val prefs = getSharedPreferences("game", Context.MODE_PRIVATE)
            if (game.state != GameState.GAME_OVER) {
                prefs.edit().putString(SNAKE_GAME, game.getData(gameSpeed)).apply()
            } else {
                prefs.edit().remove(SNAKE_GAME).apply()
            }
        }
    }

    override fun onBackPressed() {
        if (game.state == GameState.GAME_OVER || game.state == GameState.PAUSED){
            super.onBackPressed()
        } else {
            game.state = GameState.PAUSED
            main_text_container.visibility = View.VISIBLE
            tv_main.text = getString(R.string.paused)
            safeState()
        }
    }

    fun update() {
        if (game.state == GameState.RUNNING){
            game.tick()
            game_view.invalidate()
        }
    }

}
