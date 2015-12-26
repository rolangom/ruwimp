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

  override def show() = {

  }

  override def hide() = { }

  override def resize(width: Int, height: Int) = {
    gameStage.getViewport.update(width, height)
    uiStage.getViewport.update(width, height)
  }

  override def dispose() = {
    gameStage dispose()
    uiStage dispose()
  }

  override def render(delta: Float) = {
    val gl = Gdx.gl
    gl.glClearColor(0, 0, 0, 0)
    gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    renderFixed(delta)
    gameStage.draw()

    uiStage.act(delta)
    uiStage.draw()
  }

  private def renderFixed(delta : Float) {
    val frameTime = Math.min(delta, 0.25f)
    accumulator += frameTime
    while (accumulator >= TIME_STEP) {
      world.step(TIME_STEP, 8, 2)
      accumulator -= TIME_STEP
      gameStage.act(TIME_STEP)
    }
  }

  override def pause() = { }

  override def resume() = { }
}
