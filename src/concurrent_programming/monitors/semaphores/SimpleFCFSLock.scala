package concurrent_programming.monitors.semaphores

import io.threadcso.locks.Lock
import io.threadcso.semaphore.BooleanSemaphore

/*
It will not have escaped your attention that we are using stock CSO boolean semaphores to implement mutexes
with FIFO behaviour. As it happens, our stock boolean semaphores can be constructed to be fair (they have a fair
boolean parameter). Show how the implementation of FCFSLock can be simplified.
 */
class SimpleFCFSLock extends Lock{
  private [this] val sema = new BooleanSemaphore(available = true, fair = true)

  override def lock(): Unit = sema.acquire()

  override def unlock(): Unit = sema.release()
}
