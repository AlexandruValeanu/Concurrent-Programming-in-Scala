package concurrent_programming.monitors.memory_allocation

import io.threadcso.Condition
import io.threadcso.semaphore.{BooleanSemaphore, Flag}

import scala.collection.mutable

/*
  Semaphore-based and prioritized

  Conditions are themselves implemented as queues of waiting processes.

  There will never be more than one waiting process per condition; and that condition will
  be signalled exactly once.

  A more elegant solution uses a Flag or signalling-Semaphore to "stall" a thread
  making an unsatisfiable request.
 */

class Allocator3(admin: PageAdmin) extends Allocator{
  case class Request(count: Int, condition: Flag)
  private val orderByCount = new Ordering[Request] {
    override def compare(x: Request, y: Request): Int = -x.count.compareTo(y.count) // reverse order
  }

  private [this] val waiting = mutable.PriorityQueue.empty[Request](orderByCount)
  private [this] val mutex = new BooleanSemaphore(available = true)

  override def request(count: Int): Set[Int] = {
    mutex.acquire()

    if (!admin.hasAvailable(count)){
      val blocked = new Flag()
      waiting.enqueue(Request(count, blocked))
      mutex.release()
      blocked.acquire()
    }

    val res = admin.request(count)

    if (waiting.nonEmpty && admin.hasAvailable(waiting.head.count))
      waiting.dequeue().condition.release()
    else
      mutex.release()

    res
  }

  /*
  This method puts the mutex down, releases the pages, then starts the "cascade" of
  attempts to satisfy satisfiable requests from the front of the queue of waiting requests.
   */
  override def release(pages: Set[Int]): Unit = {
    mutex.acquire()
    admin.release(pages)

    // So signal the first waiting request if it's satisfiable (it will signal its successor)
    if (waiting.nonEmpty && admin.hasAvailable(waiting.head.count))
      waiting.dequeue().condition.release()
    else
      mutex.release()
  }
}
