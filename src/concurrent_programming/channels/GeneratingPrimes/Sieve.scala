package concurrent_programming.channels.GeneratingPrimes

import concurrent_programming.channels.ChannelsFunctions
import io.threadcso._

object Sieve {
  def sieve(in: ?[Int], out: ![Int]): PROC = proc{
    val n = in?()
    val mid = OneOne[Int]
    out!n
    run(sieveMultiples(n, in, mid) || sieve(mid, out))
    in.closeIn()
    out.closeOut()
  }

  def sieveMultiples(prime: Int, in: ?[Int], out: ![Int]): PROC = proc{
    repeat{
      val m = in?()

      if (m % prime != 0)
        out!m
    }

    in.closeIn()
    out.closeOut()
  }

  def main(args: Array[String]): Unit = {
    val tmp, nats, primes = OneOne[Int]
    run(ChannelsFunctions.nats(tmp) || ChannelsFunctions.drop(2)(tmp, nats) ||
        sieve(nats, primes) || ChannelsFunctions.slowConsole(primes))
  }
}
