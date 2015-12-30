package com.tagor.ras.stages

import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.{Input, Gdx}
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Table}
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.tagor.ras.models.RxPlayerConst
import com.tagor.ras.utils.{ResMgr, RxMgr, Const}
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

  private var levelLbl: Label = _
  private var scoreLbl: Label = _
  private var fpsLbl: Label = _

  private var gameTbl: Table = _
  private var startTbl: Table = _
  private var dashboardTbl: Table = _

  private var dirImg: Image = _

  private lazy val (screenSideR, screenSideL) = getScreenSideRects

  RxMgr.onGameRunning
    .subscribe(r => isRunning = r)

  RxMgr.newScore
    .subscribe(s => scoreLbl.setText(s.toString))

  RxMgr.newLevel
    .subscribe(l => showLevel(l))

  RxMgr.onPlayerAction
    .filter(i => i == RxPlayerConst.GoUp || i == RxPlayerConst.GoDown)
    .map(i => if (i == RxPlayerConst.GoUp) true else false)
    .subscribe(i => rotateDirImg(i))

  private def init(): Unit = {
    gameTbl = new Table()
    gameTbl.setFillParent(true)
//    gameTbl.setDebug(true)
    gameTbl.align(Align.top)
//    gameTbl.setVisible(false)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AldotheApache.ttf"))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    parameter.shadowOffsetX = 2
    parameter.shadowOffsetY = 2
    val lblFont = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(lblFont, Color.WHITE)

    scoreLbl = new Label("00", labelStyle)
    fpsLbl = new Label("00", labelStyle)

    parameter.size = 32
    parameter.shadowOffsetX = 1
    parameter.shadowOffsetY = 1
    val lvlFnt = generator.generateFont(parameter)
    val lvlStyle = new LabelStyle(lvlFnt, Color.YELLOW)

    levelLbl = new Label("Level 0", lvlStyle)
    levelLbl.setVisible(false)

    dirImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "arrowUp"))
    dirImg.setOrigin(Align.center)

    gameTbl.add(scoreLbl)
    gameTbl.row()
    gameTbl.add(levelLbl)
    gameTbl.row()
    gameTbl.add(dirImg).expand().align(Align.left)
    gameTbl.row()
    gameTbl.add(fpsLbl)

    generator.dispose()
    addActor(gameTbl)
  }
  init()

  private def showLevel(level: Int) {
    levelLbl.setText(s"Level $level")
    levelLbl.setVisible(true)
    levelLbl.addAction(
      sequence(
        fadeIn(.25f),
        delay(3),
        fadeOut(.5f),
        run(runToInvisible(levelLbl))))
  }

  private def runToInvisible(actor: Actor): Runnable =
    com.tagor.ras.utils.runnable { () => actor.setVisible(false) }

  private def rotateDirImg(isUp: Boolean): Unit = {
    dirImg.addAction(
      parallel(
        if (isUp) rotateUp else rotateDown,
        sequence(
          scaleTo(Const.DownScale, Const.DownScale, Const.TransitTime / 2),
          scaleTo(Const.UpScale, Const.UpScale, Const.TransitTime / 2)),
        sequence(
          alpha(Const.DownScale, Const.TransitTime / 2),
          alpha(Const.UpScale, Const.TransitTime / 2))
      )
    )
  }

  private def rotateUp = rotateTo(0, Const.TransitTime)
  private def rotateDown = rotateTo(-180, Const.TransitTime)

  private def initLbl: Label = {
    val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AldotheApache.ttf"))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    parameter.shadowOffsetX = 2
    parameter.shadowOffsetY = 2
    val lblFont = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(lblFont, Color.WHITE)
    generator.dispose()
    new Label("00", labelStyle)
  }

  private def createGameTable(): Table = {
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
    fpsLbl.setText(s"FPS: ${Gdx.graphics.getFramesPerSecond}")
  }
}
