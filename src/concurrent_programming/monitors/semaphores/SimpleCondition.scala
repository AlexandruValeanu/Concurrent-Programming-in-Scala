package concurrent_programming.monitors.semaphores

import java.util.Date
import java.util.concurrent.TimeUnit

import io.threadcso.Condition
import io.threadcso.semaphore.BooleanSemaphore


class SimpleCondition(lock: BooleanSemaphore) extends Condition{
  private [this] val queue = new scala.collection.mutable.Queue[BooleanSemaphore]

  override def await(): Unit = { // lock should be owned by the awaiting process
    val gate = new BooleanSemaphore(available = true)
    queue.enqueue(gate)
    lock.release // give up the lock
    gate.acquire() // wait this process’s turn
    lock.acquire() // await returns in the locked state
  }

  override def signal(): Unit = { // lock should be owned by the signalling process
    if (queue.nonEmpty)
      queue.dequeue().release()
  }



  override def signalAll(): Unit = ???

  override def awaitNanos(nanosTimeout: Long): Long = ???

  override def awaitUninterruptibly(): Unit = ???

  override def await(time: Long, unit: TimeUnit): Boolean = ???

  override def awaitUntil(deadline: Date): Boolean = ???
}
