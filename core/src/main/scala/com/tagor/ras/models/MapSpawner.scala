package com.tagor.ras.models

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.tagor.ras.utils
import com.tagor.ras.utils.{ScoreMgr, BlockPooler, WorldParser, RxMgr, BlockConst}
import rx.lang.scala.schedulers.ComputationScheduler

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by rolangom on 6/11/16.
  */
class MapSpawner(camera: OrthographicCamera) {

  private lazy val pooler = new BlockPooler
  private lazy val vblocks: ListBuffer[Block] = ListBuffer[Block]()
  private lazy val pblocks: ListBuffer[Block] = ListBuffer[Block]()
  private lazy val worldParser: WorldParser = new WorldParser(pooler)
  private lazy val patGen: PatternGenerator = new PatternGenerator(pooler)

  private var lastBlockReached = 0

  private var itiSub = getSubs

  def resume(): Unit = {
    println("spanwer resume")
    vblocks.foreach(_.resumeActivated())
    pblocks.foreach(_.resumeInited())
    //    pooler.resume()
  }

  def resumeGame(): Unit = {
    subscribeInterval()
  }

  private def getSubs = RxMgr.onItiAdded
    .subscribe(i => utils.post(() => initBlock(i)))


  def pause(): Unit = {
    vblocks.foreach(_.pause())
    pblocks.foreach(_.pause())
//    ResMgr.removeThemeTextureStr(BlockConst.BLOCK_INDEX)
  }

  def start(): Unit = {
    println("spanwer start")
    clear()
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
    if (itiSub == null || itiSub.isUnsubscribed)
      itiSub = getSubs
  }

  def end(): Unit = {
    println(s"spawner end")
//    clear()
  }

  def clear(): Unit = {
    def reset(blocks: ListBuffer[Block]): Unit = {
      blocks.foreach(b => pooler.free(b))
      blocks.clear()
    }
    reset(vblocks)
    reset(pblocks)
  }

  private def initBlock(iti: ItemToInst): Unit = {
//    println(s"block inited $iti")
    pblocks += pooler.get(iti).init(iti)
    pooler.free(iti)
  }

  private def checkShortInterval(): Unit = {
    checkFirstPending()
    checkLastVisible()
    checkFirstVisible()
  }

  private def spawn(x: Float, y: Float, pblock: Block): Unit = {
    Future {
//      println(s"lets spawn; x= $x")
      generate(x, y, pblock)
    }
  }

  private def initDefaults(blocks: ListBuffer[Block],
                           x: Float, y:Float): Unit = {
    val ang = 15
    val b1 = pooler.get(BlockConst.DimenUp, BlockConst.SizeXL)
    val b2 = pooler.get(BlockConst.DimenDown, BlockConst.SizeXL)

    b1.init(x, y, ang).activate()
    b2.init(x, y, -ang).activate()

    blocks += b1
    blocks += b2

    generate(b2.rightX, b2.rightY, b2)
  }

  private def generate(x: Float, y: Float, pblock: Block): Unit = {
    if (MathUtils.randomBoolean())
      worldParser.objsFromLevel2(levelFromHardness, MathUtils.random(1, 3), x)
    else
      patGen.genRandSeq(x, y, pblock)
  }

  private def levelFromHardness =
    if (!ScoreMgr.isHard) MathUtils.random(0, 7) else MathUtils.random(8, 13)

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

        lastBlockReached += 1

        if (lastBlockReached == 2) {
          lastBlockReached = 0
          ScoreMgr.increaseLevel()
        }

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
