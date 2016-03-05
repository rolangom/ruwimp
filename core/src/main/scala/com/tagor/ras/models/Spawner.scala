package com.tagor.ras.models

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.tagor.ras.utils
import com.tagor.ras.utils.{RxMgr, BlockPooler, BlockConst}
import rx.lang.scala.schedulers.ComputationScheduler
import rx.scala.concurrency.GdxScheduler

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.DurationInt

/**
  * Created by rolangom on 12/14/15.
  */
class Spawner(camera: OrthographicCamera) {

  private lazy val pooler = new BlockPooler
  private lazy val patGen = new PatternGenerator(pooler)
  private lazy val vblocks = ArrayBuffer[Block]()
  private lazy val pblocks = ArrayBuffer[Block]()
  private lazy val vspan = BlockConst.Sizes(BlockConst.SizeM) * BlockConst.Size

  RxMgr.onItiAdded
    .subscribe(i => utils.post(() => initBlock(i)))

  def resume(): Unit = {
    println("spanwer resume")
    vblocks.foreach(_.resumeActivated())
    pblocks.foreach(_.resumeInited())
//    pooler.resume()
  }

  def pause(): Unit = {
    vblocks.foreach(_.pause())
    pblocks.foreach(_.pause())
    Block.pause()
  }

  def start(): Unit = {
    println("spanwer start")
    val camPos = camera.position
    utils.post { () =>
      initDefaults(vblocks, camPos.x, camPos.y, 1)
        .lastOption
        .foreach(b => spawn(b.maxX, b.maxY))
    }
    RxMgr.intervalObs
      .subscribeOn(ComputationScheduler())
//      .sample(250 milliseconds)
      .subscribe(_ => checkShortInterval())
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
//    println("block inited")
    pblocks += pooler.get(iti).init(iti)
    pooler.free(iti)
  }

  private def checkShortInterval(): Unit = {
    checkFirstPending()
    checkLastVisible()
    // Not sure line
    checkFirstVisible()
  }

  private def spawn(x: Float, y: Float): Unit = {
    Future {
      println("lets spawn")
//      patGen.genLinearSeq(x, y)
//      patGen.genSeqX(x, y)
//      patGen.genSeqV(x, y)
//      patGen.genSeqInvV(x, y)
//      patGen.genSeqVandInvV(x, y)
//      patGen.genSeqInvVandV(x, y)
//      patGen.genSeqLT(x, y)
//      patGen.genSeqGT(x, y)
//      patGen.genDiamond(x, y)
//      patGen.genGtAndLt(x, y)
//      patGen.genParallelSeq(x, y)
//      patGen.genParPairSeqV(x, y)
      patGen.genRandSeq(x, y)
    }
  }

  private def initDefaults(blocks: ArrayBuffer[Block],
                           x: Float, y:Float,
                           count: Int = MathUtils.random(2, 4)): Array[Block] = {
    var i = 0
    val ang = 15 // MathUtils.random(10f, 40f)
    while (i < count) {
      val b1 = pooler.get(BlockConst.DimenUp, BlockConst.SizeXL)
      val b2 = pooler.get(BlockConst.DimenDown, BlockConst.SizeXL)

      b1.init(x + b1.btype.width * i, y, ang).activate()
      b2.init(x + b2.btype.width * i, y, -ang).activate()

//      blocks ++= Seq(b1, b2)
      blocks += b1
      blocks += b2

      i += 1
    }
    blocks.last.asLast()
    blocks.toArray
  }

  private def checkFirstVisible(): Unit = {
//    println("lets checkFirstVisible -1")
    vblocks.headOption
      .filter { b => camLeft >= b.getRight }
      .foreach { b =>
//        println("checkFirstVisible true -1")
        utils.post(() => pooler.free(b))
        vblocks.remove(0)
      }
  }

  private def checkLastVisible(): Unit = {
//    println("lets checkLastVisible -2")
    vblocks.lastOption
      .filter { b => b.isLast && camRight >= b.getX }
      .foreach { b =>
//        println("checkLastVisible true -2")
        b.notAsLast()
        utils.post(() => spawn(b.maxX, b.maxY))
      }
  }

  private def checkFirstPending(): Unit = {
//    println("lets checkFirstPending -3")
    pblocks.headOption
      .filter { b => camRight + vspan >= b.getX }
      .foreach { b =>
//        println("checkFirstPending true -3")
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
