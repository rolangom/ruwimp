package com.tagor.ras

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.{Action, Actor, Touchable}
import com.badlogic.gdx.utils.{Pool, Pools}

/**
  * Created by rolangom on 12/16/15.
  */
package object utils {

  private val rPool = Pools.get(classOf[RasRunnable])

  def post(f: () => Unit): Unit =
    Gdx.app.postRunnable(rPool.obtain().init(f))

  def runnable(f: () => Unit): Runnable =
    rPool.obtain().init(f)

  private class RasRunnable
    extends Runnable
    with Pool.Poolable {

    private var _f: () => Unit = () => ()
    def init(f: () => Unit): RasRunnable = {
      _f = f
      this
    }
    override def run(): Unit = {
      _f()
      rPool.free(this)
    }
    override def reset(): Unit = {
      _f = () => ()
    }
  }

  def enableTouchable(enabled: Boolean, actors: Actor*): Unit = {
    val tenabled = if (enabled) Touchable.enabled else Touchable.disabled
    actors.foreach(_.setTouchable(tenabled))
  }

  def wAng(w: Float, a: Float): Float = w * MathUtils.cosDeg(a)
  def hAng(w: Float, a: Float): Float = w * MathUtils.sinDeg(a)


  def clickEffect(f: () => Unit): Action = {
    SoundMgr.playJump(1f, 1f, 0f)
    parallel(
      sequence(
        color(Color.GRAY, .15f),
        color(Color.WHITE, .15f)
      ),
      sequence(
        scaleBy(-.15f, -.15f, .15f),
        scaleTo(1f, 1f, .15f),
        //        delay(.15f),
        run(runnable(f))
      )
    )
  }
}
