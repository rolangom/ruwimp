package com.tagor.ras.models

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.tagor.ras.utils
import com.tagor.ras.utils.{RxMgr, Const, WorldFactory}
import com.tagor.ras.utils.Const.PPM
import rx.scala.concurrency.GdxScheduler

/**
  * Created by rolangom on 12/11/15.
  */

object RxPlayerConst {

  val Jump = Input.Keys.SPACE
  val GoUp = Input.Keys.UP
  val GoDown = Input.Keys.DOWN
  val Toggle = Input.Keys.ENTER

  val Running: Int = 0
  val Jumping: Int = 1
  val Falling: Int = 2

  val MaxSpeed: Float = Const.RunnerLinearVelocity * 2.5f
  val MaxJumpUpSpeed: Float = 10f
  val MinJumpDownSpeed: Float = -2f
  val MaxJumpDownSpeed: Float = -25f

}

object Player extends Player

class Player
  extends B2dActor(
    WorldFactory.newRunner()) {

  private var currentSpeed = Const.RunnerLinearVelocity
  private var isDimenUp = true
  private var groundContacts: Int = 0
  private var stateTime: Float = 0f
  private var jumps: Int = 0
  private val MaxJumps = 2

  private val vtmp = new Vector2()

//  RxMgr.onGameRunning
//    .observeOn(GdxScheduler())
//    .subscribe(r => handleGame(r))

  RxMgr.onPlayerAction
    .subscribe(i => handleInput(i))

  private def init(): Unit = {
    val bodyPos = body.getPosition
    setBounds(
      bodyPos.x * PPM - Const.RunnerWidth / 2,
      bodyPos.y * PPM - Const.RunnerHeight / 2,
      Const.RunnerWidth,
      Const.RunnerHeight)
    setOrigin(Align.center)
    setScale(Const.UpScale)
    body.setActive(false)
    setVisible(false)
    setDebug(true)
  }
  init()

  def hello() = {
    println("Hi!")
  }

  private def handleInput(input:Int): Unit = {
    input match {
      case RxPlayerConst.GoUp => goUp()
      case RxPlayerConst.GoDown => goDown()
      case RxPlayerConst.Jump => jump()
      case RxPlayerConst.Toggle => toggle()
      case _ => ()
    }
  }

  private def handleGame(isRunning: Boolean): Unit = {
    println(s"Player.handleGame $isRunning")
    if (isRunning) activate()
    else reset()
  }

  def activate(): Unit = {
    body.setActive(true)
    body.setTransform(
      Const.RunnerX / PPM,
      Const.RunnerY / PPM, 0f)
    setVisible(true)
    RxMgr.onPlayerAction.onNext(RxPlayerConst.GoUp)
    goUp()
    body.setLinearVelocity(0f, 0f)
    RxMgr.onActorAdded.onNext(this)
  }

  def goUp(): Unit = {
    println("player goUp")
    toFront()
    changeDir(true)
  }

  def goDown(): Unit = {
    println("player goDown")
    toBack()
    changeDir(false)
  }

  private def changeDir(newDir: Boolean): Unit = {
    isDimenUp = newDir
    WorldFactory.configBodyBitsOnAir(body)
    scaleIt()
  }

  def jump(): Unit = {
    println(s"player jump; groundContacts= $groundContacts, jumps= $jumps")
    if (isOnGround || jumps < MaxJumps) {
      stateTime = 0
      body.setLinearVelocity(body.getLinearVelocity.x, 0f)
      body.applyLinearImpulse(
        Const.RunnerJumpingLinearImpulse,
        body.getWorldCenter, true)
      // play sound
      jumps += 1
      if (!isOnGround)
        addAction(Actions.rotateBy(-360, Const.TransitTime))
    }
  }

  def toggle(): Unit = {
    if (isDimenUp) goDown()
    else goUp()
  }

  private def scaleIt(): Unit = {
    val ns = scaleVal
    WorldFactory.scaleFixtures(body, isDimenUp)
    addAction(Actions.sequence(
      Actions.scaleTo(ns, ns, Const.TransitTime),
      Actions.run(configBitsRunnable)))
  }

  def onAir(): Unit = {
    println("player onAir")
    groundContacts -= 1
    stateTime = 0
    jumps = 1
  }

  def landedAt(zIndex: Int): Unit = {
    println("player landed")
    groundContacts += 1
    stateTime = 0f
    setZIndex(zIndex)
    jumps = 0
  }

  def isOnGround: Boolean = groundContacts > 0

  private val configBitsRunnable = new Runnable {
    override def run(): Unit = {
      println(s"configBitsRunnable isDimenUp = $isDimenUp")
      WorldFactory.configBodyBits(body, isDimenUp)
    }
  }

  private def running(): Unit = {
    body.setLinearVelocity(RxPlayerConst.MaxSpeed, body.getLinearVelocity.y)
  }

  private def scaleVal: Float =
    if (isDimenUp) Const.UpScale else Const.DownScale

  def reset(): Unit = {
    body.setTransform(0f, 0f, 0f)
    body.setLinearVelocity(0f, 0f)
    body.setActive(false)
    setVisible(false)
    currentSpeed = Const.RunnerLinearVelocity
    remove() // Remove Actor from stage
    groundContacts = 0
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    vtmp.set(body.getPosition)
      .scl(PPM)
      .add(- getOriginX, - getOriginY)
    setPosition(vtmp.x, vtmp.y)
    running()
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
  }
}
