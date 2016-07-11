package com.tagor.ras.screens

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.{Gdx, Screen}
import com.tagor.ras.stages.{GameStage, UiStage}
import com.tagor.ras.utils._

/**
 * Created by rolangom on 6/8/15.
 */
class GameScreen extends Screen {

  private val world = WorldFactory.world
  private val batch = new SpriteBatch
  private val gameStage = new GameStage(batch)
  private val uiStage = new UiStage(batch)
  private var worldAct: Float => Unit = renderFixed
  private var (r, g, b) = (1f, 1f, 1f)

  private var accumulator = 0f
  private val TIME_STEP = 0.01f// 1f / (if (Gdx.app.getType == ApplicationType.iOS) 60f else 300f)

  RxMgr.onGameState
    .filter(s => s == Const.GameStatePause || s == Const.GameStateResume || s == Const.GameStatePlay || s == Const.GameStateOver)
    .map(s => s == Const.GameStatePause || s == Const.GameStateOver)
    .subscribe(p => handleGameState(p))

  RxMgr.newTheme
      .subscribe(i => onNewTheme(i))

  override def show(): Unit = {
    gameStage.init()
    uiStage.init()

    world.setContactListener(gameStage)
    Gdx.input.setInputProcessor(uiStage)
    SoundMgr.init()

    onNewTheme(Const.ThemeColor)
    onNewTheme(Const.ThemeImg)
  }

  override def hide(): Unit = {
    dispose()
  }

  override def resize(width: Int, height: Int): Unit = {
    gameStage.getViewport.update(width, height)
    uiStage.getViewport.update(width, height)
  }

  override def dispose(): Unit = {
    Gdx.app.log("RGT", "Screen to dispose")
    gameStage dispose()
    uiStage dispose()
    ResMgr dispose()
    WorldFactory.dispose()
  }

  override def render(delta: Float): Unit = {
    val gl = Gdx.gl
    gl.glClearColor(r, g, b, 1f)
    gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    worldAct(delta)
    gameStage.draw()

    uiStage.act(delta)
    uiStage.draw()
  }

  private def emptyAct(delta: Float): Unit = { }

  private def renderFixed(delta : Float): Unit = {
    val frameTime = Math.min(delta, 0.25f)
    accumulator += frameTime
    while (accumulator >= TIME_STEP) {
      world.step(TIME_STEP, 6, 3)
      accumulator -= TIME_STEP
      gameStage.act(TIME_STEP)
    }
  }

  private def handleGameState(isPaused: Boolean): Unit = {
    println(s"RGT -> GameScreen handleGameState (isPaused= $isPaused)")
    worldAct = if (isPaused) emptyAct else renderFixed
  }

  private def onNewTheme(themeItem: Int): Unit = {
    if (themeItem == Const.ThemeColor) {
      val bgColor = ThemeMgr.getBgColor(BlockConst.BG1_COLOR_INDEX)
      r = bgColor.r
      g = bgColor.g
      b = bgColor.b
    }
    gameStage.onNewTheme(themeItem)
  }

  override def pause(): Unit = {
    println("RGT -> paused")
    world.setContactListener(null)
    Gdx.input.setInputProcessor(null)
    gameStage.pause()
    uiStage.pause()
    SoundMgr.pause()

    if (RxMgr.isGmRunning)
      handleGameState(true)
  }

  override def resume(): Unit = {
    println("RGT -> resumed")
    world.setContactListener(gameStage)
    Gdx.input.setInputProcessor(uiStage)

    gameStage.resume()
    uiStage.resume()
    SoundMgr.resume()
  }
}
