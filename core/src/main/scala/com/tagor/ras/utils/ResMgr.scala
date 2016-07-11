package com.tagor.ras.utils

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
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
object ResMgr extends Disposable {

  private val disposables = mutable.Map[String, Disposable]()

  def getAtlas(atlasKey: String): TextureAtlas = {
    if (!disposables.contains(atlasKey)) disposables.put(atlasKey, new TextureAtlas(atlasKey))
    disposables(atlasKey).asInstanceOf[TextureAtlas]
  }

  def getRegion(atlasKey: String, regionKey: String): TextureRegion = {
    getAtlas(atlasKey).findRegion(regionKey)
  }

  def getSprite(atlasKey: String, spriteKey: String): Sprite = {
    getAtlas(atlasKey).createSprite(spriteKey)
  }

  def getRandomCloudRegion: TextureRegion = {
    val randomIndx = MathUtils.random(2)
    getRegion(Const.BGS_PATH, Const.CLOUD_IMG.format(randomIndx))
  }

  def getTexture(textureKey: String): Texture = {
    if (!disposables.contains(textureKey)) disposables.put(textureKey, new Texture(Gdx.files.internal(textureKey),
      Format.RGBA4444, true))
    disposables(textureKey).asInstanceOf[Texture]
  }

  def getSound(key: String): Sound = {
    if (!disposables.contains(key))
      disposables.put(key, Gdx.audio.newSound(Gdx.files.internal(key)))
    disposables(key).asInstanceOf[Sound]
  }

  def remove(key: String): Unit = {
    if (disposables.contains(key)) {
      disposables(key).dispose()
      disposables.remove(key)
    }
  }

  def remove(keys: String*): Unit = {
    keys.foreach(remove)
  }

  def getThemeTexture(key: Int): Texture =
    getTexture(getThemeTextureStr(key))

  def getThemeTextureRegion(key: Int): TextureRegion =
    getRegion(Const.BGS_PATH, getThemeTextureRegionStr(key))

  def getThemeTextureStr(key: Int): String =
    BlockConst.THEMES_IMGS(ThemeMgr.currentTheme)(key)

  def getThemeTextureRegionStr(key: Int): String =
    BlockConst.THEMES_IMGS(ThemeMgr.currentTheme)(key)

  def shareBtnStr = if (Gdx.app.getType == ApplicationType.iOS) "share_ios_btn" else "share_btn"

  def removeThemeTextureStr(key: Int): Unit = {
    remove(getThemeTextureStr(key))
  }

  @Deprecated
  def removeThemeTextureRegionStr(key: Int): Unit = {
    remove(getThemeTextureRegionStr(key))
  }

  override def dispose(): Unit = {
    disposables.foreach {
      case (k, v) => {
        disposables.remove(k)
        v.dispose()
      }
    }
  }

}
