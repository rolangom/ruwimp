package com.tagor.ras.stages

import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.tagor.ras.models.BottomBgActor
import com.tagor.ras.utils._

/**
  * Created by rolangom on 2/13/16.
  */
class BgStage(batch: Batch)
  extends Stage(new StretchViewport(
    Const.Width, Const.Height,
    new OrthographicCamera), batch) with Disposable {

  private var bga: BottomBgActor = _

  RxMgr.newTheme
    .subscribe(_ => invalidate())

  def init(): Unit = {
    bga = new BottomBgActor().init()
    addActor(bga)
    invalidate()
  }

  override def dispose(): Unit = {
    bga.remove()
  }

  private def invalidate(): Unit = {
    bga.setColor(Color.valueOf(ThemeMgr.getBgColorStr(BlockConst.BG0_COLOR_INDEX)))
  }
}
