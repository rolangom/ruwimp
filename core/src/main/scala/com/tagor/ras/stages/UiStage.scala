package com.tagor.ras.stages

import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.utils.viewport.{ExtendViewport, FillViewport, FitViewport, StretchViewport}
import com.tagor.ras.models.{RxPlayerConst, Showable}
import com.tagor.ras.models.tables._
import com.tagor.ras.utils._
import rx.lang.scala.Subscription

/**
  * Created by rolangom on 12/12/15.
  */
class UiStage(batch: Batch)
  extends Stage(new ExtendViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch){

  private lazy val clickListener = new ClickListener() {
    override def touchDown(event: InputEvent,
                           x: Float, y: Float,
                           pointer: Int,
                           button: Int): Boolean = {
      val (act, touched, block) = event.getTarget.getUserObject match {
        case Const.PlayStr =>
          (() => RxMgr.onGameState.onNext(Const.GameStatePlay), true, true)
        case Const.PlayAgainStr =>
          (() => playAgain(), true, true)
        case Const.PausedStr =>
          (() => {
            RxMgr.onGameState.onNext(Const.GameStatePause)
            pauseGame()
          }, true, true)
        case Const.GoHomeStr =>
          (() => {
            RxMgr.onGameState.onNext(Const.GameStateHome)
            goHome()
          }, true, true)
        case Const.SoundStr =>
          (() => toggleSound(), true, false)
        case Const.ResumeStr =>
          (() => resumeGame(), true, true)
        case s @ (Const.HelpStr | Const.HelpToPlayStr) =>
          (() => showHelpTable(s), true, false)
        case Const.ExitFromHelpStr =>
          (() => hideHelpTable(), true, false)
        case Const.ShareStr =>
          (() => RxMgr.showShareText(Const.ShareMsg), true, false)
        case Const.ShareScoreStr =>
          (() => RxMgr.showShareText(Const.ShareScoreMsg), true, false)
        case _ => (() => (), false, true)
      }
      if (block)
        event.getTarget.setTouchable(Touchable.disabled)
      event.getTarget.addAction(clickEffect(act))
      touched
    }
  }

  private def clickEffect(f: () => Unit): Action = {
    parallel(
      sequence(
        color(Color.GRAY, .15f),
        color(Color.WHITE, .15f)
      ),
      sequence(
        scaleBy(-.25f, -.25f, .15f),
        scaleTo(1f, 1f, .15f),
//        delay(.15f),
        run(runnable(f))
      )
    )
  }

  private lazy val gtable = new GameTable(clickListener)
  private lazy val stable = new StartTable(clickListener)
  private lazy val dtable = new DashboardTable(clickListener)
  private lazy val ptable = new PausedTable(clickListener)
  private lazy val htable = new InstrTable(clickListener)

  private var currAct: Float => Unit = emptyAct
  private lazy val (screenSideR, screenSideL) = getScreenSideRects

  private var cTouchDown: (Int, Int, Int, Int) => Boolean = super.touchDown
  private var cTouchUp: (Int, Int, Int, Int) => Boolean = super.touchUp
  private var cKeyDown: (Int) => Boolean = super.keyDown
  private var cKeyUp: (Int) => Boolean = super.keyUp

  private var subs: Subscription = _

  def init(): Unit = {
    clear()

    stable.init()
    gtable.init()
    dtable.init()
    ptable.init()
    htable.init()

    showStartTable()

    subs = RxMgr.onGameState
      .filter(s => s == Const.GameStatePlay || s == Const.GameStateOver)
      .map(_ == Const.GameStatePlay)
      .subscribe(r => handleGame(r))
  }

  def hideAndShow(toHide: Showable, toShow: Showable): Unit = {
    toHide.hide()
    addActor(toShow.asInstanceOf[Actor])
    toShow.show()
  }

  private def showStartTable(): Unit = {
    addActor(stable)
    stable.show()
  }

  private def showHelpTable(from: AnyRef): Unit = {
    stable.hide()
    addActor(htable)
    htable.setUserObject(from)
    htable.show()
  }

  private def hideHelpTable(): Unit = {
    htable.hide()
    htable.getUserObject match {
      case Const.HelpToPlayStr =>
        RxMgr.onGameState.onNext(Const.GameStatePlay)
      case _ =>
        showStartTable()
    }
  }

  private def toggleSound(): Unit = {
    stable.toggleSound()
  }

  private def resumeGame(): Unit = {
    ptable.hide()
    gtable.setTouchableEnabled()
    configGameInput(true)
    addActionDelayed(1.5f, () => RxMgr.onGameState.onNext(Const.GameStateResume))
  }

  private def showGameTable(): Unit = {
    stable.hide()
    addGameTbl()
  }

  def pauseGame(): Unit = {
    addPausedTbl()
    configGameInput(false)
  }

  private def addPausedTbl(): Unit = {
    addActor(ptable)
    ptable.show()
    RxMgr.setBannerVisible(true)
  }

  private def playAgain(): Unit = {
    RxMgr.setBannerVisible(false)
    dtable.hideAndFunc {
      () => RxMgr.onGameState.onNext(Const.GameStatePlay)
    }
  }

  private def goHome(): Unit = {
    if (ptable.hasParent)
      ptable.hide()
    if (dtable.hasParent)
      dtable.hide()
    gtable.hide()
    addActionDelayed(.75f, () => showStartTable())
  }

  private def addGameTbl(): Unit = {
    addActionDelayed(1f, () => {
      addActor(gtable)
      gtable.show()
    })
  }

  private def addActionDelayed(t: Float, action: () => Unit): Unit = {
    addAction(delay(t, run(runnable(action))))
  }

  private def addDashboardTbl(): Unit = {
    RxMgr.setInterstitialVisible(true)
    RxMgr.setBannerVisible(true)
    addActionDelayed(.5f, () => {
      addActor(dtable)
      dtable.show()
    })
  }

  private def handleGame(isRunning: Boolean): Unit = {
    currAct = if (isRunning) stageAct else emptyAct
    configGameInput(isRunning)

    if (isRunning)
      showGameTable()
    else
      post(() => showDashboardTbl())
  }

  private def showDashboardTbl(): Unit = {
    gtable.hide()
    addDashboardTbl()
  }

  private def getScreenSideRects = {
    val v = stageToScreenCoordinates(new Vector2(0f, 64f))
    v.y = Gdx.graphics.getHeight - v.y
    println(s"getScreenSideRects v.y = ${v.y}")
    (new Rectangle(Gdx.graphics.getWidth / 2, v.y,
      Gdx.graphics.getWidth / 2,
      Gdx.graphics.getHeight - v.y),
      new Rectangle(0, v.y,
        Gdx.graphics.getWidth / 2,
        Gdx.graphics.getHeight - v.y))
  }

  override def keyDown(keyCode: Int): Boolean = {
    cKeyDown(keyCode)
  }

  override def keyUp(keyCode: Int): Boolean = {
    cKeyUp(keyCode)
  }

  private def configGameInput(running: Boolean): Unit = {
    cTouchDown = if (running) runningTouchDown else super.touchDown
    cKeyDown = if (running) runningKeyDown else super.keyDown
    cTouchUp = if (running) runningTouchUp else super.touchUp
    cKeyUp = if (running) runningKeyUp else super.keyUp
  }

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    cTouchDown(screenX, screenY, pointer, button)
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    cTouchUp(screenX, screenY, pointer, button)
  }

  private def runningKeyDown(keyCode: Int): Boolean = {
    RxMgr.onPlayerAction.onNext(keyCode)
    true
  }

  private def runningKeyUp(keyCode: Int): Boolean = {
    RxMgr.onPlayerAction.onNext(keyCode - 100)
    true
  }

  private def runningTouchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    if (screenSideR.contains(screenX, screenY)) {
      RxMgr.onPlayerAction.onNext(RxPlayerConst.Jump)
      return true
    } else if (screenSideL.contains(screenX, screenY)) {
      RxMgr.onPlayerAction.onNext(RxPlayerConst.Toggle)
      return true
    }
    super.touchDown(screenX, screenY, pointer, button)
  }

  private def runningTouchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    if (screenSideR.contains(screenX, screenY)) {
      RxMgr.onPlayerAction.onNext(RxPlayerConst.JumpReleased)
      return true
    }
    false
  }

  def pause(): Unit = {
    if (RxMgr.isGmRunning)
      pauseGame()
  }

  def resume(): Unit = {
//    ptable.show()
  }

  private def emptyAct(delta: Float): Unit = { }

  private def stageAct(delta: Float): Unit = {
    gtable.setFpsText(s"FPS: ${Gdx.graphics.getFramesPerSecond}, body count: ${WorldFactory.world.getBodyCount}")
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    currAct(delta)
  }

  override def dispose(): Unit = {
    super.dispose()
    subs.unsubscribe()
  }
}
