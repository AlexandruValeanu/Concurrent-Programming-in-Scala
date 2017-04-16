package concurrent_programming.monitors.producer_consumer

import io.threadcso.{PROC, proc, run}

object TestSlot {
  def main(args: Array[String]): Unit = {
    testMultiSlot()
  }

  def testMultiSlot(): Unit = {
    val bufffer = new BufferedSlot[Int](5)
    val n = 100
    var runs: PROC = proc{}

    0.to(2 * n).foreach(i => runs = runs || proc(bufffer.put(i)))
    0.to(n).foreach(_ => runs = runs || proc{val v = bufffer.get; println(v)})

    run(runs)
  }

  def testMonitorSlot(): Unit = {
    val monitorSlot = new MonitorSlot[Int]
    val n = 10
    var runs: PROC = proc{}

    0.to(n).foreach(i => runs = runs || proc(monitorSlot.put(i)))
    0.to(n).foreach(_ => runs = runs || proc{val v = monitorSlot.get; println(v)})

    run(runs)
  }
}
