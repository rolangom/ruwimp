package com.tagor.ras.models.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Table}
import com.badlogic.gdx.scenes.scene2d.utils.{TiledDrawable, ClickListener}
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.Showable
import com.tagor.ras.utils.{Const, ResMgr, ScoreMgr}
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/2/16.
  */
class DashboardTable(clickListener: ClickListener) extends Table with Showable {

  private var scoreDashbLbl: Label = _
  private var bestDashbLbl: Label = _
  private var newDashbLbl: Label = _

  private var shareBtnImg: Image = _
  private var playBtnImg: Image = _
  private var lBoardBtnImg: Image = _
  private var homeBtnImg: Image = _

  def init(): Unit = {
    reset()

    setFillParent(true)
//    setDebug(true)
    align(Align.center)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 32
    var font = generator.generateFont(parameter)
    var labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.DarkBlue))

    val scoreLbl = new Label("Score", labelStyle)
    val bestLbl = new Label("Best", labelStyle)

    parameter.size = 48
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.Red))

    scoreDashbLbl = new Label(ScoreMgr.score.toString, labelStyle)
    bestDashbLbl = new Label(ScoreMgr.bestScore.toString, labelStyle)

    parameter.size = 64
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.DarkBlue))
    val gameOverLbl = new Label("Game Over", labelStyle)

    parameter.size = 32
    parameter.borderColor = Color.valueOf(BlockConst.DarkBlue)
    parameter.borderWidth = 2
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.WHITE)
    newDashbLbl = new Label("New", labelStyle)
    generator.dispose()

    shareBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, ResMgr.shareBtnStr))
    shareBtnImg.setOrigin(Align.center)
    shareBtnImg.setUserObject(Const.ShareScoreStr)
    shareBtnImg.addListener(clickListener)
    shareBtnImg.setTouchable(Touchable.enabled)

    playBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    playBtnImg.setOrigin(Align.center)
    playBtnImg.setUserObject(Const.PlayAgainStr)
    playBtnImg.addListener(clickListener)
    playBtnImg.setTouchable(Touchable.enabled)

    lBoardBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))
    lBoardBtnImg.setOrigin(Align.center)
    lBoardBtnImg.setUserObject(Const.SubmitLeaderBoardStr)
    lBoardBtnImg.addListener(clickListener)
    lBoardBtnImg.setTouchable(Touchable.enabled)

    homeBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "home_btn"))
    homeBtnImg.setOrigin(Align.center)
    homeBtnImg.setUserObject(Const.GoHomeStr)
    homeBtnImg.addListener(clickListener)
    homeBtnImg.setTouchable(Touchable.enabled)

    add(gameOverLbl).colspan(4).spaceBottom(24)
    row()
    add(scoreLbl).colspan(3).left()
    add(bestLbl).right()
    row()
    add(scoreDashbLbl).colspan(3).left()
    add(bestDashbLbl).right()
    row()
    add(newDashbLbl).colspan(4).right().spaceBottom(12)
    row()
    add(homeBtnImg).size(64)
    add(shareBtnImg).size(64)
    add(lBoardBtnImg).size(64)
    add(playBtnImg).size(64)
    setVisible(false)
  }
  init()

  override def show(): Unit = {
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(.5f),
        run(runnable(
          () => enableTouchable(true, lBoardBtnImg, playBtnImg, shareBtnImg, homeBtnImg)
        ))
      )
    )
    scoreDashbLbl.setText(ScoreMgr.score.toString)
    bestDashbLbl.setText(ScoreMgr.bestScore.toString)
    newDashbLbl.setVisible(ScoreMgr.isNewBestScore)
  }

  override def hide(): Unit = {
    hideAndFunc(() => ())
  }

  override def hideAndFunc(f: () => Unit): Unit = {
    hideAndFunc2(f, () => ())
  }

  def hideAndFunc2(f: () => Unit, f2: () => Unit): Unit = {
    clearActions()
    addAction(
      sequence(
        run(runnable(f)),
        delay(.15f),
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor(),
        run(runnable(f2))
      )
    )
    enableTouchable(false, lBoardBtnImg, playBtnImg, shareBtnImg, homeBtnImg)
  }
}
