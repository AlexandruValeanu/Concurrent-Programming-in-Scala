package concurrent_programming.monitors.readers_writers

/*
Our first implementation keeps track of the number of readers and writers that are using
the database and maintains the invariant: (readers = 0 and writers <=1) or writers = 0
 */
class FairController extends ReadWriteController{
  private var readers, writers, waitingWriters = 0

  override protected def startRead(): Unit = synchronized{
    while (writers != 0 || waitingWriters != 0) wait()
    readers += 1
  }

  override protected def startWrite(): Unit = synchronized{
    waitingWriters += 1
    while (readers != 0 || writers != 0) wait()
    writers = 1
    waitingWriters -= 1
  }

  override protected def endRead(): Unit = synchronized{
    readers -= 1
    notifyAll()
  }

  override protected def endWrite(): Unit = synchronized{
    writers = 0
    notifyAll()
  }
}
