package com.tagor.ras.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.tagor.ras.models.ItemToInst
import rx.lang.scala.{Observable, Subject}
import scala.concurrent.duration.DurationInt

/**
  * Created by rolangom on 12/12/15.
  */
object RxMgr {
  lazy val onGameRunning = Subject[Boolean]()
  lazy val onActorAdded = Subject[Actor]()
  lazy val onPlayerAction = Subject[Int]()
  lazy val onItiAdded = Subject[ItemToInst]()
  lazy val newScore = Subject[Int]()
  lazy val newLevel = Subject[Int]()

  var intervalObs: Observable[Long] = _

  onGameRunning.filter(b => b)
    .subscribe(_ => startInterval())

  private def startInterval(): Unit = {
    intervalObs = Observable.interval(125 milliseconds)
      .takeUntil(onGameRunning)
      .publish.refCount
  }

}
