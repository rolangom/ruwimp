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
import com.tagor.ras.utils.{Const, ResMgr, ScoreMgr}
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/2/16.
  */
class DashboardTable(clickListener: ClickListener) extends Table {

  private var scoreDashbLbl: Label = _
  private var bestDashbLbl: Label = _
  private var newDashbLbl: Label = _

  private var playBtnImg: Image = _
  private var lBoardBtnImg: Image = _

  def init(): Unit = {
    reset()

    setFillParent(true)
    setDebug(true)
    align(Align.center)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AldotheApache.ttf"))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 32
    parameter.shadowOffsetX = 2
    parameter.shadowOffsetY = 2
    var font = generator.generateFont(parameter)
    var labelStyle = new Label.LabelStyle(font, Color.WHITE)

    val scoreLbl = new Label("Score", labelStyle)
    val bestLbl = new Label("Best", labelStyle)

    parameter.size = 48
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.WHITE)

    scoreDashbLbl = new Label(ScoreMgr.lastScore.toString, labelStyle)
    bestDashbLbl = new Label(ScoreMgr.bestScore.toString, labelStyle)

    parameter.size = 32
    font = generator.generateFont(parameter)
    labelStyle = new Label.LabelStyle(font, Color.YELLOW)
    newDashbLbl = new Label("Best", labelStyle)
    generator.dispose()

    val gameoverImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "game_over"))
    gameoverImg.setOrigin(Align.center)

    playBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "play_btn"))
    playBtnImg.setOrigin(Align.center)
    playBtnImg.setUserObject(Const.PlayAgainStr)
    playBtnImg.addListener(clickListener)
    playBtnImg.setTouchable(Touchable.enabled)

    lBoardBtnImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "leaderboard_btn"))
    lBoardBtnImg.setOrigin(Align.center)
    lBoardBtnImg.setUserObject(Const.LeaderBoard)
    lBoardBtnImg.addListener(clickListener)
    lBoardBtnImg.setTouchable(Touchable.enabled)

    setBackground(new TiledDrawable(ResMgr.getRegion(Const.BGS_PATH, "white_square"))
      .tint(Color.valueOf("000000E7")))

    add(gameoverImg).colspan(3).spaceBottom(32).spaceTop(32)
    row()
    add(scoreLbl).align(Align.left).colspan(2)
    add(bestLbl).align(Align.right)
    row()
    add(scoreDashbLbl).align(Align.left).colspan(2)
    add(bestDashbLbl).align(Align.right)
    row()
    add(newDashbLbl).align(Align.left).spaceBottom(32)
    row()
    add(playBtnImg).align(Align.left).colspan(2)
    add(lBoardBtnImg).align(Align.right)
    setVisible(false)
  }

  def show(): Unit = {
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(.5f),
        run(runnable(
          () => enableTouchable(true, playBtnImg, lBoardBtnImg)
        ))
      )
    )
    scoreDashbLbl.setText(ScoreMgr.lastScore.toString)
    bestDashbLbl.setText(ScoreMgr.bestScore.toString)
    newDashbLbl.setVisible(ScoreMgr.isNewBestScore)
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

  def hideAndFunc(f: () => Unit): Unit = {
    addAction(
      sequence(
        run(runnable(f)),
        delay(.15f),
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor()
      )
    )
  }
}
