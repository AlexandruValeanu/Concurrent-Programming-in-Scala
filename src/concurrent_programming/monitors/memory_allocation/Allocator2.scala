package concurrent_programming.monitors.memory_allocation

import io.threadcso.Condition
import io.threadcso.monitors.FairMonitor

import scala.collection.mutable

/*
  Monitor-based and prioritized

 Our second solution stores waiting requests in a priority queue ordered by increasing count.
 Each waiting request awaits its own distinct condition.
 Invariant: no request in the queue can currently be satisfied from admin.
 */
class Allocator2(admin: PageAdmin) extends Allocator{
  case class Request(count: Int, condition: Condition)
  private val orderByCount = new Ordering[Request] {
    override def compare(x: Request, y: Request): Int = -x.count.compareTo(y.count) // reverse order
  }

  private [this] val monitor = new FairMonitor()
  private [this] val waiting = mutable.PriorityQueue.empty[Request](orderByCount)

  override def request(count: Int): Set[Int] = monitor withLock{
    while (!admin.hasAvailable(count)){
      val blocked = monitor.newCondition
      waiting.enqueue(Request(count, blocked))
      blocked.await()
    }

    val res = admin.request(count)

    // So signal the first waiting request if it's satisfiable (it will signal its successor)
    if (waiting.nonEmpty && admin.hasAvailable(waiting.head.count))
      waiting.dequeue().condition.signal()

    res
  }

  override def release(pages: Set[Int]): Unit = {
    admin.release(pages)

    // So signal the first waiting request if it's satisfiable (it will signal its successor)
    if (waiting.nonEmpty && admin.hasAvailable(waiting.head.count))
      waiting.dequeue().condition.signal()
  }
}
