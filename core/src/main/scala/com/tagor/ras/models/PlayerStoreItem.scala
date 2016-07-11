package com.tagor.ras.models

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, GlyphLayout}
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.{Actor, Touchable}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.utils.{BlockConst, Const, ResMgr}

/**
  * Created by rolangom on 7/7/16.
  */
class PlayerStoreItem(val price: String, name: String, index: String, clickListener: ClickListener) extends Actor {

  val region = ResMgr.getRegion(Const.BGS_PATH, s"player_${index}_falling")
  var isActive = false

  var nameFont: BitmapFont = _
  var priceFont: BitmapFont = _

  private def init(): Unit = {
    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.color = Color.WHITE
    parameter.borderColor = Color.valueOf(BlockConst.DarkBlue)
    parameter.borderWidth = 2
    parameter.size = Const.ItemStoreFontNameSize
    nameFont = generator.generateFont(parameter)
    parameter.color = Color.YELLOW
    parameter.borderWidth = 0
    parameter.size = Const.ItemStoreFontPriceSize
    priceFont = generator.generateFont(parameter)
    generator.dispose()

    setSize(Const.ItemStoreSize, Const.ItemStoreSize)
    setOrigin(Align.center)

    addListener(clickListener)
    setUserObject(index)
    setTouchable(Touchable.enabled)
  }
  init()

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.draw(region, getX + getWidth * .5f - region.getRegionWidth * Const.LargerFactor * .5f,
      getY - priceFont.getLineHeight,
      region.getRegionWidth * Const.LargerFactor * .5f, region.getRegionHeight * Const.LargerFactor * .5f,
      region.getRegionWidth * Const.LargerFactor, region.getRegionHeight * Const.LargerFactor,
      getScaleX, getScaleY, getRotation)

    nameFont.draw(batch, name, getX, getY + getHeight - nameFont.getLineHeight, getWidth, Align.center, false)
    priceFont.draw(batch, price, getX, getY, getWidth, Align.center, false)
  }
}
