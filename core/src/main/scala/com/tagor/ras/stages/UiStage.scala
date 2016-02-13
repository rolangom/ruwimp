package com.tagor.ras.stages

import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.{Touchable, Actor, InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.FitViewport
import com.tagor.ras.models.RxPlayerConst
import com.tagor.ras.models.tables.{DashboardTable, StartTable, GameTable}
import com.tagor.ras.utils._

/**
  * Created by rolangom on 12/12/15.
  */
class UiStage(batch: Batch)
  extends Stage(new FitViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch){

  private lazy val uiClickListener = new ClickListener() {
    override def touchDown(event: InputEvent,
                           x: Float, y: Float,
                           pointer: Int,
                           button: Int): Boolean = {
      val (act, touched) = event.getTarget.getUserObject match {
        case Const.PlayStr =>
          (() => RxMgr.onGameState.onNext(Const.GameStatePlay), true)
        case Const.PlayAgainStr =>
          (() => playAgain(), true)
        case _ => (() => (), false)
      }
      if (touched)
        event.getTarget.setTouchable(Touchable.disabled)
      clickEffect(event.getTarget, act)
      touched
    }
  }

  private def clickEffect(actor: Actor, f: () => Unit): Unit = {
    actor.addAction(
      parallel(
        sequence(
          color(Color.GRAY, .15f),
          color(Color.WHITE, .15f)
        ),
        sequence(
          scaleBy(-.25f, -.25f, .15f),
          scaleTo(1f, 1f, .15f),
          delay(.15f),
          run(runnable(f))
        )
      )
    )
  }

  private lazy val gtable = new GameTable
  private lazy val stable = new StartTable(uiClickListener)
  private lazy val dtable = new DashboardTable(uiClickListener)

  private var currAct: Float => Unit = emptyAct
  private lazy val (screenSideR, screenSideL) = getScreenSideRects

  private var cTouchDown: (Int, Int, Int, Int) => Boolean = super.touchDown
  private var cKeyDown: (Int) => Boolean = super.keyDown

  RxMgr.onGameState
      .filter(s => s == Const.GameStatePlay || s == Const.GameStateOver)
      .map(_ == Const.GameStatePlay)
    .subscribe(r => handleGame(r))

  def init(): Unit = {
    clear()

    stable.init()
    gtable.init()
    dtable.init()

    addActor(stable)
    stable.show()
  }

  private def showGameTable(): Unit = {
    stable.hide()
    addGameTbl()
  }

  private def playAgain(): Unit = {
    dtable.hideAndFunc {
      () => RxMgr.onGameState.onNext(Const.GameStatePlay)
    }
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
      post(() => showDashboadTbl())
  }

  private def showDashboadTbl(): Unit = {
    gtable.hide()
    addDashboardTbl()
  }

  private def getScreenSideRects = {
    val scoreHeight = 48
    (new Rectangle(Gdx.graphics.getWidth / 2, scoreHeight,
      Gdx.graphics.getWidth / 2,
      Gdx.graphics.getHeight - scoreHeight),
     new Rectangle(0, scoreHeight,
      Gdx.graphics.getWidth / 2,
      Gdx.graphics.getHeight - scoreHeight))
  }

  override def keyDown(keyCode: Int): Boolean = {
    cKeyDown(keyCode)
  }

  private def configGameInput(running: Boolean): Unit = {
    cTouchDown = if (running) runningTouchDown else super.touchDown
    cKeyDown = if (running) runningKeyDown else super.keyDown
  }

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    cTouchDown(screenX, screenY, pointer, button)
  }

  private def runningKeyDown(keyCode: Int): Boolean = {
    RxMgr.onPlayerAction.onNext(keyCode)
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
    false
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
  }
}
