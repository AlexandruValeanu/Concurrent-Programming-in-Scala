package concurrent_programming.monitors.semaphores

import io.threadcso.Semaphore
import io.threadcso.semaphore.BooleanSemaphore

/*
[Challenging] Given the above assumption show how the abovementioned queue can eectively be implemented by a single fair
iothreadcsoBooleanSemaphore. Do so by by implementing a dierent class CountingSemaphoreSQ(private var available: Int).
You will still need the mutex of part (a), and may use other (scalar) auxiliary variables.
One race condition that can arise unless care is taken to avoid it happens when a release is invoked
 */
class CountingSemaphoreSQ(private var available: Int) extends Semaphore{
  private val mutex = new BooleanSemaphore(available = true, fair = true)
  private val gate = new BooleanSemaphore(available = true, fair = true)
  private var deficit = 0

  override def acquire(): Unit = {
    mutex.acquire()

    if (available > 0){
      available -= 1
      mutex.release()
    }
    else{
      deficit += 1
      mutex.release()
      gate.acquire()
    }
  }

  override def release(): Unit = {
    mutex.acquire()

    if (deficit > 0){
      deficit -= 1
      gate.release()
    }
    else
      available += 1

    mutex.release()
  }
}
