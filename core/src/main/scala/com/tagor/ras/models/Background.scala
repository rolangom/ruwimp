package com.tagor.ras.models

import com.badlogic.gdx.graphics.{Camera, Color}
import com.badlogic.gdx.graphics.Texture.{TextureFilter, TextureWrap}
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite, TextureRegion}
import com.badlogic.gdx.math.{MathUtils, Rectangle}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tagor.ras.utils._

/**
  * Created by rolangom on 1/9/16.
  */
class Background(camera: Camera) extends Actor {

//  private val bg2Sprite = new Sprite
//
//  private val cloud1Sprite = new Sprite
//  private val cloud2Sprite = new Sprite

  private val topBgRegion = new TextureRegion()
  private val cloudRegion = new TextureRegion()

  private var topBg1x, topBg2x, cloud1x, cloud2x = 0f
  private var frontBgColor, cloudColor = Color.WHITE.toFloatBits

  private var lastCamX = 0f
  private val camHalfWidth: Float = camera.viewportWidth / 2

  private val topBody = WorldFactory.createTopBody(0f, camera.viewportHeight, camera.viewportWidth * 2)

//  RxMgr.newTheme
//    .subscribe(t => invalidate(t))

  def init(): Unit = {
    initBgSprites()

    invalidate(Const.ThemeColor)
    lastCamX = camera.position.x

//    configClouds()
    toBack()
  }

  private def initBgSprites(): Unit = {
//    val bg2Texture = ResMgr.getThemeTexture(BlockConst.BG1_INDEX)
//    bg2Texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
//    bg2Texture.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge)
//    val bg2Rect = new Rectangle(0, 0, camera.viewportWidth + bg2Texture.getWidth, bg2Texture.getHeight) // y = 46
//
//    bg2Sprite.setTexture(bg2Texture)
//    bg2Sprite.setRegion(Int.box(0), 0, bg2Texture.getWidth, bg2Texture.getHeight)
//    bg2Sprite.setBounds(bg2Rect.x, bg2Rect.y, bg2Rect.width, bg2Rect.height)
//    bg2Sprite.setU2(bg2Rect.width / bg2Texture.getWidth)
//    bg2Sprite.setOrigin(0, 0)

    topBgRegion.setRegion(ResMgr.getThemeTextureRegion(BlockConst.BG1_INDEX))
    cloudRegion.setRegion(ResMgr.getThemeTextureRegion(BlockConst.CLOUD_INDEX))
    topBgRegion.flip(MathUtils.randomBoolean(), false)
    cloudRegion.flip(MathUtils.randomBoolean(), false)
  }

  private def initColors(): Unit = {
    frontBgColor = ThemeMgr.getBgColor(BlockConst.BG0_COLOR_INDEX).toFloatBits
    cloudColor = ThemeMgr.getBgColor(BlockConst.BGCLOUD_COLOR_INDEX).toFloatBits
  }

  def pause(): Unit = {
//    ResMgr.removeThemeTextureStr(BlockConst.BG1_INDEX)
  }

  def resume(): Unit = {
    initBgSprites()
    initColors()
//    setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX), bg2Sprite)
//    setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX), cloud1Sprite, cloud2Sprite)
  }

//  private def configClouds(): Unit = {
//    cloud1Sprite.setRegion(ResMgr.getRandomCloudRegion)
//    cloud2Sprite.setRegion(ResMgr.getRandomCloudRegion)
//
//    cloud1Sprite.setSize(
//      cloud1Sprite.getRegionWidth,
//      cloud1Sprite.getRegionHeight)
//    cloud1Sprite.setPosition(
//      MathUtils.random(0f, camera.viewportWidth * .5f - cloud1Sprite.getWidth),
//      MathUtils.random(camera.viewportHeight * .4f, camera.viewportHeight - cloud1Sprite.getHeight))
//
//    cloud2Sprite.setSize(
//      cloud2Sprite.getRegionWidth,
//      cloud2Sprite.getRegionHeight)
//    cloud2Sprite.setPosition(
//      MathUtils.random(camera.viewportWidth * .5f, camera.viewportWidth - cloud2Sprite.getWidth),
////      cloud1Sprite.getX + cloud1Sprite.getWidth + MathUtils.random(0, Const.Width / 2),
//      MathUtils.random(camera.viewportHeight * .45f, camera.viewportHeight - cloud2Sprite.getHeight))
//
//    cloud1Sprite.setFlip(MathUtils.randomBoolean(), false)
//    cloud2Sprite.setFlip(MathUtils.randomBoolean(), false)
//  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)

