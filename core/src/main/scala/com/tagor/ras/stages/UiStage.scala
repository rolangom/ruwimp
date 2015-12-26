package com.tagor.ras.stages

import com.badlogic.gdx.{Input, Gdx}
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Table}
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.tagor.ras.models.RxPlayerConst
import com.tagor.ras.utils.{RxMgr, Const}
import rx.scala.concurrency.GdxScheduler

import scala.concurrent.duration.DurationInt

/**
  * Created by rolangom on 12/12/15.
  */
class UiStage(batch: Batch)
  extends Stage(new FitViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch) {

  private var isRunning = false

  private lazy val scoreLbl = initLbl
  private lazy val gameTable = createGameTable()

  private lazy val (screenSideR, screenSideL) = getScreenSideRects

  RxMgr.onGameRunning
    .sample(1 seconds)
    .observeOn(GdxScheduler())
    .subscribe(r => isRunning = r)

  private def init(): Unit = {
    addActor(gameTable)
    gameTable.add(scoreLbl)
  }
  init()

  private def initLbl:Label = {
    val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AldotheApache.ttf"))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    val lblFont = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(lblFont, Color.WHITE)
    generator.dispose()
    new Label("00", labelStyle)
  }

  private def createGameTable():Table = {
    val table = new Table()
    table.setFillParent(true)
    table.align(Align.top)
    addActor(table)
    table
  }

  private def getScreenSideRects =
    (new Rectangle(Gdx.graphics.getWidth / 2,
      scoreLbl.getHeight,
      Gdx.graphics.getWidth / 2,
      Gdx.graphics.getHeight - scoreLbl.getHeight),
    new Rectangle(0, scoreLbl.getHeight,
      Gdx.graphics.getWidth / 2,
      Gdx.graphics.getHeight - scoreLbl.getHeight))

  override def keyDown(keyCode: Int): Boolean = {
    if (isRunning){
      RxMgr.onPlayerAction.onNext(keyCode)
      return true
    } else {
      if(keyCode == Input.Keys.SPACE) {
        RxMgr.onGameRunning.onNext(true)
        return true
      }
    }
    super.keyDown(keyCode)
  }

  //  val funcKeyDownRunning:(Int => Boolean) = (keyCode:Int) => {
  //    onPlayerActSubj.onNext(keyCode)
  //    true
  //  }
  //
  //  val funcKeyDown

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    if (isRunning){
      if (screenSideR.contains(screenX, screenY)) {
        RxMgr.onPlayerAction.onNext(RxPlayerConst.Jump)
        return true
      } else if (screenSideL.contains(screenX, screenY)) {
        RxMgr.onPlayerAction.onNext(RxPlayerConst.Toggle)
        return true
      }
    } else {
      RxMgr.onGameRunning.onNext(true)
      return true
    }
    super.touchDown(screenX, screenY, pointer, button)
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    scoreLbl.setText(s"FPS: ${Gdx.graphics.getFramesPerSecond}")
  }
}
