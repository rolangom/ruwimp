package com.tagor.ras.models.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.{Actor, InputEvent, Touchable}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.{Cell, Image, Label, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.tagor.ras.models.{PlayerStoreItem, Showable}
import com.tagor.ras.utils._

/**
  * Created by rolangom on 7/4/16.
  */
class StoreTable(clickListener: ClickListener) extends Table with Showable {

  private val itemClickListener = new ClickListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      val regionn: Int = String.valueOf(event.getTarget.getUserObject).toInt
      event.getTarget.addAction(clickEffect(() => { }))
      onPlayerSelected(regionn)
      true
    }
  }

  private def currPlayer = PrefMgr.currPlayer
  private var currPlayerCell: Cell[Image] = _

  private def init(): Unit = {
    reset()

    setFillParent(true)
    align(Align.center)

    val goHomeImg = new Image(ResMgr.getRegion(Const.BGS_PATH, "home_btn"))
    goHomeImg.setOrigin(Align.center)
    goHomeImg.setUserObject(Const.ExitStoreStr)
    goHomeImg.addListener(clickListener)
    goHomeImg.setTouchable(Touchable.enabled)

    val generator = new FreeTypeFontGenerator(Gdx.files.internal(Const.CurrFont))
    val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = 48
    val font = generator.generateFont(parameter)
    val labelStyle = new Label.LabelStyle(font, Color.valueOf(BlockConst.Red))
    val storeLbl = new Label("Store", labelStyle)
    generator.dispose()

    add(goHomeImg).pad(12).center()
    add(storeLbl).pad(12).center().colspan(2)
    currPlayerCell = add(newImagePlayer()).pad(12).maxSize(Const.ItemStoreSize).center()
    row()

    for (i <- 0 to 7)
      addActor(new PlayerStoreItem("5000055", "AsdF asdf", String.valueOf(i), itemClickListener), i)

    setVisible(false)
  }
  init()

  private def onPlayerSelected(n: Int): Unit = {
    RxMgr.playerRegionStream.onNext(n)
    currPlayerCell.setActor(newImagePlayer(n))
    PrefMgr.currPlayer = n
  }

  private def newImagePlayer(n: Int = currPlayer): Image = new Image(ResMgr.getRegion(Const.BGS_PATH, s"player_${n}_falling"))

  private def addActor(actor: Actor, index: Int): Unit = {
    add(actor).pad(21)
    if (index == 3)
      row()
  }

  override def show(): Unit = {
    clearActions()
    addAction(
      sequence(
        alpha(0),
        Actions.visible(true),
        fadeIn(1f)
      )
    )
  }

  override def hide(): Unit = {
    hideAndFunc(() => ())
  }

  override def hideAndFunc(f: () => Unit): Unit = {
    clearActions()
    addAction(
      sequence(
        fadeOut(.5f),
        Actions.visible(false),
        Actions.removeActor(),
        run(runnable(f))
      )
    )
  }
}
