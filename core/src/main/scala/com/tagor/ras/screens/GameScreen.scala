package com.tagor.ras.screens

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Screen}
import com.tagor.ras.stages.{BgStage, UiStage, GameStage}
import com.tagor.ras.utils._

/**
 * Created by rolangom on 6/8/15.
 */
class GameScreen extends Screen {

  private var (r, b, g) = (0f, 0f, 0f)

  private val world = WorldFactory.world
  private val batch = new SpriteBatch
  private val gameStage = new GameStage(batch)
  private val uiStage = new UiStage(batch)
  private var worldAct: Float => Unit = renderFixed

  private var accumulator = 0f
  private val TIME_STEP = 0.01f// 1f / (if (Gdx.app.getType == ApplicationType.iOS) 60f else 300f)

  RxMgr.newTheme
    .subscribe(_ => invalidate())
  RxMgr.onGameState
    .filter(s => s == Const.GameStatePause || s == Const.GameStateResume)
    .map(_ == Const.GameStatePause)
    .subscribe(p => handleGameState(p))

  private def invalidate(): Unit = {
    val color = Color.valueOf(ThemeMgr.getBgColorStr(BlockConst.BG2_COLOR_INDEX))
    r = color.r
    g = color.g
    b = color.b
  }

  override def show(): Unit = {
    gameStage.init()
    uiStage.init()
    invalidate()

    world.setContactListener(gameStage)
    Gdx.input.setInputProcessor(uiStage)
  }

  override def hide(): Unit = {
    dispose()
  }

  override def resize(width: Int, height: Int): Unit = {
    gameStage.getViewport.update(width, height)
    uiStage.getViewport.update(width, height)
  }

  override def dispose(): Unit = {
    gameStage dispose()
    uiStage dispose()
    ResMgr dispose()
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
    worldAct = if (isPaused) emptyAct else renderFixed
  }

  override def pause(): Unit = {
    println("RGT -> paused")
    world.setContactListener(null)
    Gdx.input.setInputProcessor(null)
    gameStage.pause()
    uiStage.pause()

    if (RxMgr.isGmRunning)
      handleGameState(true)
  }

  override def resume(): Unit = {
    world.setContactListener(gameStage)
    Gdx.input.setInputProcessor(uiStage)

    invalidate()
    gameStage.resume()
    uiStage.resume()
//    handleGameState(false)
  }
}
