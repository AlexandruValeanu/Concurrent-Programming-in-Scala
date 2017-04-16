package concurrent_programming.monitors.h2o

import io.threadcso.Monitor

/*
Noticing that in-effect "hydrogens signal only to oxygens, and oxygens signal only to hydrogens", modify
your solution to 1 to make a more ecient H2O implementation that doesn't use signalAll.
You may use an additional condition variable.
 */
object H2OMON2 extends Monitor with H2O{
  private var hs = 0 // count of hydrogens
  private var os = 0 // count of unbonded oxygens
  private var hb = 0 // hydrogens allowed to bond

  private val monitor = new Monitor
  private val OS, HS = monitor.newCondition

  override def H(): Unit = monitor withLock{
    hs += 1 // another hydrogen is present
    OS.signal() // inform some oxygen that another hydrogen is present
    while (hb == 0) HS.await() // wait until a hydrogen is allowed to bond
    hb -= 1; hs -= 1 // form the bond and remove the hydrogen
  }

  override def O(): Unit = monitor withLock{
    os += 1
    while (!(hs - hb >= 2 && os >= 1)) OS.await() // wait for a possible bonding
    hb += 2 // allow two more hydrogens to bond
    os -= 1 // remove the oxygen atom
    HS.signal() // signal to some hydrogen
    HS.signal() // signal to other hydrogen
  }
}
