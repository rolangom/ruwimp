package com.tagor.ras.utils

import com.badlogic.gdx.graphics.Color

/**
  * Created by rolangom on 1/9/16.
  */
object ThemeMgr {

  private var _currentTheme = 0
  private var _currentColorSet = 0
  private val MAX_COLORSET_PLAYS = 2
  private var currentColorSetPlays = 0
  private var currentColorThemePlays = 0

  def currentTheme = _currentTheme
  def currentColorSet = _currentColorSet

  RxMgr.onGameState
    .filter(_ == Const.GameStatePlay)
    .subscribe(r => next())

  private def next(): Unit = {
    currentColorSetPlays += 1
    if (currentColorSetPlays >= MAX_COLORSET_PLAYS) {

      currentColorThemePlays += 1
      if (currentColorThemePlays >= BlockConst.COLOR_THEME_SET.length) {
        currentColorThemePlays = 0
        nextThemeImg()
      }

      currentColorSetPlays = 0
      nextColorSet()
    }
  }

  private def nextThemeImg(): Unit = {
    dispose()
    _currentTheme = if (_currentTheme >= BlockConst.THEMES.length - 1) 0 else + 1
    RxMgr.newTheme.onNext(Const.ThemeImg)
  }

  private def nextColorSet(): Unit = {
    _currentColorSet = if (_currentColorSet >= BlockConst.COLOR_THEME_SET.length - 1) 0 else + 1
    RxMgr.newTheme.onNext(Const.ThemeColor)
  }

  def dispose() {
    ResMgr.remove(BlockConst.THEMES_IMGS(_currentTheme)(BlockConst.BLOCK_INDEX))
    //res.removeTexture(BlockConst.THEMES_IMGS(currentTheme)(BlockConst.JOINT_INDEX))
    ResMgr.remove(BlockConst.THEMES_IMGS(_currentTheme)(BlockConst.BG1_INDEX))
    ResMgr.remove(BlockConst.THEMES_IMGS(_currentTheme)(BlockConst.BG2_INDEX))
  }

  def getBgColorStr(index: Int): String =
    BlockConst.COLOR_THEME_SET(currentTheme)(currentColorSet)(index)

  def getBgColorStr: String =
    getBgColorStr(BlockConst.BG2_COLOR_INDEX)

  def getBlockColor(dimen: Int): Color =
    Color.valueOf(
      BlockConst.COLOR_THEME_SET
        (currentTheme)(currentColorSet)(BlockConst.BLOCK_UP_INDEX + dimen))


}
