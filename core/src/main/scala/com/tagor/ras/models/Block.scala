package com.tagor.ras.models

import com.badlogic.gdx.graphics.Texture.{TextureFilter, TextureWrap}
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite, TextureRegion}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.{Align, NumberUtils}
import com.badlogic.gdx.utils.Pool.Poolable
import com.tagor.ras.utils
import com.tagor.ras.utils.Const.PPM
import com.tagor.ras.utils._

/**
 * Created by rolangom on 7/8/15.
 */

class Block(pbody: Body, val btype: BlockType)
  extends B2dActor(pbody) with Poolable {

  var isLast = false
  private var isLanded = false
  private val region = new TextureRegion()
//  private val sprite = new Sprite()
//  private val jointLeftSprite = new Sprite()
//  private val jointRightSprite = new Sprite()
  private def isDimenUp = btype.isDimenUp
  private var currentAct: () => Unit = () => ()

  def init(): Unit = {
    val bpos = body.getPosition
    setBounds(
      bpos.x * PPM - btype.halfw,
      bpos.y * PPM - btype.halfh,
      btype.width,
      btype.height)

    setOrigin(Align.center)

    setVisible(false)
//    setDebug(true)

    val scale = btype.scale
    setScale(scale)
  }
  init()

  def pause(): Unit = {
    body.setActive(false)
    remove()
  }

  def resumeInited(): Unit = {
//    if(isTextureDisposed)
//      initSpriteTexture()
  }

  def resumeActivated(): Unit = {
    resumeInited()
    activate()
  }

  private def initSpriteTexture(): Unit = {
    region.setRegion(ResMgr.getThemeTextureRegion(BlockConst.BLOCK_INDEX))
    region.setRegion(region.getRegionX, region.getRegionY, getWidth.toInt, getHeight.toInt)

//    val blockTexture = ResMgr.getThemeTexture(BlockConst.BLOCK_INDEX)
//    sprite.setTexture(blockTexture)
//    blockTexture.setWrap(
//      TextureWrap.Repeat,
//      TextureWrap.ClampToEdge)
//    blockTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
//    sprite.setRegion(Int.box(0), 0, blockTexture.getWidth, blockTexture.getHeight)

//    val chainTexture = ResMgr.getThemeTexture(BlockConst.JOINT_INDEX)
//    jointLeftSprite.setTexture(chainTexture)
//    jointRightSprite.setTexture(chainTexture)
//    jointLeftSprite.setRegion(Int.box(0), 0, chainTexture.getWidth, chainTexture.getHeight)
//    jointRightSprite.setRegion(Int.box(0), 0, chainTexture.getWidth, chainTexture.getHeight)
//
//    chainTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
//    chainTexture.setWrap(TextureWrap.ClampToEdge, TextureWrap.Repeat)

//    sprite.setBounds(getX, getY, getWidth, getHeight)
//    sprite.setU2(getWidth / sprite.getTexture.getWidth)
//    sprite.setOriginCenter()

//    jointLeftSprite.setBounds(getX(), 0, jointLeftSprite.getTexture.getWidth, Const.Height - getY())
//    jointLeftSprite.setV2(getHeight / jointLeftSprite.getTexture.getHeight)
//    jointLeftSprite.setOrigin(0, 0)
//
//    jointRightSprite.setBounds(getRight, 0, jointRightSprite.getTexture.getWidth, Const.Height - getY())
//    jointRightSprite.setV2(getHeight / jointRightSprite.getTexture.getHeight)
//    jointRightSprite.setOrigin(0, 0)

//    sprite.setScale(scale)

//    jointLeftSprite.setScale(scale)
//    jointRightSprite.setScale(scale)

    setColor(ThemeMgr.getBlockColor(btype.dimen))
  }

  def init(iti: ItemToInst): Block =
    init(iti.x, iti.y, iti.angle, iti.isLast)

//  private def isTextureDisposed: Boolean =
//    sprite.getTexture == null || sprite.getTexture.getTextureObjectHandle == 0

  def init(x: Float,
           y: Float,
           angle: Float,
           isLast: Boolean = false): Block = {
    setPosition(x - getOriginX, y - getOriginY)
    setRotation(angle)
    initSpriteTexture()
//    if(isTextureDisposed)
//      initSpriteTexture()
//    sprite.setPosition(getX, getY)
//    sprite.setRotation(angle)
//    sprite.setColor(getColor)
    this.isLast = isLast
    this
  }

  def activate(): Block = {
//    println(s"block to activate $btype")
    body.setActive(true)
    body.setTransform(
      (getX + getOriginX) * Const.MPP,
      (getY + getOriginY) * Const.MPP,
      MathUtils.degreesToRadians * getRotation)

//    configJointPos()
    RxMgr.onActorAdded.onNext(this)

    setVisible(true)
    utils.post(() => postActivate())
    this
  }

  private def postActivate(): Unit = {
    if (isDimenUp)
      toFront()
    else {
      toBack()
      body.setLinearVelocity(Const.GroundLinearVelocity, 0f)
      currentAct = dimenDownAct
    }
  }

  private def setVisibleSmoothly(): Unit = {
    addAction(Actions.alpha(0f))
    setVisible(true)
    addAction(Actions.fadeIn(.5f))
  }

  def asLast(): Unit = {
    isLast = true
  }

  def notAsLast(): Unit = {
    isLast = false
  }

  override def reset(): Unit = {
    body.setTransform(0f, 0f, 0f)
    body.setLinearVelocity(0f, 0f)
    body.setActive(false)

    setPosition(0f, 0f)
    setRotation(0f)
    setVisible(false)
    remove() // Remove Actor from stage
    isLast = false
    isLanded = false
    currentAct = () => ()
  }

//  private def configJointPos(): Unit = {
//    val cornerX = MathUtils.cosDeg(getRotation) * (getWidth * getScaleX / 2)
//    val cornerY = MathUtils.sinDeg(getRotation) * (getWidth * getScaleY / 2)
//
//    val bodyPos = body.getPosition
//
//    val leftPosX = bodyPos.x * PPM - cornerX
//    val rightPosX = bodyPos.x * PPM + cornerX - jointRightSprite.getTexture.getWidth
//
//    val leftPosY = bodyPos.y * PPM  - cornerY
//    val rightPosY = bodyPos.y * PPM + cornerY
//
//    jointLeftSprite.setBounds(leftPosX, leftPosY, jointLeftSprite.getTexture.getWidth, Const.Height - leftPosY)
//    jointRightSprite.setBounds(rightPosX, rightPosY, jointRightSprite.getTexture.getWidth, Const.Height - rightPosY)
//
//    // to ensure the height is reaching the top of the screen
//    if (getScaleY < 1) {
//      val amountReq = 1.1f - getScaleY
//      jointLeftSprite.setSize(jointLeftSprite.getWidth, jointLeftSprite.getHeight + (jointLeftSprite.getHeight * amountReq))
//      jointRightSprite.setSize(jointRightSprite.getWidth, jointRightSprite.getHeight + (jointRightSprite.getHeight * amountReq))
//    }
//    jointLeftSprite.setV2(jointLeftSprite.getHeight / jointLeftSprite.getTexture.getHeight)
//    jointRightSprite.setV2(jointRightSprite.getHeight / jointRightSprite.getTexture.getHeight)
//  }

  def setAsLanded(): Boolean = {
    if (!isLanded) {
      isLanded = true
      return true
    }
    false
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    currentAct()
  }

  def dimenDownAct(): Unit = {
    val pos = body.getPosition
    val prevX = getX
    setX(pos.x * PPM - getOriginX)
//    sprite.setX(getX)
    val deltaX = getX - prevX
    //      jointLeftSprite.translateX(deltaX)
    //      jointRightSprite.translateX(deltaX)
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.disableBlending()
//    sprite.draw(batch)
    batch.setColor(getColor)
    batch.draw(region, getX, getY, getOriginX, getOriginY, getWidth, getHeight, getScaleX, getScaleY, getRotation)
    batch.setColor(1, 1, 1, 1)
    batch.enableBlending()

//    jointLeftSprite.draw(batch)
//    jointRightSprite.draw(batch)
  }

  def leftX: Float = optX(_ - _)
  def rightX: Float = optX(_ + _)
  def leftY: Float = optY(_ - _)
  def rightY: Float = optY(_ + _)

  def centerX = wAng(btype.halfw, getRotation)

  private def optX(f: (Float, Float) => Float): Float = {
    val cx = body.getPosition.x * PPM
    val hw = (getWidth * getScaleX) *.5f * MathUtils.cosDeg(getRotation)
    f(cx, hw)
  }

  private def optY(f: (Float, Float) => Float): Float = {
    val cy = body.getPosition.y * PPM
    val hh = (getWidth * getScaleX) *.5f * MathUtils.sinDeg(getRotation)
    f(cy, hh)
  }

  override def finalize(): Unit = {
    super.finalize()
    WorldFactory.world.destroyBody(body)
  }
}