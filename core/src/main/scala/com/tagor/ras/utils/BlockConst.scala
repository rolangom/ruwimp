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

  val Width: (Int, Int) => Float = (d: Int, s: Int) => Scales(d) * Sizes(s) * Size

  val LevelEasy = 0
  val LevelMid = 1
  val LevelHard = 2
  val LevelVeryHard = 3

  val Levels = Array(LevelEasy, LevelMid, LevelHard, LevelVeryHard)

  val WorldVertPos = Array(0, 1, 2, 3, 4)

  val Angles = Array(10, 20, 30, 40)

  // BG0, BG1, BG2, CLOUDS
  val BG0_COLOR_INDEX = 0
  val BG1_COLOR_INDEX = 1
  val BG2_COLOR_INDEX = 2 // full Bg
  val CLOUDS_COLOR_INDEX = 3
  val BLOCK_UP_INDEX = 4
  val BLOCK_DOWN_INDEX = 5

  val BLOCK_INDEX = 0
  val JOINT_INDEX = 1 // 2;
  val BG1_INDEX = 2 // 3;
  val BG2_INDEX = 3 //4;

  val THEME_CITY = 0
  val THEME_MOUNT = 1

  val THEMES = Array(THEME_CITY, THEME_MOUNT)

  val BLOCK_PATH = "imgs/plat.png"
  val WOOD_PATH = "imgs/platWoLi.png"
  val CHAIN_PATH = "imgs/chain.png"

  val CITY_BG1 = "imgs/city0.png"
  val CITY_BG2 = "imgs/city1.png"
  val MOUNTS_BG1 = "imgs/mountains0.png"
  val MOUNTS_BG2 = "imgs/mountains1.png"

  val CITY_IMGS = Array[String](BLOCK_PATH, CHAIN_PATH, CITY_BG1, CITY_BG2)
  val MOUNTS_IMGS = Array[String](WOOD_PATH, CHAIN_PATH, MOUNTS_BG1, MOUNTS_BG2)

  val THEMES_IMGS = Array[Array[String]](CITY_IMGS, MOUNTS_IMGS)

  val Red = Color.RED.toString
  val Blue = Color.BLUE.toString
  val Green = Color.GREEN.toString
  val Brown = Color.BROWN.toString

  val COLOR_SET_BLUE = Array[String]("006680ff", "0088aaff", "00aad4ff", "00ccffff", Red, Blue)
  val COLOR_SET_GRAY = Array[String]("333333ff", "4d4d4dff", "666666ff", "808080ff", Red, Blue)
  val COLOR_SET_GREEN = Array[String]("217844ff", "2ca05aff", "37c871ff", "5fd38dff", Green, Brown)
  val COLOR_SET_YELLOW = Array[String]("554400ff", "806600ff", "aa8800ff", "d4aa00ff", Green, Brown)

  val COLOR_SET_CITY = Array[Array[String]](COLOR_SET_BLUE, COLOR_SET_GRAY)
  val COLOR_SET_MOUNT = Array[Array[String]](COLOR_SET_GREEN, COLOR_SET_YELLOW)
  val COLOR_THEME_SET = Array[Array[Array[String]]](COLOR_SET_CITY, COLOR_SET_MOUNT)

}