    batch.setColor(cloudColor)
    batch.draw(cloudRegion, cloud1x, 105, cloudRegion.getRegionWidth, cloudRegion.getRegionHeight)
    batch.draw(cloudRegion, cloud2x, 105, cloudRegion.getRegionWidth, cloudRegion.getRegionHeight)
    batch.setColor(frontBgColor)
    batch.draw(topBgRegion, topBg1x, 0, topBgRegion.getRegionWidth, topBgRegion.getRegionHeight)
    batch.draw(topBgRegion, topBg2x, 0, topBgRegion.getRegionWidth, topBgRegion.getRegionHeight)
    batch.setColor(1, 1, 1, 1) // White -> default 0xFFFFFFFF

//    cloud1Sprite.draw(batch)
//    cloud2Sprite.draw(batch)
//
//    bg2Sprite.draw(batch)
  }

  override def act(delta: Float): Unit = {
    super.act(delta)

    val camX = camera.position.x
    val deltaCamX = camX - lastCamX
//    val cloudPosX = deltaCamX * .95f

    topBg1x += deltaCamX * .85f
    topBg2x += deltaCamX * .85f
    cloud1x += deltaCamX * .95f
    cloud2x += deltaCamX * .95f

//    bg2Sprite.translateX(deltaCamX * .85f)
//    cloud1Sprite.translateX(cloudPosX)
//    cloud2Sprite.translateX(cloudPosX + .02f)

    checkBounds()

    lastCamX = camX
  }

  private def checkBounds(): Unit = {
    val camX = camera.position.x

    if (topBg1x + topBgRegion.getRegionWidth < camX - camHalfWidth)
      topBg1x = topBg2x + topBgRegion.getRegionWidth
    else if (topBg2x + topBgRegion.getRegionWidth < camX - camHalfWidth)
      topBg2x = topBg1x + topBgRegion.getRegionWidth

    if (cloud1x + cloudRegion.getRegionWidth < camX - camHalfWidth)
      cloud1x = cloud2x + cloudRegion.getRegionWidth
    else if (topBg2x + cloudRegion.getRegionWidth < camX - camHalfWidth)
      cloud2x = cloud1x + cloudRegion.getRegionWidth

//    if (bg2Sprite.getX <= camX - (camHalfWidth + bg2Sprite.getTexture.getWidth))
//      bg2Sprite.setX(camX - camHalfWidth)
//
//    if (cloud1Sprite.getX + cloud1Sprite.getWidth <= camX - camHalfWidth)
//      cloud1Sprite.setX(camX + camHalfWidth)
//
//    if (cloud2Sprite.getX + cloud2Sprite.getWidth <= camX - camHalfWidth)
//      cloud2Sprite.setX(camX + camHalfWidth)

    if (topBody.getPosition.x * Const.PPM <= camX - camHalfWidth)
      topBody.setTransform((camX + camHalfWidth) * Const.MPP, (camera.viewportHeight + BlockConst.Size) * Const.MPP, 0)
  }

  def start(): Unit = {
//    bg2Sprite.setX(0)

    topBg1x = camera.position.x - camHalfWidth
    topBg2x = topBg1x + topBgRegion.getRegionWidth
    cloud1x = camera.position.x - camHalfWidth
    cloud2x = cloud1x + cloudRegion.getRegionWidth

    topBody.setTransform(
      (camera.position.x + camHalfWidth) * Const.MPP,
      (camera.viewportHeight + BlockConst.Size) * Const.MPP, 0)

    lastCamX = camera.position.x
//    configClouds()
    toBack()
  }

  def invalidate(themeItem: Int): Unit = {
    themeItem match {
      case Const.ThemeColor =>
//        setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX), bg2Sprite)
//        setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX), cloud1Sprite, cloud2Sprite)
        initColors()
      case Const.ThemeImg =>
//        ResMgr.removeThemeTextureStr(BlockConst.BG1_INDEX)
        initBgSprites()
      case _ => ()
    }
  }

  private def setSpriteColor(color: String,
                             sprites: Sprite*): Unit = {
    sprites.foreach(_.setColor(Color.valueOf(color)))
  }
}
