package com.tagor.ras.utils

import com.badlogic.gdx.audio.Sound

/**
  * Created by rolangom on 3/24/16.
  */
object SoundMgr {

  private var gameOverSound: Sound = _
  private var scoredSound: Sound = _

  private var footStepSound: Sound = _
  private var jumpSound: Sound = _
  private var goingUpSound: Sound = _
  private var goingDownSound: Sound = _

  private var currProfile: Playable = _
  def isOn: Boolean = PrefMgr.prefs.getBoolean("isSoundOn", true)

  def toggle(): Unit = {
    val lIsOn = !isOn
    PrefMgr.prefs.putBoolean("isSoundOn", lIsOn)
    PrefMgr.prefs.flush()
    currProfile = if (lIsOn) getNewPlayableTrue else new PlayableFalse()
    println(s"SoundMgr isOn $lIsOn")
  }

  def soundBtnStr = if (isOn) "sound_on_btn" else "sound_off_btn"

  def init(): Unit = {
    resume()
  }

  private def getNewPlayableTrue: Playable = new PlayableTrue(
    gameOverSound, scoredSound, footStepSound,
    jumpSound, goingUpSound, goingDownSound)

  def playGameOver(): Unit = currProfile.playGameOver()
  def playScored(): Unit = currProfile.playScored()
  def playGoingUp(): Unit = currProfile.playGoingUp()
  def playGoingDown(): Unit = currProfile.playGoingDown()
  def playJump(vol: Float, pit: Float, pan: Float): Unit = currProfile.playJump(vol, pit, pan)
  def playFootStep(vol: Float): Unit = currProfile.playFootStep(vol)
  def stopFootStep(): Unit = currProfile.stopFootStep()

  def resume(): Unit = {
    gameOverSound = ResMgr.getSound("audio/jingles_PIZZA01.mp3")
    scoredSound = ResMgr.getSound("audio/powerUp2.mp3")

    footStepSound = ResMgr.getSound("audio/footstep09.mp3")
    jumpSound = ResMgr.getSound("audio/phaseJump1.mp3")
    goingUpSound = ResMgr.getSound("audio/highUp.mp3")
    goingDownSound = ResMgr.getSound("audio/highDown.mp3")

    currProfile = if (isOn) getNewPlayableTrue else new PlayableFalse()
  }

  def pause(): Unit = {
    ResMgr.remove("audio/jingles_PIZZA01.mp3")
    ResMgr.remove("audio/powerUp2.mp3")

    Array[Sound](
      footStepSound, jumpSound, goingUpSound, goingDownSound
    ).foreach(_.stop())

    ResMgr.remove(
      "audio/footstep09.mp3",
      "audio/phaseJump1.mp3",
      "audio/highUp.mp3",
      "audio/highDown.mp3"
    )
  }
}

trait Playable {

  def playGameOver(): Unit = { }
  def playScored(): Unit = { }
  def playGoingUp(): Unit = { }
  def playGoingDown(): Unit = { }
  def playJump(vol: Float, pit: Float, pan: Float): Unit = { }
  def playFootStep(vol: Float): Unit = { }
  def stopFootStep(): Unit = { }
}

class PlayableFalse extends Playable {}

class PlayableTrue(gameOverSound: Sound,
                   scoredSound: Sound,
                   footStepSound: Sound,
                   jumpSound: Sound,
                   goingUpSound: Sound,
                   goingDownSound: Sound) extends Playable {

  override def playGameOver(): Unit = {
    gameOverSound.play()
  }
  override def playScored(): Unit = {
    scoredSound.play()
  }
  override def playGoingUp(): Unit = {
    goingUpSound.play()
  }
  override def playGoingDown(): Unit = {
    goingDownSound.play()
  }
  override def playJump(vol: Float, pit: Float, pan: Float): Unit = {
    jumpSound.play(vol, pit, pan)
  }
  override def playFootStep(vol: Float): Unit = {
    footStepSound.stop()
    footStepSound.play()
    footStepSound.loop(vol, 1.45f, 0f)
  }
  override def stopFootStep(): Unit = {
    footStepSound.stop()
  }
}
