package com.tagor.ras.screens

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Screen}
import com.tagor.ras.stages.{UiStage, GameStage}
import com.tagor.ras.utils.WorldFactory

/**
 * Created by rolangom on 6/8/15.
 */
class GameScreen extends Screen {

  val world = WorldFactory.world
  val batch = new SpriteBatch
  val gameStage = new GameStage(batch)
  val uiStage = new UiStage(batch)

  world.setContactListener(gameStage)
  Gdx.input.setInputProcessor(uiStage)

  var accumulator = 0f
  val TIME_STEP = 0.01f// 1f / (if (Gdx.app.getType == ApplicationType.iOS) 60f else 300f)

  override def show(): Unit = {

  }

  override def hide(): Unit = { }

  override def resize(width: Int, height: Int): Unit = {
    gameStage.getViewport.update(width, height)
    uiStage.getViewport.update(width, height)
  }

  override def dispose(): Unit = {
    gameStage dispose()
    uiStage dispose()
  }

  override def render(delta: Float): Unit = {
    val gl = Gdx.gl
    gl.glClearColor(.5f, .5f, .5f, .5f)
    gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    renderFixed(delta)
    gameStage.draw()

    uiStage.act(delta)
    uiStage.draw()
  }

  private def renderFixed(delta : Float): Unit = {
    val frameTime = Math.min(delta, 0.25f)
    accumulator += frameTime
    while (accumulator >= TIME_STEP) {
      world.step(TIME_STEP, 6, 3)
      accumulator -= TIME_STEP
      gameStage.act(TIME_STEP)
    }
  }

  override def pause(): Unit = { }

  override def resume(): Unit = { }
}
