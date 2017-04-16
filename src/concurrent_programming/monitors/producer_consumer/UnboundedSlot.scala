package concurrent_programming.monitors.producer_consumer

import io.threadcso.Monitor

import scala.collection.mutable

class UnboundedSlot[T] extends Slot[T] {
  private [this] val queue = new mutable.Queue[T]()
  private [this] val monitor = new Monitor
  private [this] val isNonEmpty = monitor.newCondition

  override def put(v: T): Unit = monitor withLock{
    queue += v
    isNonEmpty.signal()
  }

  override def get: T = {
    while (queue.isEmpty) isNonEmpty.signal()
    queue.dequeue()
  }
}
