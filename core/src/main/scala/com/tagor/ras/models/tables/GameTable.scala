package com.tagor.ras.models.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.Showable
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/2/16.
  */
class GameTable(clickListener: ClickListener) extends Table with Showable {

  private var dirImg: Image = _
  private var pauseImg: Image = _
  private var scoreLbl: Label = _
//  private var fpsLbl: Label = _
  private var levelLbl: Label = _

  RxMgr.onPlayerAction
    .filter(i => i == Const.GoUp || i == Const.GoDown)
    .map(_ == Const.GoUp)
    .subscribe(i => rotateDirImg(i))

  RxMgr.newScore
    .subscribe(s => scoreLbl.setText(s.toString))

  RxMgr.newLevel
    .filter(_ > 0)
    .subscribe(l => showLevel(l))

  def init(): Unit = {
    reset()

    setFillParent(true)
//    setDebug(true)
    align(Align.top)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
//    parameter.shadowOffsetY = 3
//    parameter.shadowColor = Color.valueOf(BlockConst.DarkBlue)
    val lblFont = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(lblFont, Color.valueOf(BlockConst.DarkBlue))
//    val labelStyle = new Label.LabelStyle(lblFont, Color.WHITE)

    scoreLbl = new Label("0", labelStyle)
//    fpsLbl = new Label("00", labelStyle)

    parameter.size = 32
//    parameter.shadowColor = Color.valueOf(BlockConst.Red)
    val lvlFnt = generator.generateFont(parameter)
    val lvlStyle = new LabelStyle(lvlFnt, Color.valueOf(BlockConst.Red))
//    val lvlStyle = new LabelStyle(lvlFnt, Color.WHITE)
    generator.dispose()

    levelLbl = new Label("Level 0", lvlStyle)
    levelLbl.setVisible(false)

    dirImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "arrowUp"))
    dirImg.setOrigin(Align.center)

    pauseImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "pause_btn"))
    pauseImg.setOrigin(Align.center)
    pauseImg.setUserObject(Const.PausedStr)
    pauseImg.addListener(clickListener)
    pauseImg.setTouchable(Touchable.enabled)

    add(pauseImg).size(64f).left().pad(12f)
    add(scoreLbl).pad(12f).expandX()
    add(dirImg).size(64f).right().pad(12f)
    row()
    add(levelLbl).colspan(3).center()
//    row()
//    add(fpsLbl).colspan(2).center()

    setVisible(false)
  }
  init()

  private def showLevel(level: Int): Unit = {
    levelLbl.setText(s"Level $level")
    levelLbl.setVisible(true)
    levelLbl.addAction(
      sequence(
        fadeIn(.25f),
        delay(3),
        fadeOut(.5f),
        visible(false)))
  }

  private def rotateDirImg(isUp: Boolean): Unit = {
    dirImg.addAction(
      parallel(
        rotateTo(if (isUp) 0 else -180, Const.TransitTime),
        sequence(
          scaleTo(Const.DownScale, Const.DownScale, Const.TransitTime * .5f),
          scaleTo(Const.UpScale, Const.UpScale, Const.TransitTime * .5f)),
        sequence(
          Actions.color(Color.LIGHT_GRAY, Const.TransitTime * .5f),
          Actions.color(Color.WHITE, Const.TransitTime * .5f)
        )
      )
    )
  }

//  def setFpsText(s: String): Unit = fpsLbl.setText(s)

  override def hide(): Unit = {
    hideAndFunc(() => ())
  }

  override def hideAndFunc(f: () => Unit): Unit = {
    clearActions()
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor(),
        run(runnable(f))
      )
    )
    enableTouchable(false, pauseImg)
  }

  override def show(): Unit = {
    clearActions()
    addAction(
      sequence(
        alpha(0f),
        Actions.visible(true),
        fadeIn(.5f)
      )
    )
    setTouchableEnabled()
  }

  def setTouchableEnabled(): Unit = {
    pauseImg.setTouchable(Touchable.enabled)
  }
}
