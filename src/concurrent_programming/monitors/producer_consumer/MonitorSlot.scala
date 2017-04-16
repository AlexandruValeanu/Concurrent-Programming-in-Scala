package concurrent_programming.monitors.producer_consumer

import io.threadcso.Monitor

class MonitorSlot[T] extends Slot[T]{
  private [this] val monitor = new Monitor
  private [this] var value: T = _
  private [this] var empty = true
  private [this] val isEmpty, isNonEmpty = monitor.newCondition


  override def put(v: T): Unit = monitor withLock {
    while (!empty) isEmpty.await()
    value = v
    empty = false
    isNonEmpty.signal()
  }

  override def get: T = monitor withLock {
    while (empty) isNonEmpty.await()
    val result = value
    empty = true
    isEmpty.signal()
    result
  }
}
