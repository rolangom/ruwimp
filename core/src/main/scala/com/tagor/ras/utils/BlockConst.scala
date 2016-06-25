package com.tagor.ras.utils

import com.badlogic.gdx.graphics.Color
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

//  val Width: (Int, Int) => Float = (d: Int, s: Int) => Scales(d) * Sizes(s) * Size
  def Width(d: Int, s: Int): Float = Scales(d) * Sizes(s) * Size
  def Height(d: Int): Float = Scales(d) * Size

  val LevelEasy = 0
  val LevelMid = 1
  val LevelHard = 2
  val LevelVeryHard = 3

  val Levels = Array(LevelEasy, LevelMid, LevelHard, LevelVeryHard)

  val WorldVertPos = Array(0, 1, 2, 3, 4)

  val Angles = Array(10, 20, 30, 40)

  // BG0, BG1, BG2, CLOUDS
  val BG0_COLOR_INDEX = 0
  val BLOCK_UP_INDEX = 1
  val BLOCK_DOWN_INDEX = 2

  val BLOCK_INDEX = 0
  val JOINT_INDEX = 1
  val BG1_INDEX = 2

  val THEME_CITY = 0
  val THEME_MOUNT = 1
  val THEME_SEA = 2

  val THEMES = Array(THEME_CITY, THEME_MOUNT, THEME_SEA)

  val BLOCK_PATH = "imgs/brick_v2.png" // "imgs/block.png"
  val PLAT_PATH = "imgs/plat_v2.png"
  val WOOD_PATH = "imgs/tree_v2.png" // "imgs/trunk.png"
  val CHAIN_PATH = "imgs/chain.png"

  val CITY_BG2 = "imgs/city1.png"
  val MOUNTS_BG2 = "imgs/mountains1.png"
  val SEA_BG = "imgs/sea_bg.png"

  val CITY_IMGS = Array[String](BLOCK_PATH, CHAIN_PATH, CITY_BG2)
  val MOUNTS_IMGS = Array[String](WOOD_PATH, CHAIN_PATH, MOUNTS_BG2)
  val SEA_IMGS = Array[String](PLAT_PATH, CHAIN_PATH, SEA_BG)

  val THEMES_IMGS = Array[Array[String]](CITY_IMGS, MOUNTS_IMGS, SEA_IMGS)

  val Red = "a02c2cff" // "f07342" // orage
  val DarkBlue = "374548ff"
  val Gray = "66a5a6" // "374845ff"
  val Blue = "007673"// "216778ff"
  val BlueLight = "216778ff"
  val Green = "00a258ff"// "217821ff"
  val Brown = "986639ff" //"784421ff"

  val COLOR_SET_BLUE = Array[String]("e6f4f4ff", Red, Blue)
  val COLOR_SET_GRAY = Array[String]("ecf0f1ff", Red, Blue)
  val COLOR_SET_GREEN = Array[String]("ecf4ecff", Green, Brown)
  val COLOR_SET_YELLOW = Array[String]("f4f4e7ff", Green, Brown)
  val COLOR_SET_BLUE_LIGHT = Array[String]("e6e6f4ff", Gray, Blue)
  val COLOR_SET_GRAY2 = Array[String]("ecf0f1ff", Gray, Blue)

  val COLOR_SET_CITY = Array[Array[String]](COLOR_SET_BLUE, COLOR_SET_GRAY)
  val COLOR_SET_MOUNT = Array[Array[String]](COLOR_SET_GREEN, COLOR_SET_YELLOW)
  val COLOR_SET_SEA = Array[Array[String]](COLOR_SET_BLUE_LIGHT, COLOR_SET_GRAY2)
  val COLOR_THEME_SET = Array[Array[Array[String]]](COLOR_SET_CITY, COLOR_SET_MOUNT, COLOR_SET_SEA)

}
