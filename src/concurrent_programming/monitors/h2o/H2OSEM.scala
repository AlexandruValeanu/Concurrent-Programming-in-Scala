package concurrent_programming.monitors.h2o

import io.threadcso.semaphore.{BooleanSemaphore, CountingSemaphore}

/*
Show how to implement H2O using Counting Semaphores.
 */
object H2OSEM extends H2O{
  private [this] val OS = new CountingSemaphore(available = 0)
  private [this] val HS = new CountingSemaphore(available = 0)
  // OS counts ‘‘oxygen permissions’’: O needs two permissions to proceed
  // HS counts ‘‘hydrogen permissions’’

  private [this] val mutex = new BooleanSemaphore(available = true)

  override def O(): Unit = {
    mutex.acquire()
      OS.acquire()
      OS.acquire()
      HS.release()
      HS.release()
    mutex.release()
  }

  override def H(): Unit = {
    OS.release()
    HS.acquire()
  }
}
