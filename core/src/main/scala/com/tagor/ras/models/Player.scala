package com.tagor.ras.models

import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Animation, Batch}
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.tagor.ras.utils.{ResMgr, RxMgr, Const, WorldFactory}
import com.tagor.ras.utils.Const.PPM

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

  val MaxSpeed: Float = Const.RunnerLinearVelocity * 2f
  val MaxJumpUpSpeed: Float = 10f
  val MinJumpDownSpeed: Float = -2f
  val MaxJumpDownSpeed: Float = -25f

}

class Player
  extends B2dActor(
    WorldFactory.newRunner()) {

  private val MinSpeed = 4
  private val MaxSpeed = 8
  private var currentSpeed = Const.RunnerLinearVelocity
  private var isDimenUp = true
  private var groundContacts: Int = 0
  private var stateTime: Float = 0f
  private var jumps: Int = 0
  private val MaxJumps = 2

  private val vtmp = new Vector2()

  private var runningAnim: Animation = _
  private var jumpingAnim: Animation = _
  private var fallingAnim: Animation = _
  private var chAnim: () => TextureRegion = handleAnimOnAir

  private var footStepSound: Sound = _
  private var jumpSound: Sound = _
  private var goingUpSound: Sound = _
  private var goingDownSound: Sound = _

  RxMgr.onPlayerAction
    .subscribe(i => handleInput(i))

  def init(): Unit = {
    val bodyPos = body.getPosition
    val (w, h) = (Const.RunnerWidth * 1.2f, Const.RunnerHeight * 1.2f)
    setBounds(
      bodyPos.x * PPM - w / 2,
      bodyPos.y * PPM - h / 2,
      w, h)
    setOrigin(Align.center)
    setScale(Const.UpScale)
    body.setActive(false)
    setVisible(false)
    setDebug(true)

    val atlas = ResMgr.getAtlas("atlas/player_01_anims.txt")
    runningAnim = new Animation(0.02f, atlas.findRegions("running_"), PlayMode.LOOP)
    jumpingAnim = new Animation(0.02f, atlas.findRegions("jumping_"), PlayMode.NORMAL)
    fallingAnim = new Animation(0.1f, atlas.findRegions("falling_"), PlayMode.NORMAL)
    stateTime = 0f

    footStepSound = ResMgr.getSound("audio/footstep09.mp3")
    jumpSound = ResMgr.getSound("audio/phaseJump1.mp3")
    goingUpSound = ResMgr.getSound("audio/highUp.mp3")
    goingDownSound = ResMgr.getSound("audio/highDown.mp3")
  }

  def hello() = {
    println("Hi!")
  }

  def velX = body.getLinearVelocity.x

  def goFaster(): Unit = {
    if (currentSpeed < RxPlayerConst.MaxSpeed)
      currentSpeed += 1f
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
    handleAnimOnAir()
    RxMgr.onActorAdded.onNext(this)
  }

  def goUp(): Unit = {
    println("player goUp")
    toFront()
    goingUpSound.play()
    changeDir(true)
  }

  def goDown(): Unit = {
    println("player goDown")
    toBack()
    goingDownSound.play()
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
      jumpSound.play(if (isDimenUp) Const.UpScale else Const.DownScale, MathUtils.random(1, 1.2f), 0)
      jumps += 1
      if (!isOnGround)
        addAction(Actions.rotateBy(-360, Const.TransitTime))
    }
  }

  def toggle(): Unit = {
    RxMgr.onPlayerAction.onNext {
      if (isDimenUp) RxPlayerConst.GoDown
      else RxPlayerConst.GoUp
    }
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
    if (groundContacts <= 0)
      chAnim = () => handleAnimOnAir()
    footStepSound.stop()
  }

  def landedAt(zIndex: Int): Unit = {
    println("player landed")
    groundContacts += 1
    stateTime = 0f
    setZIndex(if (zIndex < 0) 0 else zIndex)
    jumps = 0
    chAnim = () => handleAnimOnGround()

    footStepSound.play()
    footStepSound.loop((if (isDimenUp) Const.UpScale else Const.DownScale) - .3f, 1.35f, 0)
  }

  def isOnGround: Boolean = groundContacts > 0

  private val configBitsRunnable = new Runnable {
    override def run(): Unit = {
      println(s"configBitsRunnable isDimenUp = $isDimenUp")
      WorldFactory.configBodyBits(body, isDimenUp)
    }
  }

  private def running(): Unit = {
    body.setLinearVelocity(currentSpeed, body.getLinearVelocity.y)
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
    stateTime += delta
  }

  private def handleAnimOnGround(): TextureRegion = runningAnim.getKeyFrame(stateTime)

  private def handleAnimOnAir(): TextureRegion =
    if (body.getLinearVelocity.y < 0)
      fallingAnim.getKeyFrame(stateTime)
    else jumpingAnim.getKeyFrame(stateTime)

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.draw(chAnim(), getX, getY, getOriginX, getOriginY, getWidth, getHeight,
      getScaleX, getScaleY, getRotation)
  }
}
