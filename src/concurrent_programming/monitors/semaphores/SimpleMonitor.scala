package concurrent_programming.monitors.semaphores

import java.util.concurrent.locks.Condition

import io.threadcso.Monitor
import io.threadcso.semaphore.BooleanSemaphore

/*
  We demonstrate how a form of monitor can be implemented from semaphores

 In this implementation we assume (without checking) that processes calling signal and
await on conditions of the monitor own the monitor's lock.
 We use the stock CSO BooleanSemaphore to implement both locking and "gating".
 We could have used Flags to implement the queues in which processes awaiting conditions
are stalled.
 */
class SimpleMonitor extends Monitor{
  private [this] val lock = new BooleanSemaphore(available = true)

  override def withLock[T](body: => T): T = {
    lock.acquire()
    try {
      body
    }
    finally {
      lock.release()
    }
  }

  override def newCondition: Condition = new SimpleCondition(lock)
}
