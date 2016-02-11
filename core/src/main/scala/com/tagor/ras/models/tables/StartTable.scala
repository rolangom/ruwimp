package com.tagor.ras.models.tables

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.utils.{Const, ResMgr}

/**
  * Created by rolangom on 2/2/16.
  */
class StartTable(clickListener: ClickListener) extends Table {

  def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val logoImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "logo"))
    logoImg.setOrigin(Align.center)

    val playBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    playBtnImg.setUserObject(Const.PlayStr)
    playBtnImg.addListener(clickListener)
    val lBoardBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))

    add(logoImg).colspan(2).spaceBottom(32).spaceTop(64)
    row()
    add(lBoardBtnImg).align(Align.center)
    add(playBtnImg).align(Align.center)
    setVisible(false)
  }

  def show(): Unit = {
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f)
      )
    )
  }

  def hide(): Unit = {
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor()
      )
    )
  }
}
