package com.tagor.ras.models

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.tagor.ras.utils._
import com.tagor.ras.utils.Const._

/**
  * Created by rolangom on 12/11/15.
  */

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

  private var runningAnim: Animation = _
  private var jumpingAnim: Animation = _
  private var fallingAnim: Animation = _
  private var chAnim: () => TextureRegion = handleAnimOnAir
  private var cVelY: () => Float = () => velY
  private var n = PrefMgr.currPlayer

  RxMgr.onPlayerAction
    .subscribe(i => handleInput(i))

  RxMgr.playerRegionStream
      .subscribe { i =>
        n = i
        initDisposables()
      }

  def init(): Unit = {
    val bodyPos = body.getPosition
    val (w, h) = (Const.RunnerWidth * 1.4f, Const.RunnerHeight * 1.4f)
    setBounds(
      bodyPos.x * PPM - w / 2,
      bodyPos.y * PPM - h / 2,
      w, h)
    setOrigin(Align.center)
    setScale(Const.UpScale)
    body.setActive(false)
    setVisible(false)
//    setDebug(true)

    initDisposables()
    stateTime = 0f
  }

  private def initDisposables(): Unit = {
    val atlas = ResMgr.getAtlas(Const.BGS_PATH) // ("atlas/player_01_anims.txt")
    runningAnim = new Animation(0.025f * 1.25f, atlas.findRegions(s"player_${n}_running"), PlayMode.LOOP)
    jumpingAnim = new Animation(0.02f * 2, atlas.findRegions(s"player_${n}_jumping"), PlayMode.NORMAL)
    fallingAnim = new Animation(0.1f, atlas.findRegions(s"player_${n}_falling"), PlayMode.NORMAL)
  }

  def hello() = {
    println("Hi!")
  }

  def velX = body.getLinearVelocity.x

  def goFaster(): Unit = {
    if (currentSpeed < Const.RunnerMaxSpeed)
      currentSpeed += .25f// .5f 2f
  }

  private def handleInput(input:Int): Unit = {
    input match {
      case Const.GoUp => goUp()
      case Const.GoDown => goDown()
      case Const.Jump => jump()
      case Const.Toggle => toggle()
      case Const.JumpReleased => jumpReleased()
      case _ => ()
    }
  }

  private def handleGame(isRunning: Boolean): Unit = {
    if (isRunning) activate()
    else reset()
  }

  def preStart(): Unit = {
    body.setTransform(
      Const.RunnerX / PPM,
      Const.RunnerY / PPM, 0f)
    setPosition(Const.RunnerX, Const.RunnerY)
    RxMgr.onPlayerAction.onNext(Const.GoUp)
//    goUp()
    handleAnimOnAir()
    body.setLinearVelocity(0f, 0f)
  }

  def activate(): Unit = {
    body.setActive(true)
    setVisible(true)
    RxMgr.onActorAdded.onNext(this)
  }

  def resume(): Unit = {
    initDisposables()
    if (RxMgr.isGmRunning)
      activate()
  }

  def pause(): Unit = {
    onAir()
//    ResMgr.remove("atlas/player_01_anims.txt")
  }

  def goUp(): Unit = {
    toFront()
    SoundMgr.playGoingUp()
    changeDir(true)
  }

  def goDown(): Unit = {
    toBack()
    SoundMgr.playGoingDown()
    changeDir(false)
  }

  private def changeDir(newDir: Boolean): Unit = {
    isDimenUp = newDir
    WorldFactory.configBodyBitsOnAir(body)
    scaleIt()
  }

  def jump(): Unit = {
    val isOnGround = this.isOnGround
    if (isOnGround || jumps < MaxJumps) {
      stateTime = 0
      body.setLinearVelocity(body.getLinearVelocity.x, 0f)
      body.applyLinearImpulse(Const.RunnerJumpingLinearImpulse, body.getWorldCenter, true)
      SoundMgr.playJump(if (isDimenUp) Const.UpScale else Const.DownScale,
        MathUtils.random(1, 1.2f), 0)
      jumps += 1
      if (!isOnGround)
        addAction(Actions.rotateBy(-360, Const.TransitTime))
    }
    cVelY = () => clampedVelY
  }

  def jumpReleased(): Unit = {
    cVelY = () => velY
  }

  def toggle(): Unit = {
    RxMgr.onPlayerAction.onNext {
      if (isDimenUp) Const.GoDown
      else Const.GoUp
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
    groundContacts -= 1
    stateTime = 0
    jumps = 1
    if (groundContacts <= 0) {
      chAnim = () => handleAnimOnAir()
      SoundMgr.stopFootStep()
    }
  }

  def landedAt(zIndex: Int): Unit = {
    groundContacts += 1
    stateTime = 0f
    setZIndex(if (zIndex < 0) 0 else zIndex)
    jumps = 0
    chAnim = () => handleAnimOnGround()
    cVelY = () => velY

    post(() =>  SoundMgr.playFootStep((if (isDimenUp) Const.UpScale else Const.DownScale) - .5f))
  }

  def isOnGround: Boolean = groundContacts > 0

  private val configBitsRunnable = new Runnable {
    override def run(): Unit =
      WorldFactory.configBodyBits(body, isDimenUp)
  }

  private def velY = body.getLinearVelocity.y

  private def clampedVelY = MathUtils.clamp(body.getLinearVelocity.y, -2.5f, Integer.MAX_VALUE)

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
    cVelY = () => velY
    SoundMgr.stopFootStep()
  }

  def pauseGame(): Unit = {
    body.setGravityScale(0)
    SoundMgr.stopFootStep()
  }

  def resumeGame(): Unit = {
    body.setGravityScale(Const.RunnerGravityScale)
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    vtmp.set(body.getPosition)
    setPosition(vtmp.x * PPM - getOriginX, vtmp.y * PPM - getOriginY)
    body.setLinearVelocity(currentSpeed, cVelY())
    stateTime += delta
  }

  private def handleAnimOnGround(): TextureRegion = runningAnim.getKeyFrame(stateTime)

  private def handleAnimOnAir(): TextureRegion =
    if (body.getLinearVelocity.y < 0)
      fallingAnim.getKeyFrame(stateTime)
    else
      jumpingAnim.getKeyFrame(stateTime)

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    val region = chAnim()
    batch.draw(region, getX, getY, getOriginX, getOriginY, region.getRegionWidth, region.getRegionHeight,
      getScaleX, getScaleY, getRotation)
  }
}
