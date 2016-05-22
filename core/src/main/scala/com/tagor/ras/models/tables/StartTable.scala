package com.tagor.ras.models.tables

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Table}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, TextureRegionDrawable}
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
  private var helpImg: Image = _
  private var rateBtnImg: Image = _
  private var shareBtnImg: Image = _
  private var soundBtnImg: Image = _
//  private var noAdsBtnImg: Image = _
  private var twitterImg: Image = _
  private var infoImg: Image = _

  def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val logoImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "logo"))
    logoImg.setOrigin(Align.center)

//    noAdsBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "no_ads_btn"))
//    noAdsBtnImg.setOrigin(Align.center)
//    noAdsBtnImg.setUserObject(Const.NoAdsStr)
//    noAdsBtnImg.addListener(clickListener)
//    noAdsBtnImg.setTouchable(Touchable.enabled)

    helpImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "help_btn"))
    helpImg.setOrigin(Align.center)
    helpImg.setUserObject(Const.HelpStr)
    helpImg.addListener(clickListener)
    helpImg.setTouchable(Touchable.enabled)

    soundBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, SoundMgr.soundBtnStr))
    soundBtnImg.setOrigin(Align.center)
    soundBtnImg.setUserObject(Const.SoundStr)
    soundBtnImg.addListener(clickListener)
    soundBtnImg.setTouchable(Touchable.enabled)

    shareBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, ResMgr.shareBtnStr))
    shareBtnImg.setOrigin(Align.center)
    shareBtnImg.setUserObject(Const.ShareStr)
    shareBtnImg.addListener(clickListener)
    shareBtnImg.setTouchable(Touchable.enabled)

    rateBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "rate_btn"))
    rateBtnImg.setOrigin(Align.center)
    rateBtnImg.setUserObject(Const.RateStr)
    rateBtnImg.addListener(clickListener)
    rateBtnImg.setTouchable(Touchable.enabled)

    playBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    playBtnImg.setOrigin(Align.center)
    playBtnImg.setUserObject(Const.HelpToPlayStr)
    playBtnImg.addListener(clickListener)
    playBtnImg.setTouchable(Touchable.enabled)

    lBoardBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))
    lBoardBtnImg.setOrigin(Align.center)
    lBoardBtnImg.setUserObject(Const.LeaderBoardStr)
    lBoardBtnImg.addListener(clickListener)
    lBoardBtnImg.setTouchable(Touchable.enabled)

    twitterImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "twitter_btn"))
    twitterImg.setOrigin(Align.center)
    twitterImg.setUserObject(Const.TwitterStr)
    twitterImg.addListener(clickListener)
    twitterImg.setTouchable(Touchable.enabled)

    infoImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "info_btn"))
    infoImg.setOrigin(Align.center)
    infoImg.setUserObject(Const.InfoStr)
    infoImg.addListener(clickListener)
    infoImg.setTouchable(Touchable.enabled)

    add(logoImg).colspan(6).spaceBottom(32).spaceTop(64)
    row()
    add(lBoardBtnImg).pad(12).center()
    add(shareBtnImg).pad(12).center()
    add(rateBtnImg).pad(12).center()
    add(playBtnImg).pad(12).center()
    row()
    add(soundBtnImg).pad(12).center()
    add(helpImg).pad(12).center()
//    add(noAdsBtnImg).pad(12).center()
    add(twitterImg).pad(12).center()
    add(infoImg).pad(12).center()
    setVisible(false)
  }
  init()

  def toggleSound(): Unit = {
    SoundMgr.toggle()
    val region = ResMgr.getRegion(Const.BGS_PATH, SoundMgr.soundBtnStr)
    soundBtnImg.setDrawable(new TextureRegionDrawable(region))
  }

  override def show(): Unit = {
    clearActions()
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f),
        run(runnable(
          () => enableTouchable(true, soundBtnImg, lBoardBtnImg, playBtnImg)
        ))
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
    enableTouchable(false, soundBtnImg, lBoardBtnImg, playBtnImg)
  }
}
