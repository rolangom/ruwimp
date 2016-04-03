package com.tagor.ras.utils

import com.badlogic.gdx.Gdx

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by rolangom on 12/26/15.
  */
object ScoreMgr {

  private val prefs = Gdx.app.getPreferences("RumperGamePrefs")

  private var _score: Int = 0
  private var _level: Int = 0
  private var isFixedLvlInc = false
  private var newScoreToReach: Int = _
  private val PlusScore = 35
  private var _isNewBestScore = false

  def score = _score
  def bestScore = prefs.getInteger("score", 0)

  def increase(): Unit = {
    _score += 1
    RxMgr.newScore.onNext(_score)
    Future(handleScore())
    SoundMgr.playScored()
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
    _score = 0
    _level = 0
    isFixedLvlInc = false
    _isNewBestScore = false
    RxMgr.newScore.onNext(_score)
    RxMgr.newLevel.onNext(_level)
  }

  def isNewBestScore: Boolean = _isNewBestScore //_score > bestScore

  def save(): Unit = {
    val currBestScore = bestScore
    if (_score > currBestScore) {
      _isNewBestScore = true
      prefs.putInteger("score", _score)
      prefs.flush()
    }
  }
}
