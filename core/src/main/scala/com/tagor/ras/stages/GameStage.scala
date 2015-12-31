package com.tagor.ras.stages

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Interpolation, Vector3}
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.tagor.ras.models._
import com.tagor.ras.utils._
import rx.scala.concurrency.GdxScheduler

import scala.concurrent.duration.DurationInt

/**
 * Created by rolangom on 7/8/15.
 */
class GameStage(batch: Batch)
  extends Stage(new StretchViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch)
  with ContactListener {

  private val b2dr = new Box2DDebugRenderer
  private val b2dCam = new OrthographicCamera
  private val spawner = new Spawner(getCamera.asInstanceOf[OrthographicCamera])

  private val MinCamSpeed = 1
  private val MaxCamSpeed = 5
  private var cSpeed = MinCamSpeed
  private val CamTargetX = Const.Width * .65f
  private val newCamPos = new Vector3()

  b2dCam.setToOrtho(false,
    getViewport.getWorldWidth / Const.PPM,
    getViewport.getWorldHeight / Const.PPM)
  getCamera.asInstanceOf[OrthographicCamera]
    .setToOrtho(false,
      getViewport.getWorldWidth,
      getViewport.getWorldHeight)

  Player.hello()

  RxMgr.onActorAdded
    .doOnNext(a => println(s"actor added ${a.getClass.getName}"))
    .subscribe(a => post(() => addActor(a)))

  RxMgr.onGameRunning
    .subscribe(r => post(() => handleGame(r)))

  private def init(): Unit = {
    val viewport = getViewport
    val camPos = getCamera.position.set(
      viewport.getWorldWidth / 2f,
      viewport.getWorldHeight / 2f, 1f)
    newCamPos.set(0f, camPos.y, camPos.z)
  }
  init()

  private def handleGame(isRunning: Boolean): Unit = {
    if (isRunning) start()
    else end()
  }

  private def start(): Unit = {
    val cam = getCamera
    newCamPos.set(cam.viewportWidth / 2, cam.position.y, cam.position.z)
    cam.position.set(newCamPos)
    cam.update()
    spawner.start()
    Player.activate()

    RxMgr.intervalObs
      .sample(1 seconds)
      .filter(_ => Player.getTop < 0 || Player.getRight < getCamera.position.x - getViewport.getWorldWidth * .5f)
      .doOnEach(_ => println("Player is out"))
      .subscribe(_ => RxMgr.onGameRunning.onNext(false))
  }

  private def end(): Unit = {
    spawner.end()
    Player.reset()
    ScoreMgr.saveAndReset()
  }

  override def draw(): Unit = {
    super.draw()
    // TODO: ONLY WHILE IN DEBUG (Remove)
    // draw box2d world
    b2dr.render(WorldFactory.world, b2dCam.combined)
  }

  override def act(delta : Float): Unit = {
    super.act(delta)
    // TODO: ONLY WHILE IN DEBUG (Remove)
    val cam = getCamera
    b2dCam.position.set(
      cam.position.x / Const.PPM,
      cam.position.y / Const.PPM,
      cam.position.z)
    b2dCam.update()

    newCamPos.x = Player.getX() + CamTargetX
//    newCamPos.y = MathUtils.clamp(player.getY(), 0f, 1400f);
    cam.position.interpolate(newCamPos, cSpeed * delta, Interpolation.linear)
  }

  override def beginContact(contact: Contact): Unit = {
    WorldFactory.blockIfLanded(
      contact.getFixtureA,
      contact.getFixtureB).foreach { b =>
        Player.landedAt(b.getZIndex)
        if (b.setAsLanded())
          ScoreMgr.increase()
      }
  }

  override def endContact(contact: Contact): Unit = {
    if (WorldFactory.isPlayerAndGround(contact.getFixtureA, contact.getFixtureB))
      Player.onAir()
  }

  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = { }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = { }
}
