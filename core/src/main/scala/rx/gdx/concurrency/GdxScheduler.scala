package rx.gdx.concurrency

import java.util.concurrent.atomic.AtomicBoolean

import com.badlogic.gdx.Gdx
import rx.lang.scala.{Subscription, Worker, Scheduler}

import scala.concurrent.duration.Duration
import rx.lang.scala.JavaConversions._


/**
  * Created by rolangom on 11/22/15.
  */
object GdxScheduler extends GdxScheduler

class GdxScheduler private() extends Scheduler {

  override val asJavaScheduler: rx.Scheduler = this

  override def createWorker: Worker = worker

  private val worker = new Worker {
    override val asJavaWorker: rx.Scheduler.Worker = this

    @volatile var mIsUnsubscribed:Boolean = _
    override def schedule(action: => Unit): Subscription = {
      if(mIsUnsubscribed)
        return Subscription()

      val runAb = new AtomicBoolean(true)
      Gdx.app.postRunnable(new Runnable {
        override def run(): Unit = {
          if(!mIsUnsubscribed && runAb.get()) action
        }
      })

      Subscription(runAb.set(false))
    }

    override def schedule(delay: Duration)(action: => Unit): Subscription = {
      if(mIsUnsubscribed)
        return Subscription()

      val runAb = new AtomicBoolean(true)

      val delayInMillis = delay.toMillis
      if(delayInMillis < 0)
        throw new IllegalArgumentException("delay may not be negative (in milliseconds): " + delayInMillis);

      val sleeper = new Thread(new Runnable {
        override def run(): Unit = {
          try{
            Thread.sleep(delayInMillis)
            if(!mIsUnsubscribed && runAb.get()) schedule(action)
          }catch {
            case e:InterruptedException => runAb.set(false)
          }
        }
      }, "gdx-scheduler-sleeper")

      Subscription(runAb.set(false))
    }

    override def unsubscribe(): Unit = mIsUnsubscribed = true

    override def isUnsubscribed: Boolean = mIsUnsubscribed
  }
}

