package com.tagor.ras.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable

import scala.collection.mutable

/**
  * Created by rolangom on 12/27/15.
  */
object ResMgr {

  private val textures = mutable.Map[String, Disposable]()

  def getAtlas(atlasKey: String): TextureAtlas = {
    if (!textures.contains(atlasKey)) textures.put(atlasKey, new TextureAtlas(atlasKey))
    textures(atlasKey).asInstanceOf[TextureAtlas]
  }

  def getRegion(atlasKey: String, regionKey: String): TextureRegion = {
    getAtlas(atlasKey).findRegion(regionKey)
  }

  def getSprite(atlasKey: String, spriteKey: String): Sprite = {
    getAtlas(atlasKey).createSprite(spriteKey)
  }

  def getRandomCloudRegion(): TextureRegion = {
    val randomIndx = MathUtils.random(2)
    getRegion(Const.BGS_PATH, Const.CLOUD_IMG.format(randomIndx))
  }

  def getRandomCloudSprite(): Sprite = {
    val randomIndx = MathUtils.random(2)
    getSprite(Const.BGS_PATH, Const.CLOUD_IMG.format(randomIndx))
  }

  def getCityRegion(index: Int): TextureRegion = {
    getRegion(Const.BGS_PATH, Const.CITY_BGS.format(index))
  }

  def getCityTexture(index: Int): Texture = {
    getTexture(Const.CITY_BGS.format(index))
  }

  def getTreesTexture(index: Int): Texture = {
    getTexture(Const.TREES_BGS.format(index))
  }

  def getTexture(textureKey: String): Texture = {
    if (!textures.contains(textureKey)) textures.put(textureKey, new Texture(Gdx.files.internal(textureKey),
      Format.RGBA4444, true))
    textures(textureKey).asInstanceOf[Texture]
  }

  def removeTexture(textureKey: String) {
    if (textures.contains(textureKey)) {
      textures(textureKey).dispose()
      textures.remove(textureKey)
    }
  }

//  def getThemeTexture(key: Int): Texture = {
//    getTexture(BlockConst.THEMES_IMGS(ThemeManager.currentTheme)(key))
//  }

  def dispose() {
    textures.foreach{
      case (k, v) => v.dispose()
    }
  }

}
