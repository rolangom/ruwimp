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

  var currentSpeed = Const.RunnerLinearVelocity
  var isDimenUp = true

  val vtmp = new Vector2()

//  RxMgr.onGameRunning
//    .observeOn(GdxScheduler())
//    .subscribe(r => handleGame(r))

  RxMgr.onPlayerAction
    .subscribe(i => handleInput(i))

  private def init() = {
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

  private def handleInput(input:Int):Unit = {
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

  def activate() = {
    body.setActive(true)
    body.setTransform(
      Const.RunnerX / PPM,
      Const.RunnerY / PPM, 0f)
    setVisible(true)
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

  private def changeDir(newDir: Boolean) = {
    isDimenUp = newDir
    WorldFactory.configBodyBitsOnAir(body)
    scaleIt()
  }

  def jump(): Unit = {
    println("player jump")
    body.applyLinearImpulse(
      Const.RunnerJumpingLinearImpulse,
      body.getWorldCenter, true)
  }

  def toggle(): Unit = {
    if (isDimenUp) goDown()
    else goUp()
  }

  private def scaleIt():Unit = {
    val ns = scaleVal
    WorldFactory.scaleFixtures(body, isDimenUp)
    addAction(Actions.sequence(
      Actions.scaleTo(ns, ns, Const.TransitTime),
      Actions.run(configBitsRunnable)))
  }

  val configBitsRunnable = new Runnable {
    override def run(): Unit = {
      println(s"configBitsRunnable isDimenUp = $isDimenUp")
      WorldFactory.configBodyBits(body, isDimenUp)
    }
  }

  private def running(): Unit = {
    body.setLinearVelocity(RxPlayerConst.MaxSpeed, body.getLinearVelocity.y)
  }

  private def scaleVal =
    if (isDimenUp) Const.UpScale else Const.DownScale

  def reset() = {
    body.setLinearVelocity(0f, 0f)
    body.setActive(false)
    setVisible(false)
    currentSpeed = Const.RunnerLinearVelocity
    remove() // Remove Actor from stage
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
