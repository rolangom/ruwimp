package com.tagor.ras.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Pools
import com.tagor.ras.models.ItemToInst
import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.BehaviorSubject
import scala.concurrent.duration.DurationInt

/**
  * Created by rolangom on 12/12/15.
  */
object RxMgr {
  lazy val onGameRunning = Subject[Boolean]()
  lazy val onActorAdded = Subject[Actor]()
  lazy val onPlayerAction = Subject[Int]()
  lazy val onItiAdded = Subject[ItemToInst]()

  var intervalObs: Observable[Long] = _

  onGameRunning.filter(b => b)
    .subscribe(_ => startIntervalObs())

  private def startIntervalObs(): Unit = {
    intervalObs = Observable.interval(125 milliseconds)
      .takeUntil(onGameRunning)
      .publish.refCount
  }

}
