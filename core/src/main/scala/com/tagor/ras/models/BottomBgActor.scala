package com.tagor.ras.models

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tagor.ras.utils.{ResMgr, Const}

/**
  * Created by rolangom on 2/13/16.
  */
class BottomBgActor extends Actor {

  private var sprite: Sprite = _

  def init(): BottomBgActor = {
    sprite = new Sprite(ResMgr.getRegion(Const.BGS_PATH, "white_square"))
    sprite.setBounds(0,0,Const.Width, Const.Height)
    this
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.disableBlending()
    sprite.draw(batch)
    batch.enableBlending()
  }

  override def setColor(color: Color): Unit = {
    super.setColor(color)
    sprite.setColor(color)
  }

  override def setColor(r: Float, g: Float, b: Float, a: Float): Unit = {
    super.setColor(r, g, b, a)
    sprite.setColor(r, g, b, a)
  }
}
