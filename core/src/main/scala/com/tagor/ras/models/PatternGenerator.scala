package com.tagor.ras.models

import com.badlogic.gdx.math.MathUtils
import com.tagor.ras.utils.{Const, RxMgr, BlockPooler, BlockConst}
import BlockConst._

/**
  * Created by rolangom on 12/18/15.
  */
class PatternGenerator(pooler: BlockPooler) {

  private def randQty: Int = MathUtils.random(2, 4)
  private val vspan = BlockConst.Sizes(BlockConst.SizeS) * BlockConst.Size

  private def randDimen = MathUtils.random(0, Dimens.length - 1)
  private def randSize = MathUtils.random(0, Sizes.length - 1)

  private def wAng(w: Float, a: Float): Float =
    w * MathUtils.cosDeg(a)

  private def hAng(w: Float, a: Float): Float =
    w * MathUtils.sinDeg(a)

  private var level = 0

  private def randAng =
    if (level == 0) Angles(0) else MathUtils.random(level)

  private def isDimenUp(dimen: Int): Boolean = dimen == DimenUp

  private def angSign(dimen: Int): Int =
    if (isDimenUp(dimen)) 1 else -1

  private val hsh = Const.Height / 2 // half screen height

  def genSeqX(x: Float,
              y: Float,
              c: Int = randQty,
              ang: Float = randAng,
              space: Float = vspan,
              size: Int = randSize,
              wlast: Boolean = true): Float = {

    var i = 0
    var xAcc = x
    val hwAng = wAng(Width(DimenUp, size), ang) * .5f
    val subs = RxMgr.onItiAdded
    val ny = hsh
    val hsd =  hwAng * (1 - Const.DownScale)

    while (i < c) {
      xAcc += space + hwAng

      val b1 = pooler.getIti.init(DimenUp, size,
        xAcc, ny, ang)
      val b2 = pooler.getIti.init(DimenDown, size,
        xAcc - hsd, ny, -ang,
        isLast = wlast && i == c - 1)

      subs.onNext(b1)
      subs.onNext(b2)

      xAcc += hwAng
      i += 1
    }
    xAcc
  }

  def genLinearSeq(x: Float,
                   y: Float,
                   c: Int = randQty,
                   ang: Float = randAng,
                   dimen: Int = randDimen,
                   size: Int = randSize,
                   space: Float = vspan,
                   wlast: Boolean = true): Float = {
    var i = 0
    val rang = ang * angSign(dimen)
    val w = Width(dimen, size)
    val hwAng = wAng(w, rang) * .5f
    var xAcc = x
    val subs = RxMgr.onItiAdded
    val ny = hsh

    while (i < c) {
      xAcc += space + hwAng
      val b = pooler.getIti.init(
        dimen,
        size,
        xAcc, // x
        ny,
        rang,
        isLast = wlast && i == c - 1
      )
      println(s"iti on genLinearSeq $b")
      subs.onNext(b)
      xAcc += hwAng
      i += 1
    }
    xAcc
  }

  def genSeqLT(x: Float,
               y: Float,
               c: Int = randQty,
               ang: Float = randAng,
               size: Int = randSize,
               space: Float = vspan,
               wlast: Boolean = true): Float =
    genSeqLTorGT(x, y, c, ang, size, space, true, wlast)

  def genSeqGT(x: Float,
               y: Float,
               c: Int = randQty,
               ang: Float = randAng,
               size: Int = randSize,
               space: Float = vspan,
               wlast: Boolean = true): Float =
    genSeqLTorGT(x, y, c, ang, size, space, false, wlast)

  private def genSeqLTorGT(x: Float,
                   y: Float,
                   c: Int = randQty,
                   ang: Float = randAng,
                   size: Int = randSize,
                   space: Float = vspan,
                   isLt: Boolean = true,
                   wlast: Boolean = true): Float = {
    var i = 0
    val w = Width(DimenUp, size)
    val iltSign = if (isLt) 1 else -1
    val hwAng = wAng(w, ang) * .5f
    val hhAng = (hAng(w, ang) * .5f) * iltSign
    var xAcc = x
    val subs = RxMgr.onItiAdded
    val ny = hsh

    while (i < c) {
      xAcc += space + hwAng
      val b1 = pooler.getIti.init(DimenUp, size,
        xAcc, ny + hhAng, ang)
      val b2 = pooler.getIti.init(DimenDown, size,
        xAcc, ny - hhAng, - ang,
        isLast = wlast && i == c - 1)
      subs.onNext(b1)
      subs.onNext(b2)
      xAcc += hwAng
      i += 1
    }
    xAcc
  }

  def genDiamond(x: Float,
                  y: Float): Float = {
    val nx = genSeqLT(x, y, 1, wlast = false)
    genSeqGT(nx, y, 1)
  }

  def genGtAndLt(x: Float,
                 y: Float): Float = {
    val nx = genSeqGT(x, y, 1, wlast = false)
    genSeqLT(nx, y, 1)
  }

  def genSeqVandInvV(x: Float,
                     y: Float): Float = {
    val nx = genSeqV(x, y, 1, wlast = false)
    genSeqInvV(nx, y, 1)
  }

  def genSeqInvVandV(x: Float,
                     y: Float): Float = {
    val nx = genSeqInvV(x, y, 1, wlast = false)
    genSeqV(nx, y, 1)
  }

  def genSeqV(x: Float,
              y: Float,
              c: Int = randQty,
              ang: Float = randAng,
              size: Int = randSize,
              space: Float = vspan,
              wlast: Boolean = true): Float =
    genDimenABnSizeCC(x, y, true, c, ang, size, space, wlast)

  def genSeqInvV(x: Float,
              y: Float,
              c: Int = randQty,
              ang: Float = randAng,
              size: Int = randSize,
              space: Float = vspan,
              wlast: Boolean = true): Float =
    genDimenABnSizeCC(x, y, false, c, ang, size, space, wlast)

  private def genDimenABnSizeCC(x: Float,
                        y: Float,
                        inverse: Boolean = true,
                        c: Int = randQty,
                        ang: Float = randAng,
                        size: Int = randSize,
                        space: Float = vspan,
                        wlast: Boolean = true): Float = {

    var i = 0
    val w = Width(DimenUp, size)
    val hwAng = wAng(w, ang) * .5f
    var xAcc = x
    val subs = RxMgr.onItiAdded
    val (msign, d1, d2) = if (inverse) (-1, DimenDown, DimenUp) else (1, DimenUp, DimenDown)
//    val ny = hsh + (msign * hAng(w, ang) * .5f)
    val ny = hsh - hAng(w, ang) * .5f
    val hsd =  hwAng * Const.DownScale

    while (i < c) {
      xAcc += space + hsd
      val b1 = pooler.getIti.init(d1, size,
        xAcc, ny, ang * msign)

      xAcc += hsd + space + hwAng
      val b2 = pooler.getIti.init(d2, size,
        xAcc, ny, - ang * msign,
        isLast = wlast && i == c - 1)

      subs.onNext(b1)
      subs.onNext(b2)

      xAcc += hwAng
      i += 1
    }
    xAcc
  }
}
