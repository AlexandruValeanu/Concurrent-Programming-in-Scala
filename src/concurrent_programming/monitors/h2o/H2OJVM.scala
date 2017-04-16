package concurrent_programming.monitors.h2o

object H2OJVM extends H2O{
  private var hs = 0 // count of hydrogens
  private var os = 0 // count of unbonded oxygens
  private var hb = 0 // hydrogens allowed to bond

  override def H(): Unit = synchronized{
    hs += 1 // another hydrogen is present
    notifyAll() // inform all that another hydrogen is present
    while (hb == 0) wait() // wait until a hydrogen is allowed to bond
    hs -= 1; hb -= 1 // form the bond and remove the hydrogen
  }

  override def O(): Unit = synchronized{
    os += 1
    while (!(hs - hb >= 2 && os >= 1)) wait() // wait for a possible bonding
    os -= 1 // remove the oxygen atom
    hb += 2 // allow two more hydrogens to bond
    notifyAll()
  }
}
