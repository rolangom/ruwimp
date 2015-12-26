package com.tagor.ras.models

import com.tagor.ras.utils.{Const, BlockConst}

/**
 * Created by rolangom on 6/8/15.
 */
class BlockType(val dimen: Int, val size: Int) {

  def width: Float = size * BlockConst.Size
  def height: Float = BlockConst.Size
  def halfw: Float = width * .5f
  def halfh: Float = height * .5f
  def isDimenUp: Boolean = dimen == BlockConst.DimenUp
  def scale: Float = if (isDimenUp) Const.UpScale else Const.DownScale
  def category: Short = if (isDimenUp) Const.CategoryGroundUp else Const.CategoryGroundDown
  def mask: Short = if(isDimenUp) Const.MaskGroundUp else Const.MaskGroundDown

}

case class UpBlockType(override val size: Int)
  extends BlockType(BlockConst.DimenUp, size)
case class DownBlockType(override val size: Int)
  extends BlockType(BlockConst.DimenDown, size)
