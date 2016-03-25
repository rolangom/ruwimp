package com.tagor.ras.stages

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{MathUtils, Interpolation, Vector3}
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.tagor.ras.models._
import com.tagor.ras.utils._
import rx.lang.scala.schedulers.ComputationScheduler
import rx.lang.scala.subscriptions.CompositeSubscription

import scala.concurrent.duration.DurationInt

/**
 * Created by rolangom on 7/8/15.
 */
class GameStage(batch: Batch)
  extends Stage(new StretchViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch)
  with ContactListener {

//  private val b2dr = new Box2DDebugRenderer
//  private val b2dCam = new OrthographicCamera
  private val spawner = new Spawner(getCamera.asInstanceOf[OrthographicCamera])

  private val MinCamSpeed = 1f
  private val MaxCamSpeed = 3f
  private var cSpeed = MinCamSpeed
  private val CamTargetX = Const.Width * .65f
  private val newCamPos = new Vector3()
  private val background = new Background(getCamera)
  private var currAct: Float => Unit = emptyAct
  private val player = new Player

  def playerVelX = player.velX

//  b2dCam.setToOrtho(false,
//    getViewport.getWorldWidth / Const.PPM,
//    getViewport.getWorldHeight / Const.PPM)
  getCamera.asInstanceOf[OrthographicCamera]
    .setToOrtho(false,
      getViewport.getWorldWidth,
      getViewport.getWorldHeight)

  player.hello()

  var subs: CompositeSubscription = _

  private def initSubs(): Unit = {
    if (subs == null || subs.isUnsubscribed) subs = CompositeSubscription(
      RxMgr.onActorAdded
        .subscribe(a => post(() => addActor(a))),
      RxMgr.onGameState subscribe ( m => m match {
        case Const.GameStatePlay | Const.GameStateOver =>
          post(() => handleGame(m == Const.GameStatePlay))
        case Const.GameStatePause | Const.GameStateResume =>
          post(() => handleGameMode(m == Const.GameStatePause))
        case Const.GameStateHome =>
          post(() => goHome())
      }),
      RxMgr.newLevel
        .subscribe(l => goFaster())
    )
  }

  def init(): Unit = {
    initSubs()
    background.init()
    player.init()
    RxMgr.onActorAdded.onNext(background)
  }

  def goFaster(): Unit = {
    player.goFaster()
    if (cSpeed < MaxCamSpeed)
      cSpeed += .5f
  }

  private def handleGame(isRunning: Boolean): Unit = {
    if (isRunning) startDelayed()
    else end()
  }

  private def startDelayed(): Unit = {
    preStart()
    addAction(
      Actions.delay(2f, Actions.run(runnable(() => start())))
    )
  }

  private def start(): Unit = {
    println("RGT -> GameStage start")
    player.activate()
    currAct = gameAct
    initInterval()
  }

  private def initInterval(): Unit = {
    val cam = getCamera
    val halfViewportWidth = getViewport.getWorldWidth * .5f
    subs += RxMgr.intervalObs
      .subscribeOn(ComputationScheduler())
      .sample(1 seconds)
      .filter(_ => player.getTop < 0 || player.getRight < cam.position.x - halfViewportWidth)
      .subscribe(_ => RxMgr.onGameState.onNext(Const.GameStateOver))
  }

  private def preStart(): Unit = {
    println("RGT -> GameStage preStart")
    ScoreMgr.reset()
    val cam = getCamera
    newCamPos.set(cam.viewportWidth / 2, cam.position.y, cam.position.z)
    cam.position.set(newCamPos)
    cam.update()
    player.preStart()
    background.start()
    spawner.start()
  }

  private def end(): Unit = {
    ScoreMgr.save()
    SoundMgr.playGameOver()
    currAct = emptyAct
    spawner.end()
    player.reset()
    cSpeed = MinCamSpeed
  }

  private def goHome(): Unit = {
    println("RGT -> GameStage goHome")
    ScoreMgr.reset()
    currAct = emptyAct
    spawner.end()
    player.resumeGame()
    player.reset()
    cSpeed = MinCamSpeed
  }

  private def handleGameMode(isPaused: Boolean): Unit = {
    if (isPaused)
      pauseGame()
    else
      resumeGame()
  }

  def pauseGame(): Unit = {
    currAct = emptyAct
    player.pauseGame()
  }

  private def resumeGame(): Unit = {
    currAct = gameAct
    player.resumeGame()
    spawner.resumeGame()
    initInterval()
  }

  private def gameAct(delta: Float): Unit = {
    // TODO: ONLY WHILE IN DEBUG (Remove)
    val cam = getCamera
//    b2dCam.position.set(
//      cam.position.x / Const.PPM,
//      cam.position.y / Const.PPM,
//      cam.position.z)
//    b2dCam.update()

    newCamPos.x = player.getX() + CamTargetX
//    newCamPos.y = MathUtils.clamp(player.getY(), 0f, 1400f)
    cam.position.interpolate(newCamPos, cSpeed * delta, Interpolation.linear)
  }

  private def emptyAct(delta: Float): Unit = { }

//  override def draw(): Unit = {
//    super.draw()
//    // TODO: ONLY WHILE IN DEBUG (Remove)
//    // draw box2d world
//    b2dr.render(WorldFactory.world, b2dCam.combined)
//  }

  override def act(delta : Float): Unit = {
    super.act(delta)
    currAct(delta)
  }

  override def beginContact(contact: Contact): Unit = {
//    println("beginContact")
    WorldFactory.blockIfLanded(
      contact.getFixtureA,
      contact.getFixtureB).foreach { b =>
        player.landedAt(b.getZIndex)
        if (b.setAsLanded())
          ScoreMgr.increase()
      }
  }

  override def endContact(contact: Contact): Unit = {
    if (WorldFactory.isPlayerAndGround(contact.getFixtureA, contact.getFixtureB))
      player.onAir()
  }

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = { }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = { }

  def resume(): Unit = {
    initSubs()
    background.resume()
    spawner.resume()
    player.resume()
  }

  def pause(): Unit = {
    player.pause()
    spawner.pause()
    background.pause()
    subs.unsubscribe()
    currAct = emptyAct

    if (RxMgr.isGmRunning)
      pauseGame()
  }
}
