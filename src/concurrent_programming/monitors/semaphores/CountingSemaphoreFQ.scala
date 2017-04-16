package concurrent_programming.monitors.semaphores

import io.threadcso.Semaphore
import io.threadcso.semaphore.{BooleanSemaphore, Flag}

/*
(a) Show how to implement a class CountingSemaphoreFQ(private var available: Int) using a queue of Flags and a mutex
to enforce atomicity. Your implementation should guarantee to satisfy acquire requests strictly in first-come
first-served order.
You may assume that the stock iothreadcsoBooleanSemaphores satisfy acquires in first-come first-served order when
their fair parameter is true.
 */
class CountingSemaphoreFQ(private var available: Int) extends Semaphore{
  private val mutex = new BooleanSemaphore(available = true, fair = true)
  private val queue = new scala.collection.mutable.Queue[Flag]()
  // (when mutex is up)
  // invariant: queue:size = original (available) + #release - #acquire
  // queue contains a Flag for each waiting acquirers

  def acquire(): Unit ={
    mutex.acquire()

    if (available > 0){
      available -= 1
      mutex.release()
    }
    else{
      val gate = new Flag()
      queue.enqueue(gate)
      mutex.release()
      gate.acquire()
    }
  }

  def release(): Unit ={
    mutex.acquire()

    if (queue.isEmpty)
      available += 1
    else{
      val gate = queue.dequeue()
      gate.release()
    }

    mutex.release()
  }
}
