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
  * Created by rolangom on 4/9/16.
  */
class InstrTable(clickListener: ClickListener) extends Table with Showable {

  def init(): Unit = {
    reset()

    setFillParent(true)
    //    setDebug(true)
    align(Align.center)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 32
    val font = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.DarkBlue))

    val leftLbl = new Label("Tap left\nto switch", labelStyle)
    val rightLbl = new Label("Tap right\nto jump,\nhold to float", labelStyle)
    val tapContLbl = new Label("Tap to continue", labelStyle)
    leftLbl.setAlignment(Align.center)
    rightLbl.setAlignment(Align.center)
    tapContLbl.setAlignment(Align.center)

    leftLbl.setUserObject(Const.ExitFromHelpStr)
    rightLbl.setUserObject(Const.ExitFromHelpStr)
    tapContLbl.setUserObject(Const.ExitFromHelpStr)

    leftLbl.setTouchable(Touchable.enabled)
    rightLbl.setTouchable(Touchable.enabled)
    tapContLbl.setTouchable(Touchable.enabled)

    leftLbl.addListener(clickListener)
    rightLbl.addListener(clickListener)
    tapContLbl.addListener(clickListener)

    val switchImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "switch_img"))
    val jumpImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "jump_img"))

    add(switchImg).center().pad(24)
    add(jumpImg).center().pad(24)
    row()

    add(leftLbl).center().padRight(24)
    add(rightLbl).center().padLeft(24)
    row()

    add(tapContLbl).center().pad(24).colspan(2)

    setVisible(false)
  }

  override def show(): Unit = {
    clearActions()
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f)/*,
        run(runnable(
          () => enableTouchable(true, soundBtnImg, lBoardBtnImg, playBtnImg)
        ))*/
      )
    )
  }

  override def hide(): Unit = {
    clearActions()
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor()
      )
    )
//    enableTouchable(false, soundBtnImg, lBoardBtnImg, playBtnImg)
  }
}
