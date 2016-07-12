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
  val BG1_COLOR_INDEX = 1
  val BGCLOUD_COLOR_INDEX = 2
  val BLOCK_UP_INDEX = 3
  val BLOCK_DOWN_INDEX = 4

  val BLOCK_INDEX = 0
  val JOINT_INDEX = 1
  val BG1_INDEX = 2
  val CLOUD_INDEX = 3

  val THEME_CITY = 0
  val THEME_MOUNT = 1
  val THEME_SEA = 2

  val THEMES = Array(THEME_CITY, THEME_MOUNT, THEME_SEA)

  val BLOCK_PATH = "brick_long"//  "imgs/brick_v2.png" // "imgs/block.png"
  val PLAT_PATH = "plat_long" //"imgs/plat_v2.png"
  val WOOD_PATH = "tree_long" //"imgs/tree_v2.png" // "imgs/trunk.png"
  val CHAIN_PATH = "imgs/chain.png"

  val CITY_BG2 = "cities1" //imgs/city1.png"
  val MOUNTS_BG2 = "monts1"// "imgs/mountains1.png"
  val HILLS_BG2 = "hills1" //"imgs/sea_bg.png"

  val CLOUD1_REG = "clouds1"
  val CLOUD2_REG = "clouds2"

//  val CITY_REGIONS = Array[String]("cities1", CLOUD2_REG)
//  val MONTS_REGIONS = Array[String]("monts1", CLOUD2_REG)
//  val HILLS_REGIONS = Array[String]("hills1", "clouds1")

  val CITY_IMGS = Array[String](PLAT_PATH, CHAIN_PATH, CITY_BG2, CLOUD2_REG)
  val MOUNTS_IMGS = Array[String](BLOCK_PATH, CHAIN_PATH, MOUNTS_BG2, CLOUD1_REG)
  val HILLS_IMGS = Array[String](WOOD_PATH, CHAIN_PATH, HILLS_BG2, CLOUD2_REG)

  val THEMES_IMGS = Array[Array[String]](CITY_IMGS, MOUNTS_IMGS, HILLS_IMGS)
//  val THEMES_REGION = Array[Array[String]](CITY_REGIONS, MONTS_REGIONS, HILLS_REGIONS)

  val Red = "a02c2cff" // "f07342" // orage
  val RedStrong = "F03146"
  val BrownDark = "61413C"
  val DarkBlue = "374548ff"
  val Gray = "66a5a6" // "374845ff"
  val Blue = "007673"// "216778ff"
  val BlueStrong = "0080D8"
  val BlueLight = "216778ff"
  val Green = "00a258ff"// "217821ff"
  val GreenStrong = "00BB7F"//
  val Brown = "986639ff" //"784421ff"
  val BrownStrong = "4E3531" //

//  val COLOR_SET_BLUE = Array[String]("e6f4f4ff", Red, Blue)
//  val COLOR_SET_GRAY = Array[String]("ecf0f1ff", Red, Blue)
//  val COLOR_SET_GREEN = Array[String]("ecf4ecff", Green, Brown)
//  val COLOR_SET_YELLOW = Array[String]("f4f4e7ff", Green, Brown)
//  val COLOR_SET_BLUE_LIGHT = Array[String]("e6e6f4ff", Red, Blue)
//  val COLOR_SET_GRAY2 = Array[String]("ecf0f1ff", Red, Blue)

  val COLOR_SET_BLUEL = Array[String]("ffede1ff", "fffaf5ff", "ffffffff", RedStrong, BlueStrong) // "afdde9ff", "b9e1edff", "c1e4efff"
  val COLOR_SET_BLUELL = Array[String]("eaf6f6ff", "f8fcfcff", "ffffffff", RedStrong, BlueStrong) // "85f7f7ff", "6cf7f6ff", "a0f9f9ff"
  val COLOR_SET_BLUEGREENL = Array[String]("f2f1eeff", "fbfaf9ff", "ffffffff", RedStrong, BlueStrong) // "b9eff2ff", "c6f6f8ff", "d4fcfdff"

  val COLOR_SET_BROWNL = Array[String]("fff5e1ff", "fffdf5ff", "ffffffff", Red, Blue) // "cfb391ff", "dcbe9aff", "e4c9a7ff"
  val COLOR_SET_BLUEGREENS = Array[String]("e1fff8ff", "f5fffdff", "ffffffff", Red, Blue) // "82d23aff", "a7e237ff", "bae41bff"
  val COLOR_SET_YELLOW = Array[String]("f0ffe1ff", "fbfff5ff", "ffffffff", Red, Blue) // "eaddaaff", "f8e9b3ff", "fff1baff"

  val COLOR_SET_BROWND = Array[String]("e1fffaff", "f5ffffff", "ffffffff", Green, Brown)  // "8a664aff", "926d4fff", "987053ff"
  val COLOR_SET_VIOLET = Array[String]("e1ffefff", "f5fffaff", "ffffffff", GreenStrong, BrownStrong) // "523770ff", "5b3a7cff", "603c83ff"
  val COLOR_SET_GREEND = Array[String]("f0e1ffff", "faf5ffff", "ffffffff", Green, BrownDark) // "2ca089ff", "2dad96ff", "2eb6a1ff"

  val COLOR_SET_CITY = Array[Array[String]](COLOR_SET_BLUELL, COLOR_SET_BLUEL, COLOR_SET_BLUEGREENL)
  val COLOR_SET_MOUNT = Array[Array[String]](COLOR_SET_BLUEGREENS, COLOR_SET_BROWNL, COLOR_SET_YELLOW)
  val COLOR_SET_HILL = Array[Array[String]](COLOR_SET_GREEND, COLOR_SET_BROWND, COLOR_SET_VIOLET)

  val COLOR_THEME_SET = Array[Array[Array[String]]](COLOR_SET_CITY, COLOR_SET_MOUNT, COLOR_SET_HILL)

}
