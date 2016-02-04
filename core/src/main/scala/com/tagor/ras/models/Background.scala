package com.tagor.ras.models

import com.badlogic.gdx.graphics.{Color, Camera}
import com.badlogic.gdx.graphics.Texture.{TextureWrap, TextureFilter}
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite}
import com.badlogic.gdx.math.{MathUtils, Rectangle}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tagor.ras.utils._

import scala.concurrent.duration.DurationInt
/**
  * Created by rolangom on 1/9/16.
  */
class Background(camera: Camera) extends Actor {

//  val bg1Sprite = new Sprite
  val bg2Sprite = new Sprite

  val cloud1Sprite = new Sprite
  val cloud2Sprite = new Sprite

  var lastCamX = 0f
  val bg1Speed = .80f
  val bg2Speed = .85f
  val cloudSpeed = .95f
  val camHalfWidth: Float = camera.viewportWidth / 2

//  val topBody = WorldFactory.createTopBody(0f, Const.Height, Const.Width * 2)

  RxMgr.onActorAdded.onNext(this)

  RxMgr.newTheme
    .subscribe(t => invalidate(t))

  def init(): Unit = {
    initBgSprites()

    invalidate(Const.ThemeColor)
    lastCamX = camera.position.x

    configClouds()
    toBack()
  }

  private def initBgSprites(): Unit = {
//    val bg1Texture = ResMgr.getThemeTexture(BlockConst.BG1_INDEX)
//    bg1Texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
//    bg1Texture.setWrap(TextureWrap.	Repeat, TextureWrap.ClampToEdge)
//    val bg1Rect = new Rectangle(0, 0, Const.Width + bg1Texture.getWidth, bg1Texture.getHeight)
//
//    bg1Sprite.setTexture(bg1Texture)
//    bg1Sprite.setRegion(Int.box(0), 0, bg1Texture.getWidth, bg1Texture.getHeight)
//    bg1Sprite.setBounds(bg1Rect.x, bg1Rect.y, bg1Rect.width, bg1Rect.height)
//    bg1Sprite.setU2(bg1Rect.width / bg1Texture.getWidth)
//    bg1Sprite.setOrigin(0, 0)

    val bg2Texture = ResMgr.getThemeTexture(BlockConst.BG2_INDEX)
    bg2Texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
    bg2Texture.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge)
    val bg2Rect = new Rectangle(0, -50, Const.Width + bg2Texture.getWidth, bg2Texture.getHeight) // y = 46

    bg2Sprite.setTexture(bg2Texture)
    bg2Sprite.setRegion(Int.box(0), 0, bg2Texture.getWidth, bg2Texture.getHeight)
    bg2Sprite.setBounds(bg2Rect.x, bg2Rect.y, bg2Rect.width, bg2Rect.height)
    bg2Sprite.setU2(bg2Rect.width / bg2Texture.getWidth)
    bg2Sprite.setOrigin(0, 0)
  }

  private def configClouds(): Unit = {
    cloud1Sprite.setRegion(ResMgr.getRandomCloudRegion())
    cloud2Sprite.setRegion(ResMgr.getRandomCloudRegion())

    cloud1Sprite.setSize(
      cloud1Sprite.getRegionWidth,
      cloud1Sprite.getRegionHeight)
    cloud1Sprite.setPosition(
      MathUtils.random(0, Const.Width * .25f),
      MathUtils.random(Const.Height * .45f, Const.Height - cloud1Sprite.getHeight))

    cloud2Sprite.setSize(
      cloud2Sprite.getRegionWidth,
      cloud2Sprite.getRegionHeight)
    cloud2Sprite.setPosition(
      cloud1Sprite.getX + cloud1Sprite.getWidth + MathUtils.random(0, Const.Width / 2),
      MathUtils.random(Const.Height * .45f, Const.Height - cloud2Sprite.getHeight))

    cloud1Sprite.setFlip(MathUtils.randomBoolean(), false)
    cloud2Sprite.setFlip(MathUtils.randomBoolean(), false)
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)

    cloud1Sprite.draw(batch, parentAlpha)
    cloud2Sprite.draw(batch, parentAlpha)

    bg2Sprite.draw(batch, parentAlpha)
//    bg1Sprite.draw(batch, parentAlpha)
  }

  override def act(delta: Float): Unit = {
    super.act(delta)

    val camX = camera.position.x
    val deltaCamX = camX - lastCamX
    val cloudPosX = deltaCamX * cloudSpeed
//    val bg1PosX = deltaCamX * bg1Speed

//    bg1Sprite.translateX(bg1PosX)
    bg2Sprite.translateX(deltaCamX * bg2Speed)
    cloud1Sprite.translateX(cloudPosX)
    cloud2Sprite.translateX(cloudPosX + .02f)

    checkBounds()

    lastCamX = camX
  }

  private def checkBounds(): Unit = {
    val camX = camera.position.x
//    if (bg1Sprite.getX <= camX - (camHalfWidth + bg1Sprite.getTexture.getWidth))
//      bg1Sprite.setX(camX - camHalfWidth)

    if (bg2Sprite.getX <= camX - (camHalfWidth + bg2Sprite.getTexture.getWidth))
      bg2Sprite.setX(camX - camHalfWidth)

    if (cloud1Sprite.getX + cloud1Sprite.getWidth <= camX - camHalfWidth)
      cloud1Sprite.setX(camX + camHalfWidth)

    if (cloud2Sprite.getX + cloud2Sprite.getWidth <= camX - camHalfWidth)
      cloud2Sprite.setX(camX + camHalfWidth)

//    if (topBody.getPosition.x * Const.PPM <= camX - camHalfWidth)
//      topBody.setTransform(camX + camHalfWidth / Const.PPM, Const.Height / Const.PPM, 0)
  }

  def start(): Unit = {
//    bg1Sprite.setX(0)
    bg2Sprite.setX(0)

//    topBody.setTransform(
//      camera.position.x + camHalfWidth / Const.PPM,
//      Const.Height/ Const.PPM, 0)

    lastCamX = camera.position.x
    configClouds()
  }

  private def invalidate(themeItem: Int): Unit = {
    themeItem match {
      case Const.ThemeColor =>
//        setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX), bg1Sprite)
        setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.BG1_COLOR_INDEX), bg2Sprite)
        setSpriteColor(ThemeMgr.getBgColorStr(BlockConst.CLOUDS_COLOR_INDEX), cloud1Sprite, cloud2Sprite)
      case Const.ThemeImg => initBgSprites()
      case _ => ()
    }
  }

  private def setSpriteColor(color: String,
                             sprites: Sprite*): Unit = {
    for (s <- sprites)
      s.setColor(Color.valueOf(color))
  }
}
