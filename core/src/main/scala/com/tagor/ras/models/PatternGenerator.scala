package com.tagor.ras.models

import com.badlogic.gdx.math.MathUtils
import com.tagor.ras.utils.{RxMgr, BlockPooler, BlockConst}

/**
  * Created by rolangom on 12/18/15.
  */
class PatternGenerator(pooler: BlockPooler) {

  private def randQty: Int = MathUtils.random(2, 4)

  def initDefaultsIti(x: Float,
                      y: Float,
                      count: Int = randQty): Unit = {
    var i = 0
    val ang = MathUtils.random(10f, 40f)
    val subs = RxMgr.onItiAdded
    while (i < count) {
      val b1 = pooler.getIti.init(BlockConst.DimenUp, BlockConst.SizeL,
        x + BlockConst.Width(BlockConst.DimenUp, BlockConst.SizeL) * i, y, ang)
      val b2 = pooler.getIti.init(BlockConst.DimenDown, BlockConst.SizeL,
        x + BlockConst.Width(BlockConst.DimenDown, BlockConst.SizeL) * i, y, -ang,
        isLast = i == count - 1)

      println(s"b2 is last? ${b2.isLast}")

      subs.onNext(b1)
      subs.onNext(b2)

      i += 1
    }
  }
}
