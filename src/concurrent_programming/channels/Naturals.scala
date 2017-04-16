package concurrent_programming.channels

import io.threadcso._

object Naturals {
  def main(args: Array[String]): Unit = {
    val mid, nats, succs, out = OneOne[Int]

    val circuit = ChannelsFunctions.prefix(0)(succs, nats) ||
      ChannelsFunctions.tee(nats, out, mid) ||
      ChannelsFunctions.map((x: Int) => x + 1)(mid, succs) ||
      ChannelsFunctions.slowConsole(out)

    run(circuit)
  }
}
