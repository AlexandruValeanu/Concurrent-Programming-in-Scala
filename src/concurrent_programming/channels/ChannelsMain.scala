package concurrent_programming.channels

import io.threadcso._

object ChannelsMain {
  def main(args: Array[String]): Unit = {
    val ones, twos, lo, hi = OneOne[Int]
    val out = OneOne[(Int, Int)]

    run(ChannelsFunctions.randomStreamInt(twos) ||
        ChannelsFunctions.randomStreamInt(ones) ||
        ChannelsFunctions.simpleExchanger(twos, ones, lo, hi) ||
        ChannelsFunctions.zipWith((x: Int, y: Int) => (x, y))(lo, hi, out) ||
        ChannelsFunctions.slowConsole(out))
  }
}
