package concurrent_programming.monitors.h2o

import io.threadcso._

object TestH2O {
  def main(args: Array[String]): Unit = {
    val N = 1000

    var procsH = proc{}
    var procsO = proc{}

    for (_ <- 0.until(2 * N))
      procsH = procsH || proc{H2OSEM.H()}

    for (_ <- 0.until(N))
      procsO = procsO || proc{H2OSEM.O()}

    run(procsH || procsO)
    println("Test finished!")
  }
}
