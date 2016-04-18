package com.tagor.ras.utils

import com.badlogic.gdx.Gdx

/**
  * Created by rolangom on 4/16/16.
  */
object PrefMgr {

  private val _prefs = Gdx.app.getPreferences("RumperGamePrefs")

  def prefs = _prefs
}
