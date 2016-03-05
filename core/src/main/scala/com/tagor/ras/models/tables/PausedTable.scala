package com.tagor.ras.models.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Image, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.Showable
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/28/16.
  */
class PausedTable(clickListener: ClickListener) extends Table with Showable {

  private var resumeImg: Image = _
  private var goHomeImg: Image = _

  def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AldotheApache.ttf"))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    parameter.shadowOffsetX = 2
    parameter.shadowOffsetY = 2
    var font = generator.generateFont(parameter)
    var labelStyle = new Label.LabelStyle(font, Color.YELLOW)

    val pauseLbl = new Label("Paused", labelStyle)

    resumeImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    resumeImg.setOrigin(Align.center)
    resumeImg.setUserObject(Const.ResumeStr)
    resumeImg.addListener(clickListener)
    resumeImg.setTouchable(Touchable.enabled)

    goHomeImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))
    goHomeImg.setOrigin(Align.center)
    goHomeImg.setUserObject(Const.GoHomeStr)
    goHomeImg.addListener(clickListener)
    goHomeImg.setTouchable(Touchable.enabled)

    add(pauseLbl).colspan(2).spaceBottom(32).spaceTop(64)
    row()
    add(goHomeImg).align(Align.center)
    add(resumeImg).align(Align.center)
    setVisible(false)
  }

  override def show(): Unit = {
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f),
        run(runnable(
          () => enableTouchable(true, resumeImg, resumeImg)
        ))
      )
    )
  }

  override def hide(): Unit = {
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor()
      )
    )
  }
}
