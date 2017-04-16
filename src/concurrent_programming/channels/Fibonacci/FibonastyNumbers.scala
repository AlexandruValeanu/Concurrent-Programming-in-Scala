package concurrent_programming.channels.Fibonacci

import concurrent_programming.channels.ChannelsFunctions
import io.threadcso._
import io.threadcso.component._

object FibonastyNumbers {
  // Haskell recursive definition of the fibonacci numbers
  // fibn = 0:1:zipWith(\ x y -> 1+x*y)(fibn, tail fibn)

  /** pre: x >= 0 y>0
      returns: 1 + x * y, unless computing that would overflow
      -1, otherwise
    */
  private def nextFib(x: Long, y: Long): Long = try{
    Math.addExact(1L, Math.multiplyExact(x, y))
  }
  catch {
    case _: ArithmeticException => -1L
  }

  private def fibN(out: ![Long]): PROC = proc{
    val tailFibn = OneOne[Long]
    val fibs, out1, out2 = OneOne[Long]
    val result = OneOne[Long]

    run(ChannelsFunctions.prefix(0L, 1L)(result, fibs) ||
        ChannelsFunctions.tee(fibs, out, out1, out2) ||
        ChannelsFunctions.tail(out1, tailFibn) ||
        ChannelsFunctions.zipWith(nextFib)(tailFibn, out2, result))
  }

  def printSmallFibs(): Unit ={
    val fibs, out = OneOne[Long]
    run(fibN(fibs) || ChannelsFunctions.takeWhile((x: Long) => x >= 0)(fibs, out) ||
      ChannelsFunctions.slowConsole(out))
  }

  private def bigNextFib(x: BigInt, y: BigInt): BigInt = 1 + x * y

  private def bigFibN(out: ![BigInt]): PROC = proc{
    val tailFibn = OneOne[BigInt]
    val fibs, out1, out2 = OneOne[BigInt]
    val result = OneOne[BigInt]

    run(ChannelsFunctions.prefix[BigInt](0, 1)(result, fibs) ||
      ChannelsFunctions.tee(fibs, out, out1, out2) ||
      ChannelsFunctions.tail(out1, tailFibn) ||
      ChannelsFunctions.zipWith(bigNextFib)(tailFibn, out2, result))
  }

  def printBigFibs(): Unit ={
    val fibs = OneOne[BigInt]
    run(bigFibN(fibs) || ChannelsFunctions.slowConsole(fibs))
  }

  def main(args: Array[String]): Unit = {
    printBigFibs()
  }
}
