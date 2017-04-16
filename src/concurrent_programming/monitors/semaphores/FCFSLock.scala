package concurrent_programming.monitors.semaphores

import io.threadcso.locks.Lock
import io.threadcso.semaphore.{BooleanSemaphore, Flag}

class FCFSLock extends Lock{
  private var locked = false
  private val mutex = new BooleanSemaphore(available = true)
  private val queue = scala.collection.mutable.Queue[Flag]()

  override def lock(): Unit = {
    mutex.acquire()

    if (!locked){
      locked = true
      mutex.release()
    }
    else{
      val gate = new Flag()
      queue.enqueue(gate)
      mutex.release()
      gate.acquire()
      locked = true
    }
  }

  override def unlock(): Unit = {
    mutex.acquire()

    if (queue.isEmpty){
      locked = false
    }
    else{
      val gate = queue.dequeue()
      gate.release()
    }

    mutex.release()
  }
}
