package com.tagor.ras.models.tables

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.Showable
import com.tagor.ras.utils.{Const, ResMgr}
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/2/16.
  */
class StartTable(clickListener: ClickListener) extends Table with Showable {

  private var playBtnImg: Image = _
  private var lBoardBtnImg: Image = _

  def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val logoImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "logo"))
    logoImg.setOrigin(Align.center)

    playBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    playBtnImg.setOrigin(Align.center)
    playBtnImg.setUserObject(Const.PlayStr)
    playBtnImg.addListener(clickListener)
    playBtnImg.setTouchable(Touchable.enabled)

    lBoardBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))
    lBoardBtnImg.setOrigin(Align.center)
    lBoardBtnImg.setUserObject(Const.LeaderBoardStr)
    lBoardBtnImg.addListener(clickListener)
    lBoardBtnImg.setTouchable(Touchable.enabled)

    add(logoImg).colspan(2).spaceBottom(32).spaceTop(64)
    row()
    add(lBoardBtnImg).align(Align.center)
    add(playBtnImg).align(Align.center)
    setVisible(false)
  }

  override def show(): Unit = {
    clearActions()
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f),
        run(runnable(
          () => enableTouchable(true, lBoardBtnImg, playBtnImg)
        ))
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
  }
}
