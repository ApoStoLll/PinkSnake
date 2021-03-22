package com.sdirin.games.testingsnake.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.sdirin.games.testingsnake.R
import com.sdirin.games.testingsnake.utils.TopScores
import kotlinx.android.synthetic.main.game_over.*
import java.io.File
import java.io.FileOutputStream


class GameOverActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        tv_current_date.visibility = View.GONE
        tv_current_score.visibility = View.GONE

        btn_new_game.setOnClickListener {
            finish()
        }

        btn_clear.setOnClickListener{
            TopScores(this).clearTop()
            showTop()
        }

        ib_share.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_EXTERNAL_STORAGE)
            } else {
                createImageAndShare();
            }
        }

        showTop()
    }

    private fun createImageAndShare(){
        val bitmap = getScreenShot(main_layout)
        store(bitmap, "topScore.png")
        shareImage(File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots", "topScore.png"))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createImageAndShare();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun showTop(){
        val topScores = TopScores(this)
        val top = topScores.getTop()
        val lastScore = topScores.getLastScore()
        var needLastScore = true
        for(id in (0..4)){
            if (top.size > id){
                val dateId = resources.getIdentifier("tv_top${id + 1}_date", "id", this.packageName)
                findViewById<TextView>(dateId).text = top[id].date
                val scoreId = resources.getIdentifier("tv_top${id + 1}_score", "id", this.packageName)
                findViewById<TextView>(scoreId).text = top[id].score.toString()
                if (top[id].date == lastScore.date && top[id].score == lastScore.score){
                    needLastScore = false
                    findViewById<TextView>(dateId).setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                    findViewById<TextView>(scoreId).setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                } else {
                    findViewById<TextView>(dateId).setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    findViewById<TextView>(scoreId).setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                }
            } else {
                val dateId = resources.getIdentifier("tv_top${id + 1}_date", "id", this.packageName)
                findViewById<TextView>(dateId).text = "Unknown"
                val scoreId = resources.getIdentifier("tv_top${id + 1}_score", "id", this.packageName)
                findViewById<TextView>(scoreId).text = "-"

                main_layout.recomputeViewAttributes(findViewById<TextView>(dateId))
                main_layout.recomputeViewAttributes(findViewById<TextView>(scoreId))
            }
        }
        if (needLastScore && lastScore.date != "none") {
            tv_current_date.visibility = View.VISIBLE
            tv_current_date.text = lastScore.date
            tv_current_score.visibility = View.VISIBLE
            tv_current_score.text = lastScore.score.toString()
        }
    }

    fun getScreenShot(view: View): Bitmap {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }
    fun store(bm: Bitmap, fileName: String) {
        val dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots"
        val dir = File(dirPath)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            val smallBm = getResizedBitmap(bm, bm.width/2,bm.height/2)
            smallBm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun shareImage(file: File) {
        val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".com.sdirin.games.testingsnake.provider", file)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "")
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_screenshot)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_app_available), Toast.LENGTH_SHORT).show()
        }

    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }
}
