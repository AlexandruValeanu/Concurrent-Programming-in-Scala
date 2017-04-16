package concurrent_programming.monitors.readers_writers

/*
The Readers and Writers Problem

Consider a collection of processes running concurrently, each of which needs to access a
database some for reading and some for writing.

It is allowable for several readers to be accessing the database simultaneously, since their
transactions will be independent.

But it is not allowable for two writers to have simultaneous access, nor for a reader and
writer to have simultaneous access.
 */
trait ReadWriteController {

  def Read (readOperations: => Unit): Unit = {
    startRead()
    try {
      readOperations
    } finally {
      endRead()
    }
  }

  def Write(writeOperations: => Unit){
    startWrite()
    try {
      writeOperations
    } finally {
      endWrite()
    }
  }

  protected def startRead(): Unit
  protected def startWrite(): Unit
  protected def endRead(): Unit
  protected def endWrite(): Unit
}
