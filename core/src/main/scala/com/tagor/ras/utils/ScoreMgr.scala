package com.tagor.ras.utils

import com.tagor.ras.models.Block

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by rolangom on 12/26/15.
  */
object ScoreMgr {

  val initLevel = 0

  private var _plays: Int = 1
  private var _score: Int = 0
  private var _scoreUp: Int = 0
  private var _scoreDown: Int = 0
  private var _level: Int = initLevel
//  private var isFixedLvlInc = false
//  private var newScoreToReach: Int = _
//  private val PlusScore = 35
  private var _isNewBestScore = false

  def score = _score
  def level = _level
  def bestScore = PrefMgr.prefs.getInteger("score", 0)
  def fullScoreStr: String = s"${_score};${_scoreUp};${_scoreDown}"
  def plays = _plays

  def increase(b: Block): Unit = {
    _score += 1
    RxMgr.newScore.onNext(_score)
    Future(handleScore(b))
    post(() =>  SoundMgr.playScored())
  }

  private def handleScore(b: Block): Unit = {
    if (b.btype.isDimenUp)
      _scoreUp += 1
    else
      _scoreDown += 1

//    if (isFixedLvlInc) {
//      if (_score > newScoreToReach) {
//        newScoreToReach += PlusScore
//        increaseLevel()
//      }
//    } else {
//      _score match {
//        case 10 | 25 | 45 | 60 =>
//          increaseLevel()
//        case 90 =>
//          isFixedLvlInc = true
//          newScoreToReach = 90
//        case _ => ()
//      }
//    }
  }

  def isHard = _score > 75

  def increaseLevel() {
    _level += 1

    //testing
//    if (_level == initLevel + 1)
//      _level = initLevel

    RxMgr.newLevel.onNext(_level)
  }

  def saveAndReset(): Unit = {
    save()
    reset()
  }

  def reset(): Unit = {
    _score = 0
    _scoreUp = 0
    _scoreDown = 0
    _level = initLevel
//    isFixedLvlInc = false
    _isNewBestScore = false
    RxMgr.newScore.onNext(_score)
    RxMgr.newLevel.onNext(_level)
  }

  def isNewBestScore: Boolean = _isNewBestScore //_score > bestScore

  def save(): Unit = {
    _plays += 1
    RxMgr.incEvent.onNext(String.valueOf(_plays))
    RxMgr.submitLeaderBoard.onNext(fullScoreStr)
    RxMgr.submitAchivements.onNext(fullScoreStr)
    val currBestScore = bestScore
    if (_score > currBestScore) {
      _isNewBestScore = true
      val prefs = PrefMgr.prefs
      prefs.putInteger("score", _score)
      prefs.flush()
    }
  }

  def isInterstitialToShow: Boolean = _plays % 4 == 0
}
