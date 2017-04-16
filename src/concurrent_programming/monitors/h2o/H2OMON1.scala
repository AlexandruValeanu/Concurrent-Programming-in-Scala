package concurrent_programming.monitors.h2o

import io.threadcso.Monitor

/*
Implement H2O using a CSO Monitor with only a single condition variable. This should involve little more
than a transcription of H2OJVM to use withLock, await, signallAll.
 */
object H2OMON1 extends Monitor with H2O{
  private var hs = 0 // count of hydrogens
  private var os = 0 // count of unbonded oxygens
  private var hb = 0 // hydrogens allowed to bond

  private val monitor = new Monitor
  private val condition = monitor.newCondition

  override def H(): Unit = monitor withLock{
    hs += 1 // another hydrogen is present
    condition.signalAll() // inform all that another hydrogen is present
    while (hb == 0) condition.await() // wait until a hydrogen is allowed to bond
    hs -= 1; hb -= 1 // form the bond and remove the hydrogen
  }

  override def O(): Unit = monitor withLock{
    os += 1
    while (!(hs - hb >= 2 && os >= 1)) condition.await() // wait for a possible bonding
    os -= 1 // remove the oxygen atom
    hb += 2 // allow two more hydrogens to bond
    condition.signalAll()
  }
}
