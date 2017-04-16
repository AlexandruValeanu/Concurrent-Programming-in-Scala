package concurrent_programming.monitors.producer_consumer

import io.threadcso.semaphore.{BooleanSemaphore, CountingSemaphore}

// A bounded-buffered multi-producer/multi-consumer slot
class BufferedSlot[T](size: Int) extends Slot[T]{
  private [this] val buffer = new scala.collection.mutable.Queue[T]()
  private [this] val space = new CountingSemaphore(available = size)
  private [this] val datum = new CountingSemaphore(available = 0)
  private [this] val mutex = new BooleanSemaphore(available = true)

  // invariant: space.count + datum.available = size ^ (buffer.size = datum.available)

  override def put(v: T): Unit = {
    space.acquire()
    mutex.acquire()
      buffer.enqueue(v)
      datum.release()
    mutex.release()
  }

  override def get: T = {
    datum.acquire()
    mutex.acquire()
      val v = buffer.dequeue()
      space.release()
    mutex.release()
    v
  }
}
