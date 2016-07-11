package com.tagor.ras.utils

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.math.Vector2

/**
 * Created by rolangom on 6/8/15.
 */
object Const {

  val Width = 800; //960; //
  val Height = 480; //640; //

  val PPM = 85f
  val MPP:Float = 1f/PPM

  val UpScale: Float = 1f
  val DownScale: Float = .85f

  val SmallerFactor = .65f
  val LargerFactor = 1.25f

  val TransitTime = .1f

  val CategoryGroundUp: Short = 0x0001
  val CategoryPlayerUp: Short = 0x0002
  val CategoryGroundDown: Short = 0x0004
  val CategoryPlayerDown: Short = 0x0008
  val CategoryPlayerMiddle: Short = 0x0016
  val CategoryTop: Short = 0x0032

  val MaskPlayerUp: Short = (CategoryGroundUp | CategoryTop).toShort
  val MaskPlayerDown: Short = (CategoryGroundDown | CategoryTop).toShort
  val MaskPlayerMiddle: Short = CategoryTop
  val MaskGroundUp: Short = CategoryPlayerUp
  val MaskGroundDown: Short = CategoryPlayerDown
  val MaskTop: Short = (CategoryPlayerDown | CategoryPlayerUp | MaskPlayerMiddle).toShort

  val RunnerHeight = 64f
  val RunnerWidth = 35f

  val GroundStrType = "ground"
  val PlayerStrType = "player"
  val FootStrType = "foot"
  val TopStrType = "top"

  val RunnerJumpingLinearImpulse = new Vector2(0, 12f)
  val RunnerGravityScale = 3.5f
  val RunnerGravityScaleOnair = RunnerGravityScale / 2
  val RunnerLinearVelocity = 3.5f //4.65f
  val RunnerX = Width * .2f
  val RunnerY = Height - RunnerHeight

  val Jump = Input.Keys.SPACE
  val JumpReleased = Input.Keys.SPACE - 100
  val GoUp = Input.Keys.UP
  val GoDown = Input.Keys.DOWN
  val Toggle = Input.Keys.ENTER

  val Running: Int = 0
  val Jumping: Int = 1
  val Falling: Int = 2

  val RunnerMaxSpeed: Float = Const.RunnerLinearVelocity * 3f

  val GroundLinearVelocity = .15f

  val ThemeImg = 0
  val ThemeColor = 1

  val GameStateHome = 0
  val GameStatePlay = 1
  val GameStateOver = 2
  val GameStatePlayAgain = 3
  val GameStatePause = 4
  val GameStateResume = 5

  val CurrFont = "fonts/LAIKA.ttf" // "fonts/AldotheApache.ttf" //

  val HelpToPlayStr = new String("HelpToPay")
  val PlayStr = new String("play")
  val ShareStr = new String("Share")
  val ShareScoreStr = new String("ShareScore")
  val SoundStr = new String("Sound")
  val NoAdsStr = new String("NoAds")
  val RateStr = new String("Rate")
  val PlayAgainStr = new String("playAgain")
  val LeaderBoardStr = new String("LeaderBoard")
  val AchivementsStr = new String("Achivements")
  val SubmitLeaderBoardStr = new String("SubmitLeaderBoard")
  val SubmitAchivementsStr = new String("SubmitAchivements")
  val PausedStr = new String("Pause")
  val ResumeStr = new String("Resume")
  val GoHomeStr = new String("GoHome")
  val ExitStoreStr = new String("GoHomeFromStore")
  val HelpStr = new String("Help")
  val InfoStr = new String("Info")
  val TwitterStr = new String("CompTwitter")
  val ExitFromHelpStr = new String("GoHomeFromHelp")
  val MyTwitterStr = new String("MyTwitter")
  val ExitFromInfoStr = new String("GoHomeFromInfo")
  val StoreStr = new String("Store")

  val BGS_PATH = "atlas/worldAtlas3.txt"

  val CLOUD_IMG = "cloud%d"

  val IOSUrl = "https://itunes.apple.com/app/id980891232"
  val AndroidUrl = "http://play.google.com/store/apps/details?id=com.tagor.ras"
  val AppUrl = Gdx.app.getType match {
    case ApplicationType.iOS => IOSUrl
    case _ => AndroidUrl
  }

  val ShareMsg = s"Check this very fun game <<Run, Jump 'n Switch>> @ iOS: $IOSUrl, Android: $AndroidUrl"
  def ShareScoreMsg = s"See my score (${ScoreMgr.score}) in this very fun game <<Run, Jump 'n Switch>> @ iOS: $IOSUrl, Android: $AndroidUrl"

  val ItemStoreFontNameSize = 18
  val ItemStoreFontPriceSize = 22
  val ItemStoreSpace = 4
  val ItemStoreSize = 124
}
