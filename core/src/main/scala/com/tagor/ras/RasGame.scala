package com.tagor.ras;

import com.badlogic.gdx.Game

import com.tagor.ras.screens.GameScreen


class RasGame extends Game {

  override def create() =
    setScreen(new GameScreen)
}
