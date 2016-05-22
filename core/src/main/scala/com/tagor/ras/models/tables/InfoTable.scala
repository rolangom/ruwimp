package com.tagor.ras.models.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.Showable
import com.tagor.ras.utils._

/**
  * Created by rolangom on 4/30/16.
  */
class InfoTable(clickListener: ClickListener) extends Table with Showable {

  def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    var font = generator.generateFont(parameter)
    var labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.DarkBlue))

    val titleLbl = new Label("About", labelStyle)

    parameter.size = 32
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.DarkBlue))

    val lbl1 = new Label("Designed and Developed by", labelStyle)
    val tapContLbl = new Label("Tap to continue", labelStyle)
    val creditsLbl = new Label("Thanks to Libgdx and Kenney's assets", labelStyle)
    labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.Red))
    val myTwitterlbl = new Label("@rolangom", labelStyle)

    titleLbl.setAlignment(Align.center)
    lbl1.setAlignment(Align.center)
    myTwitterlbl.setAlignment(Align.center)
    tapContLbl.setAlignment(Align.center)

    myTwitterlbl.setUserObject(Const.MyTwitterStr)
    tapContLbl.setUserObject(Const.ExitFromInfoStr)

    myTwitterlbl.setTouchable(Touchable.enabled)
    tapContLbl.setTouchable(Touchable.enabled)

    myTwitterlbl.addListener(clickListener)
    tapContLbl.addListener(clickListener)

    add(titleLbl).center().pad(12).spaceBottom(24)
    row()
    add(lbl1).center()
    row()
    add(myTwitterlbl).center().pad(12)
    row()
    add(creditsLbl).center().pad(12).spaceBottom(24)
    row()
    add(tapContLbl).center().pad(24)

    setVisible(false)
  }
  init()

  override def show(): Unit = {
    println("InfoTable to show")
    clearActions()
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f)
      )
    )
  }

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
  }
}
