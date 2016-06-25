package com.tagor.ras.models

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.tagor.ras.utils
import com.tagor.ras.utils._
import rx.lang.scala.schedulers.ComputationScheduler

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
//  private lazy val vspan = BlockConst.Sizes(BlockConst.SizeM) * BlockConst.Size

  RxMgr.onItiAdded
    .subscribe(i => utils.post(() => initBlock(i)))

  def resume(): Unit = {
    println("spanwer resume")
    vblocks.foreach(_.resumeActivated())
    pblocks.foreach(_.resumeInited())
//    pooler.resume()
  }

  def resumeGame(): Unit = {
    subscribeInterval()
  }

  def pause(): Unit = {
    vblocks.foreach(_.pause())
    pblocks.foreach(_.pause())
    Block.pause()
  }

  def start(): Unit = {
    println("spanwer start")
    subscribeInterval()
    val camPos = camera.position
    utils.post { () =>
      initDefaults(vblocks, camPos.x, camPos.y)
    }
  }

  private def subscribeInterval(): Unit = {
    RxMgr.intervalObs
      .subscribeOn(ComputationScheduler())
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

  private def spawn(x: Float, y: Float, pblock: Block): Unit = {
    Future {
      println("lets spawn")
      patGen.genRandSeq(x, y, pblock)
    }
  }

  private def initDefaults(blocks: ArrayBuffer[Block],
                           x: Float, y:Float): Unit = {

    val ang = 15
    val b1 = pooler.get(BlockConst.DimenUp, BlockConst.SizeXL)
    val b2 = pooler.get(BlockConst.DimenDown, BlockConst.SizeXL)

    b1.init(x, y, ang).activate()
    b2.init(x, y, -ang).activate()

    blocks += b1
    blocks += b2

    patGen.genRandSeq(b2.rightX, b2.rightY, b2)
  }

  private def checkFirstVisible(): Unit = {
    vblocks.headOption
      .filter { b => camLeft >= b.getRight }
      .foreach { b =>
        utils.post(() => pooler.free(b))
        vblocks.remove(0)
      }
  }

  private def checkLastVisible(): Unit = {
    vblocks.lastOption
      .filter { b => b.isLast && camRight >= b.getX }
      .foreach { b =>
        b.notAsLast()
        utils.post(() => spawn(b.rightX, b.rightY, b))
      }
  }

  private def checkFirstPending(): Unit = {
    pblocks.headOption
      .filter { b => camRight >= b.getX }
      .foreach { b =>
        vblocks += b
        pblocks.remove(0)
        utils.post(() => b.activate())
      }
  }

  private def camLeft: Float =
    camera.position.x - camera.viewportWidth * .5f

  private def camRight: Float =
    camera.position.x + camera.viewportWidth * 1.5f
}
