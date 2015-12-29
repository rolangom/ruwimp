package com.tagor.ras

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.{Pools, Pool}

/**
  * Created by rolangom on 12/16/15.
  */
package object utils {

  private val rPool = Pools.get(classOf[RasRunnable])

  def post(f: () => Unit): Unit = {
    Gdx.app.postRunnable(rPool.obtain().init(f))
  }

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
}
