package com.tagor.ras.models

import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{Sprite, Batch}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pool.Poolable
import com.tagor.ras.utils
import com.tagor.ras.utils.Const.PPM
import com.tagor.ras.utils._

/**
 * Created by rolangom on 7/8/15.
 */
class Block(pbody: Body, val btype: BlockType)
  extends B2dActor(pbody) with Poolable {

  var isLanded = false
  var isLast = false
  val sprite = new Sprite()
  def isDimenUp = btype.isDimenUp

  def init(): Unit = {
    val bpos = body.getPosition
    setBounds(
      bpos.x * PPM - btype.halfw,
      bpos.y * PPM - btype.halfh,
      btype.width,
      btype.height)
    initSpriteTexture()

    sprite.setBounds(getX, getY, getWidth, getHeight)
    sprite.setU2(getWidth / sprite.getTexture.getWidth)
    sprite.setOriginCenter()

    setOrigin(Align.center)
    setScale(btype.scale)

    sprite.setScale(btype.scale)

    setVisible(false)
    setDebug(true)
  }
  init()

  private def initSpriteTexture(): Unit = {
    sprite.setTexture(ResMgr.getThemeTexture(BlockConst.BLOCK_INDEX))
    sprite.getTexture.setWrap(
      TextureWrap.MirroredRepeat,
      TextureWrap.ClampToEdge)
    setColor(ThemeMgr.getBlockColor(btype.dimen))
  }

  def init(iti: ItemToInst): Block =
    init(iti.x, iti.y, iti.angle, iti.isLast)

  private def isTextureDisposed: Boolean = sprite.getTexture.getTextureObjectHandle == 0

  def init(x: Float,
           y: Float,
           angle: Float,
           isLast: Boolean = false): Block = {
    setPosition(x - getOriginX, y - getOriginY)
    setRotation(angle)
    if(isTextureDisposed)
      initSpriteTexture()
    sprite.setPosition(getX, getY)
    sprite.setRotation(angle)
    sprite.setColor(getColor)
    this.isLast = isLast
    this
  }

  def activate(): Block = {
    body.setActive(true)
    body.setTransform(
      (getX + getOriginX) / PPM,
      (getY + getOriginY) / PPM,
      MathUtils.degreesToRadians * getRotation)

    RxMgr.onActorAdded.onNext(this)

    setVisible(true)
    utils.post { () =>
      if (isDimenUp)
        toFront()
      else {
        toBack()
        body.setLinearVelocity(Const.GroundLinearVelocity, 0f)
      }
    }
    this
  }

  private def setVisibleSmoothly(): Unit = {
    addAction(Actions.alpha(0f))
    setVisible(true)
    addAction(Actions.fadeIn(1f))
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
  }

  def setAsLanded(): Boolean = {
    if (!isLanded) {
      isLanded = true
      return true
    }
    false
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    if (!isDimenUp) {
      val pos = body.getPosition
      setX(pos.x * PPM - getOriginX)
      sprite.setX(getX)
    }
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.disableBlending()
    sprite.draw(batch, parentAlpha)
    batch.enableBlending()
  }

  def minX: Float = optX(_ - _)
  def maxX: Float = optX(_ + _)
  def minY: Float = optY(_ - _)
  def maxY: Float = optY(_ + _)

  private def optX(f: (Float, Float) => Float): Float = {
    val cx = body.getPosition.x * PPM
    val hw = (getWidth * getScaleX) / 2 * MathUtils.cosDeg(getRotation)
    f(cx, hw)
  }

  private def optY(f: (Float, Float) => Float): Float = {
    val cy = body.getPosition.y * PPM
    val hh = (getHeight * getScaleY) / 2 * MathUtils.sinDeg(getRotation)
    f(cy, hh)
  }

  override def finalize(): Unit = {
    super.finalize()
    WorldFactory.world.destroyBody(body)
  }
}

case class UpBlock(override val body: Body, size: Int)
  extends Block(body, UpBlockType(size))

case class DownBlock(override val body: Body, size: Int)
  extends Block(body, DownBlockType(size))