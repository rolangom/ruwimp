package rx.scala.concurrency

import rx.lang.scala.Scheduler

/**
  * Created by rolangom on 12/15/15.
  */
object GdxScheduler {

  def apply(): GdxScheduler =  {
    new GdxScheduler(rx.concurrency.GdxScheduler.get())
  }
}

class GdxScheduler private[scala] (val asJavaScheduler: rx.Scheduler)
  extends Scheduler {}

