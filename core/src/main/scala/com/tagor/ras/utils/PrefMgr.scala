package com.tagor.ras.utils

import com.badlogic.gdx.{Gdx, Preferences}

/**
  * Created by rolangom on 4/16/16.
  */
object PrefMgr {

  private val _prefs: Preferences = Gdx.app.getPreferences("RumperGamePrefs")

  def prefs: Preferences = _prefs

  def currPlayer: Int = _prefs.getInteger("currPlayer", 0)

  def currPlayer_=(n: Int) = {
    _prefs.putInteger("currPlayer", n)
    _prefs.flush()
  }

  def playsCount: Int = _prefs.getInteger("plays", 0)
  def playsCount_= (n: Int) = {
    _prefs.putInteger("plays", n)
    _prefs.flush()
  }

  def score: Int = _prefs.getInteger("score", 0)
  def score_=(n: Int) = {
    _prefs.putInteger("score", n)
    _prefs.flush()
  }

  def coins: Int = _prefs.getInteger("coins", 0)
  def coins_=(n: Int) = {
    _prefs.putInteger("coins", n)
    _prefs.flush()
  }
}
