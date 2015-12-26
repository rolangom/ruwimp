package com.tagor.ras.models

import com.badlogic.gdx.utils.Pool
import com.tagor.ras.utils.BlockConst

/**
  * Created by rolangom on 12/12/15.
  */
class ItemToInst extends Pool.Poolable {

  var size: Int = _
  var dimen: Int = _
  @volatile
  var isLast: Boolean = _
  var x: Float = _
  var y: Float = _
  var angle: Float = _

  def isGoingUp: Boolean = dimen == BlockConst.DimenUp

  def init(dimen: Int,
           size: Int,
           x: Float,
           y: Float,
           angle: Float,
           isLast: Boolean = false): ItemToInst = {
    this.dimen = dimen
    this.size = size
    this.x = x
    this.y = y
    this.angle = angle
    this.isLast = isLast
    this
  }

  def init(size: Int,
           x: Float,
           y: Float,
           angle: Float,
           isGoingUp: Boolean,
           isLast: Boolean) {
    init(size, isGoingUp, x, y, angle)
    this.isLast = isLast
  }

  def init(size: Int,
           isGoingUp: Boolean,
           x: Float,
           y: Float,
           angle: Float) {
    this.size = size
    this.dimen = if (isGoingUp) BlockConst.DimenUp else BlockConst.DimenDown
    this.x = x
    this.y = y
    this.angle = angle
  }

  def asLast() {
    isLast = true
  }

  override def reset() {
    size = 0
    dimen = 0
    x = 0f
    y = 0f
    angle = 0f
    isLast = false
  }
}
