package com.tagor.ras.utils

import com.badlogic.gdx.Gdx

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by rolangom on 12/26/15.
  */
object ScoreMgr {

  private val prefs = Gdx.app.getPreferences("RumperGamePrefs")

  private var _lastScore = 0
  private var _score: Int = 0
  private var _level: Int = 0
  private var isFixedLvlInc = false
  private var newScoreToReach: Int = _
  private val PlusScore = 35

  def score = _score
  def lastScore = _lastScore
  def bestScore = prefs.getInteger("score", 0)

  def increase(): Unit = {
    _score += 1
    RxMgr.newScore.onNext(_score)
    Future(handleScore())
  }

  private def handleScore(): Unit = {
    if (isFixedLvlInc) {
      if (score > newScoreToReach) {
        newScoreToReach += PlusScore
        increaseLevel()
      }
    } else {
      score match {
        case 10 | 25 | 45 | 60 =>
          increaseLevel()
        case 90 =>
          isFixedLvlInc = true
          newScoreToReach = 90
        case _ => ()
      }
    }
  }

  private def increaseLevel() {
    _level += 1
    RxMgr.newLevel.onNext(_level)
  }

  def saveAndReset(): Unit = {
    save()
    reset()
  }

  def reset(): Unit = {
    _lastScore = _score
    _score = 0
    _level = 0
    isFixedLvlInc = false
    RxMgr.newScore.onNext(_score)
    RxMgr.newLevel.onNext(_level)
  }

  def isNewBestScore: Boolean = _lastScore > bestScore

  def save(): Unit = {
    val currScore = prefs.getInteger("score", 0)
    if (_score > currScore) {
      prefs.putInteger("score", _score)
      prefs.flush()
    }
  }
}
