package com.tagor.ras.utils

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

/**
 * Created by rolangom on 6/8/15.
 */
object Const {

  val Width = 800; // 960;
  val Height = 480; // 640;

  val PPM = 85f
//  val PTM = 1f/MTP

  val UpScale: Float = 1f
  val DownScale: Float = .85f

  val TransitTime = .150f

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
  val RunnerWidth = 32f

  val GroundStrType = "ground"
  val PlayerStrType = "player"
  val FootStrType = "foot"
  val TopStrType = "top"

  val RunnerJumpingLinearImpulse = new Vector2(0, 12f)
  val RunnerGravityScale = 3f
  val RunnerGravityScaleOnair = RunnerGravityScale / 2
  val RunnerLinearVelocity = 3.5f //4.65f
  val RunnerX = Width * .2f
  val RunnerY = Height * 1.05f

  val GroundLinearVelocity = .15f

  val ThemeImg = 0
  val ThemeColor = 1

  val GameStateHome = 0
  val GameStatePlay = 1
  val GameStateOver = 2
  val GameStatePlayAgain = 3
  val GameStatePause = 4
  val GameStateResume = 5

  val CurrFont = "fonts/LAIKA.ttf"

  val HelpToPlayStr = new String("HelpToPay")
  val PlayStr = new String("play")
  val ShareStr = new String("Share")
  val ShareScoreStr = new String("ShareScore")
  val SoundStr = new String("Sound")
  val NoAdsStr = new String("NoAds")
  val RateStr = new String("Rate")
  val PlayAgainStr = new String("playAgain")
  val LeaderBoardStr = new String("LeaderBoard")
  val SubmitLeaderBoardStr = new String("SubmitLeaderBoard")
  val PausedStr = new String("Pause")
  val ResumeStr = new String("Resume")
  val GoHomeStr = new String("GoHome")
  val HelpStr = new String("Help")
  val InfoStr = new String("Info")
  val TwitterStr = new String("CompTwitter")
  val ExitFromHelpStr = new String("GoHomeFromHelp")
  val MyTwitterStr = new String("MyTwitter")
  val ExitFromInfoStr = new String("GoHomeFromInfo")

  val BGS_PATH = "atlas/worldAtlas2.txt"

  val CLOUD_IMG = "cloud%d"

  val IOSUrl = "https://itunes.apple.com/app/id980891232"
  val AndroidUrl = "http://play.google.com/store/apps/details?id=com.tagor.ras"
  val AppUrl = Gdx.app.getType match {
    case ApplicationType.iOS => IOSUrl
    case _ => AndroidUrl
  }

  val ShareMsg = s"Check this very fun game <<Run, Jump 'n Switch>>  @ $AppUrl"
  def ShareScoreMsg = s"Check my score (${ScoreMgr.score}) in this very fun game <<Run, Jump 'n Switch>> @ $AppUrl"
}
