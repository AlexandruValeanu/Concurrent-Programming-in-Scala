package concurrent_programming.monitors.producer_consumer

import io.threadcso.Monitor

import scala.collection.mutable

class BoundedSlot[T](val bufSize: Int) extends Slot[T] {
  private [this] val monitor = new Monitor
  private [this] val hasSpace, isNonEmpty = monitor.newCondition
  private [this] val queue = new mutable.Queue[T]()

  override def put(v: T): Unit = monitor withLock {
    while (queue.size == bufSize) hasSpace.await()
    queue += v
    isNonEmpty.signal()
  }

  override def get: T = monitor withLock {
    while (queue.isEmpty) isNonEmpty.await()
    val v = queue.dequeue()
    hasSpace.signal()
    v
  }
}
