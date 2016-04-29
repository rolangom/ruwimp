package com.tagor.ras.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.tagor.ras.models.ItemToInst
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.{Observable, Subject}
import scala.concurrent.duration.DurationInt

/**
  * Created by rolangom on 12/12/15.
  */
object RxMgr {
  lazy val onGameState = Subject[Int]()
  lazy val onActorAdded = Subject[Actor]()
  lazy val onPlayerAction = Subject[Int]()
  lazy val onItiAdded = Subject[ItemToInst]()
  lazy val newScore = Subject[Int]()
  lazy val newLevel = Subject[Int]()
  lazy val newTheme = Subject[Int]()

  lazy val onShareText = Subject[String]()
  lazy val onLeaderBoardClicked = Subject[String]()
  lazy val bannerViewVisible = Subject[String]()
  lazy val interstitialViewVisible = Subject[String]()

  private var _intervalObs: Observable[Long] = _
  private var _isGmRunning = false

  def intervalObs = _intervalObs
  def isGmRunning = _isGmRunning

  onGameState
    .subscribeOn(ComputationScheduler())
    .doOnNext({
      case Const.GameStatePlay =>
        _isGmRunning = true
      case Const.GameStateOver | Const.GameStateHome =>
        _isGmRunning = false
      case _ => ()
    })
    .filter(s => s == Const.GameStatePlay || s == Const.GameStateResume)
    .subscribe(_ => startInterval())

  def setBannerVisible(visible: Boolean): Unit = {
    bannerViewVisible.onNext(String.valueOf(visible))
  }

  def setInterstitialVisible(visible: Boolean): Unit = {
    if (ScoreMgr.isInterstitialToShow)
      interstitialViewVisible.onNext(String.valueOf(visible))
  }

  def showShareText(msg: String): Unit = onShareText.onNext(msg)

  private def startInterval(): Unit = {
    _intervalObs = Observable.interval(125 milliseconds)
      .takeUntil(onGameState.filter(s => s == Const.GameStatePause
        || s == Const.GameStateOver || s == Const.GameStateHome))
      .publish.refCount
  }

}
