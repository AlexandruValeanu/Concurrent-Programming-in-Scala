package concurrent_programming.monitors.memory_allocation

import io.threadcso.monitors.FairMonitor

/*
  Monitor-based

  Our first solution uses the sequential data structure safely. It satisfies requests in first-come
  first-served order as far as is possible because it uses a fair monitor for locking.
 */
class Allocator1(admin: PageAdmin) extends Allocator{
  private [this] val monitor = new FairMonitor()
  private [this] val waiting = monitor.newCondition

  override def request(count: Int): Set[Int] = monitor withLock{
    while (!admin.hasAvailable(count)) waiting.await()
    admin.request(count)
  }

  override def release(pages: Set[Int]): Unit = monitor withLock{
    admin.release(pages)
    waiting.signalAll()
  }
}
