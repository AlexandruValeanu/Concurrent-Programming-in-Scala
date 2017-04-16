package concurrent_programming.monitors.semaphores

/* inefficient implementation */
class BinarySemaphore(private [this] var available: Boolean = true) extends
  io.threadcso.semaphore.Semaphore {

  override def acquire(): Unit = synchronized{
    while (!available) wait()
    available = false
  }

  override def release(): Unit = synchronized{
    available = true
    notify()
  }
}
