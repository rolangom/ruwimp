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

import scala.concurrent.duration.DurationInt

/**
 * Created by rolangom on 7/8/15.
 */
class GameStage(batch: Batch)
  extends Stage(new StretchViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch)
  with ContactListener {

  RxMgr.onActorAdded
    .subscribe(a => post(() => addActor(a)))

  private val b2dr = new Box2DDebugRenderer
  private val b2dCam = new OrthographicCamera
  private val spawner = new Spawner(getCamera.asInstanceOf[OrthographicCamera])

  private val MinCamSpeed = 1f
  private val MaxCamSpeed = 3f
  private var cSpeed = MinCamSpeed
  private val CamTargetX = Const.Width * .65f
  private val newCamPos = new Vector3()
  private val background = new Background(getCamera)
  private var currAct: Float => Unit = emptyAct
  private val player = new Player
  private var gameOverSound: Sound = _

  def playerVelX = player.velX

  b2dCam.setToOrtho(false,
    getViewport.getWorldWidth / Const.PPM,
    getViewport.getWorldHeight / Const.PPM)
  getCamera.asInstanceOf[OrthographicCamera]
    .setToOrtho(false,
      getViewport.getWorldWidth,
      getViewport.getWorldHeight)

  player.hello()

  RxMgr.onGameState
    .filter(s => s == Const.GameStatePlay || s == Const.GameStateOver)
    .map(_ == Const.GameStatePlay)
    .subscribe(r => post(() => handleGame(r)))

  RxMgr.newTheme
    .subscribe(t => init())
  RxMgr.newLevel
    .subscribe(l => goFaster())

  def init(): Unit = {
    spawner.init()
    background.init()
    player.init()
    gameOverSound = ResMgr.getSound("audio/jingles_PIZZA01.mp3")
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
      Actions.delay(1.5f,
        Actions.run(
          runnable(() => start())
        )
      )
    )
  }

  private def start(): Unit = {
      player.activate()
      currAct = gameAct

      RxMgr.intervalObs
        .sample(1 seconds)
        .filter(_ => player.getTop < 0 || player.getRight < getCamera.position.x - getViewport.getWorldWidth * .5f)
        .subscribe(_ => RxMgr.onGameState.onNext(Const.GameStateOver))
  }

  private def preStart(): Unit = {
    spawner.start()
    background.start()
    val cam = getCamera
    newCamPos.set(cam.viewportWidth / 2, cam.position.y, cam.position.z)
    cam.position.set(newCamPos)
    cam.update()
  }

  private def end(): Unit = {
    gameOverSound.play()
    currAct = emptyAct
    spawner.end()
    player.reset()
    cSpeed = MinCamSpeed
    ScoreMgr.saveAndReset()
  }

  private def gameAct(delta: Float): Unit = {
    // TODO: ONLY WHILE IN DEBUG (Remove)
    val cam = getCamera
    b2dCam.position.set(
      cam.position.x / Const.PPM,
      cam.position.y / Const.PPM,
      cam.position.z)
    b2dCam.update()

    newCamPos.x = player.getX() + CamTargetX
//    newCamPos.y = MathUtils.clamp(player.getY(), 0f, 1400f)
    cam.position.interpolate(newCamPos, cSpeed * delta, Interpolation.linear)
  }

  private def emptyAct(delta: Float): Unit = { }

  override def draw(): Unit = {
    super.draw()
    // TODO: ONLY WHILE IN DEBUG (Remove)
    // draw box2d world
    b2dr.render(WorldFactory.world, b2dCam.combined)
  }

  override def act(delta : Float): Unit = {
    super.act(delta)
    currAct(delta)
  }

  override def beginContact(contact: Contact): Unit = {
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

  def disposeLight(): Unit = {
    ResMgr.remove(ResMgr.getThemeTextureStr(BlockConst.BLOCK_INDEX))
    ResMgr.remove(ResMgr.getThemeTextureStr(BlockConst.JOINT_INDEX))
  }
}
