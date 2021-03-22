package com.sdirin.games.testingsnake.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.sort


/**
 * Created by SDirin on 28-Dec-17.
 */

const val TOP_SCORES = "top_scores"
const val DATE_KEY = "date_key"
const val SCORE_KEY = "score_key"
const val LAST_DATE_KEY = "last_date_key"
const val LAST_SCORE_KEY = "last_score_key"
class TopScores(val context: Context) {

    //date - score
    //1 - 5
    fun safeScore(score: Int) {
        //reading saved data
        val prefs = context.getSharedPreferences(TOP_SCORES, Context.MODE_PRIVATE)

        val top = getTop()

        val df = SimpleDateFormat.getDateInstance()
        val today = Calendar.getInstance().time
        val reportDate = df.format(today)

        top.add(Score(reportDate, score))
        //sorting data
        sort(top) { o1, o2 ->  o2.score.compareTo(o1.score)}
        //saving data
        for ((i, data_score) in top.withIndex()){
                prefs.edit()
                    .putString(DATE_KEY + i.toString(), data_score.date)
                    .putInt(SCORE_KEY + i.toString(), data_score.score)
                    .apply()
        }
        prefs.edit()
                .putString(LAST_DATE_KEY, reportDate)
                .putInt(LAST_SCORE_KEY, score)
                .apply()
    }

    fun getLastScore():Score{
        val prefs = context.getSharedPreferences(TOP_SCORES, Context.MODE_PRIVATE)
        return Score(prefs.getString(LAST_DATE_KEY,"none"),prefs.getInt(LAST_SCORE_KEY,0))
    }

    fun getTop():MutableList<Score>{
        val prefs = context.getSharedPreferences(TOP_SCORES, Context.MODE_PRIVATE)
        val top : MutableList<Score> = ArrayList()
        for (i in 0..4){
            if (prefs.contains(DATE_KEY + i.toString())){
                top.add(Score(prefs.getString(DATE_KEY + i.toString(),"none"),prefs.getInt(SCORE_KEY + i.toString(),0)))
            } else {
                break
            }
        }
        return top
    }

    fun clearTop(){
        val prefs = context.getSharedPreferences(TOP_SCORES, Context.MODE_PRIVATE)
        for (i in 0..4){
            if (prefs.contains(DATE_KEY + i.toString())){
                prefs.edit().remove(DATE_KEY + i.toString()).apply()
            } else {
                break
            }
        }
    }

    class Score(val date: String, val score: Int)
}