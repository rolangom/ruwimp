package com.tagor.ras.stages

import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.utils.viewport._
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
        case Const.LeaderBoardStr =>
          (() => RxMgr.showLeaderBoard.onNext(""), true, false)
        case Const.AchivementsStr =>
          (() => RxMgr.showAchivements.onNext(""), true, false)
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
        case Const.MyTwitterStr =>
          (() => openTwitter(true), true, false)
        case Const.TwitterStr =>
          (() => openTwitter(false), true, false)
        case Const.RateStr =>
          (() => rateApp(), true, false)
        case Const.InfoStr =>
          (() => showInfoTable(), true, false)
        case Const.ExitFromInfoStr =>
          (() => hideInfoTable(), true, false)
        case Const.StoreStr =>
          (() => showStoreTable(), true, false)
        case Const.ExitStoreStr =>
          (() => hideStoreTable(), true, false)
        case _ => (() => (), false, true)
      }
      if (block)
        event.getTarget.setTouchable(Touchable.disabled)
      event.getTarget.addAction(clickEffect(act))
      touched
    }
  }

  private var _gtable: GameTable = _
  private var _stable: StartTable = _
  private var _dtable: DashboardTable = _
  private var _ptable: PausedTable = _
  private var _htable:  InstrTable = _
  private var _itable:  InfoTable = _
  private var _storeTable: StoreTable = _

  private def gtable: GameTable = {
    if (_gtable == null)
      _gtable = new GameTable(clickListener)
    _gtable
  }
  private def stable: StartTable = {
    if (_stable == null)
      _stable = new StartTable(clickListener)
    _stable
  }
  private def dtable: DashboardTable = {
    if (_dtable == null)
      _dtable = new DashboardTable(clickListener)
    _dtable
  }
  private def ptable: PausedTable = {
    if (_ptable == null)
      _ptable = new PausedTable(clickListener)
    _ptable
  }
  private def htable: InstrTable = {
    if (_htable == null)
      _htable = new InstrTable(clickListener)
    _htable
  }
  private def itable: InfoTable = {
    if (_itable == null)
      _itable = new InfoTable(clickListener)
    _itable
  }
  private def storeTable: StoreTable = {
    if (_storeTable == null)
      _storeTable = new StoreTable(clickListener)
    _storeTable
  }

  private lazy val (screenSideR, screenSideL) = getScreenSideRects

  private var cTouchDown: (Int, Int, Int, Int) => Boolean = super.touchDown
  private var cTouchUp: (Int, Int, Int, Int) => Boolean = super.touchUp
  private var cKeyDown: (Int) => Boolean = super.keyDown
  private var cKeyUp: (Int) => Boolean = super.keyUp

  private var subs: Subscription = _

  def init(): Unit = {
    clear()
    showStartTable()

    subs = RxMgr.onGameState
      .filter(s => s == Const.GameStatePlay || s == Const.GameStateOver)
      .map(_ == Const.GameStatePlay)
      .subscribe(r => handleGame(r))
  }

  private def showStartTable(): Unit = {
    addActor(stable)
    stable.show()
    _stable.updateStatLbl()
    RxMgr.setBannerVisible(false)
  }

  private def showHelpTable(from: AnyRef): Unit = {
    stable.hide()
    addActor(htable)
    _htable.setUserObject(from)
    _htable.show()
  }

  private def hideHelpTable(): Unit = {
    htable.getUserObject match {
      case Const.HelpToPlayStr =>
        RxMgr.onGameState.onNext(Const.GameStatePlay)
      case _ =>
        showStartTable()
    }
    htable.hideAndFunc(() =>{
      _htable = null
    })
  }

  private def showInfoTable(): Unit = {
    stable.hide()
    addActor(itable)
    _itable.show()
    RxMgr.setBannerVisible(true)
  }

  private def hideInfoTable(): Unit = {
    itable.hideAndFunc(() => {
      _itable = null
    })
    addActor(stable)
    _stable.show()
    RxMgr.setBannerVisible(false)
  }

  private def showStoreTable(): Unit = {
    stable.hide()
    addActor(storeTable)
    _storeTable.show()
  }

  private def hideStoreTable(): Unit = {
    storeTable.hideAndFunc(() => {
      _storeTable = null
    })
    addActor(stable)
    _stable.show()
  }

  private def openTwitter(personal: Boolean): Unit = {
    val url = if (personal) "http://www.twitter.com/rolangom" else "http://www.twitter.com/rolangom"
    Gdx.net.openURI(url)
  }

  private def rateApp(): Unit = {
    Gdx.net.openURI(Const.AppUrl)
  }

  private def toggleSound(): Unit = {
    stable.toggleSound()
  }

  private def resumeGame(): Unit = {
    RxMgr.setBannerVisible(false)
    ptable.hideAndFunc(() => {
      _ptable = null
    })
    gtable.setTouchableEnabled()
    configGameInput(true)
    addActionDelayed(1.5f, () => RxMgr.onGameState.onNext(Const.GameStateResume))
  }

  private def showGameTable(): Unit = {
    stable.hideAndFunc(() => {
      _stable = null
    })
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
    dtable.hideAndFunc(
      () => RxMgr.onGameState.onNext(Const.GameStatePlay))
  }

  private def goHome(): Unit = {
    if (ptable.hasParent)
      ptable.hideAndFunc(() => {
        _ptable = null
      })
    if (dtable.hasParent)
      dtable.hide()
    gtable.hideAndFunc(() => {
      _gtable = null
    })
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
      _dtable.show()
    })
  }

  private def handleGame(isRunning: Boolean): Unit = {
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
    val v = stageToScreenCoordinates(new Vector2(0f, Const.RunnerHeight * Const.LargerFactor))
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
      RxMgr.onPlayerAction.onNext(Const.Jump)
      return true
    } else if (screenSideL.contains(screenX, screenY)) {
      RxMgr.onPlayerAction.onNext(Const.Toggle)
      return true
    }
    super.touchDown(screenX, screenY, pointer, button)
  }

  private def runningTouchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    if (screenSideR.contains(screenX, screenY)) {
      RxMgr.onPlayerAction.onNext(Const.JumpReleased)
      return true
    }
    false
  }

  def pause(): Unit = {
    if (RxMgr.isGmRunning)
      pauseGame()
  }

  def resume(): Unit = {

  }

  private def emptyAct(delta: Float): Unit = { }


  override def dispose(): Unit = {
    super.dispose()
    subs.unsubscribe()
  }
}
