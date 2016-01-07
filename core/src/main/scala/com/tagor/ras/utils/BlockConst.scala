package com.tagor.ras.utils

import com.tagor.ras.models.BlockType

/**
 * Created by rolangom on 6/8/15.
 */
object BlockConst {

  val Size: Int = 32

  val DimenUp: Int = 0
  val DimenDown: Int = 1

  val Dimens = Array(DimenUp, DimenDown)
  val Scales = Array(Const.UpScale, Const.DownScale)

  private val SizeSCount = 4
  private val SizeMCount = 8
  private val SizeLCount = 16
  private val SizeXLCount = 32

  val SizeS = 0
  val SizeM = 1
  val SizeL = 2
  val SizeXL = 3

  val Sizes = Array(SizeSCount, SizeMCount, SizeLCount, SizeXLCount)

  val BlockTypes: Array[Array[BlockType]] = Array.ofDim[BlockType](Dimens.length, Sizes.length)

  for (i <- Dimens.indices;
       j <- Sizes.indices) {
    BlockTypes(i)(j) = new BlockType(Dimens(i), Sizes(j))
  }

  val Width: (Int, Int) => Float = (d: Int, s: Int) => Scales(d) * Sizes(s) * Size

  val LevelEasy = 0
  val LevelMid = 1
  val LevelHard = 2
  val LevelVeryHard = 3

  val Levels = Array(LevelEasy, LevelMid, LevelHard, LevelVeryHard)

  val WorldVertPos = Array(0, 1, 2, 3, 4)

  val Angles = Array(10, 20, 30, 40)
//
//  case class Blck(level: Int, dimen: Int, size: Int, x: Float, y: Float, angle: Float)
//  case class RelBlck(posBlck: Blck, blcks: Array[Blck])
//
////  3 2 4 3
//
//  val relBlcks = Array.ofDim[RelBlck](Levels.length, Dimens.length, Sizes.length)
//
//  def init(): Unit = {
//    for (
//      l <- Levels.indices;
//      d <- Dimens.indices;
//      s <- Sizes.indices) {
//      relBlcks =
//    }
//  }
}
