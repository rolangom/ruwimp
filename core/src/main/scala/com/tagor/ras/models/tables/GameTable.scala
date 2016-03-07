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
import com.tagor.ras.models.{Showable, RxPlayerConst}
import com.tagor.ras.utils.{RxMgr, Const, ResMgr}

/**
  * Created by rolangom on 2/2/16.
  */
class GameTable(clickListener: ClickListener) extends Table with Showable {

  private var dirImg: Image = _
  private var pauseImg: Image = _
  private var scoreLbl: Label = _
  private var fpsLbl: Label = _
  private var levelLbl: Label = _

  RxMgr.onPlayerAction
    .filter(i => i == RxPlayerConst.GoUp || i == RxPlayerConst.GoDown)
    .map(_ == RxPlayerConst.GoUp)
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
    generator.dispose()

    levelLbl = new Label("Level 0", lvlStyle)
    levelLbl.setVisible(false)

    dirImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "arrowUp"))
    dirImg.setOrigin(Align.center)

    pauseImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    pauseImg.setOrigin(Align.center)
    pauseImg.setUserObject(Const.PausedStr)
    pauseImg.addListener(clickListener)
    pauseImg.setTouchable(Touchable.enabled)

    add(scoreLbl).expandX()
    add(pauseImg).size(48f).align(Align.right).padRight(24f)
    row()
    add(levelLbl)
    row()
    add(dirImg).expand().align(Align.left)
    row()
    add(fpsLbl).colspan(2).align(Align.center)
    setVisible(false)
  }

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
          scaleTo(Const.DownScale, Const.DownScale, Const.TransitTime / 2),
          scaleTo(Const.UpScale, Const.UpScale, Const.TransitTime / 2)),
        sequence(
          Actions.color(Color.LIGHT_GRAY, Const.TransitTime / 2),
          Actions.color(Color.WHITE, Const.TransitTime / 2)
        )
      )
    )
  }

  def setFpsText(s: String): Unit = fpsLbl.setText(s)

  override def hide(): Unit = {
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor()
      )
    )
  }

  override def show(): Unit = {
    addAction(
      sequence(
        alpha(0f),
        Actions.visible(true),
        fadeIn(.5f)
      )
    )
    pauseImg.setTouchable(Touchable.enabled)
  }
}
