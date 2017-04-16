package concurrent_programming.channels

import io.threadcso._
import ChannelsFunctions._


object Multiple4{
  def main(args: Array[String]): Unit = {
    val x1, x2, x4 = OneOne[Int]

    (nats(x1) ||
      alts(x1, x2) ||
      alts(x2, x4) ||
      slowConsole(x4)
      )()
  }
}
