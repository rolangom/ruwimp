package com.tagor.ras.models

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.{Vector2, MathUtils}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.tagor.ras.utils
import com.tagor.ras.utils.{RxMgr, BlockPooler, BlockConst}
import rx.lang.scala.schedulers.{TrampolineScheduler, ImmediateScheduler}
import rx.lang.scala.{Subscription, Observable}
import rx.scala.concurrency.GdxScheduler

import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/**
  * Created by rolangom on 12/14/15.
  */
class Spawner(camera: OrthographicCamera) {

  lazy val pooler = new BlockPooler
  lazy val patGen = new PatternGenerator(pooler)
  lazy val vblocks = ArrayBuffer[Block]()
  lazy val pblocks = ArrayBuffer[Block]()
  lazy val VisibleSpan = BlockConst.Sizes(BlockConst.SizeM) * BlockConst.Size

  RxMgr.onItiAdded
    .observeOn(GdxScheduler())
    .doOnNext(_ => println("patGen.initDefaultsIti on Next"))
    .subscribe(i => initBlock(i))

  def start(): Unit = {
    val camPos = camera.position
    utils.post { () =>
      initDefaults(vblocks, camPos.x, camPos.y, 1)
        .lastOption
        .foreach(b => spawn(b.getRight + b.getWidth / 2, b.getY))
    }
    RxMgr.intervalObs
      .doOnEach(_ => checkShortInterval())
      .sample(500 milliseconds)
      .doOnEach(_ => println("Observable.interval 125 -> spawner onNext"))
      .subscribe (_ => checkFirstVisible())
  }

  def end(): Unit = {
    println(s"spawner end")
    def reset(blocks: ArrayBuffer[Block]): Unit = {
      blocks.foreach(b => pooler.free(b))
      blocks.clear()
    }
    reset(vblocks)
    reset(pblocks)
  }

  private def initBlock(iti: ItemToInst): Unit = {
    pblocks += pooler.get(iti.dimen, iti.size).init(iti)
    pooler.free(iti)
  }

  def checkShortInterval(): Unit = {
    checkFirstPending()
    checkLastVisible()
  }

  private def spawn(x: Float, y: Float): Unit = {
    Future {
      println("lets spawn")
      patGen.initDefaultsIti(x, y)
    }
  }

  private def initDefaults(blocks: ArrayBuffer[Block],
                           x: Float, y:Float,
                           count: Int = MathUtils.random(2, 4)): ArrayBuffer[Block] = {
    var i = 0
    val ang = MathUtils.random(10f, 40f)
    while (i < count) {
      val b1 = pooler.get(BlockConst.DimenUp, BlockConst.SizeL)
      val b2 = pooler.get(BlockConst.DimenDown, BlockConst.SizeL)

      b1.init(x + b1.btype.width * i, y, ang).activate()
      b2.init(x + b2.btype.width * i, y, -ang).activate()

//      blocks ++= Seq(b1, b2)
      blocks += b1
      blocks += b2

      i += 1
    }
    blocks.last.asLast()
    blocks
  }

  private def checkFirstVisible(): Unit = {
    println("lets checkFirstVisible -1")
    vblocks.headOption
      .filter { b => camLeft >= b.getRight }
      .foreach { b =>
        println("checkFirstVisible true -1")
        utils.post(() => b.reset())
        vblocks.remove(0)
      }
  }

  private def checkLastVisible(): Unit = {
    println("lets checkLastVisible -2")
    vblocks.lastOption
      .filter { b => b.isLast && camRight >= b.getX }
      .foreach { b =>
        println("checkLastVisible true -2")
        b.notAsLast()
        spawn(b.getRight + b.getWidth / 2, b.getY)
      }
  }

  private def checkFirstPending(): Unit = {
    println("lets checkFirstPending -3")
    pblocks.headOption
      .filter { b => camRight + VisibleSpan >= b.getX }
      .foreach { b =>
        println("checkFirstPending true -3")
        vblocks += b
        pblocks.remove(0)
        utils.post(() => b.activate())
      }
  }

  private def camLeft: Float =
    camera.position.x - camera.viewportWidth * .5f

  private def camRight: Float =
    camera.position.x + camera.viewportWidth * .5f
}
