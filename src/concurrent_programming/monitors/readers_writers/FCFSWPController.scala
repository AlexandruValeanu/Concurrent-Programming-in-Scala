package concurrent_programming.monitors.readers_writers

import io.threadcso.monitors.FairMonitor

class FCFSWPController extends ReadWriteController{
  private [this] val monitor = new FairMonitor()
  private [this] val readable, writable = monitor.newCondition

  private var readers, writers, waitingWriters = 0

  override protected def startRead(): Unit = monitor withLock {
    while (writers != 0 || waitingWriters != 0) readable.await()
    readers += 1
  }

  override protected def startWrite(): Unit = monitor withLock {
    waitingWriters += 1
    while (writers != 0 || readers != 0) writable.await()
    writers = 1
    waitingWriters -= 1
  }

  override protected def endRead(): Unit = monitor withLock {
    readers -= 1
    chooseRunnable()
  }

  override protected def endWrite(): Unit = monitor withLock {
    writers = 0
    chooseRunnable()
  }

  def chooseRunnable(): Unit = {
    if (waitingWriters != 0 && readers == 0)
      writable.signal()
    else
      readable.signalAll()
  }
}
