package concurrent_programming.peers

import io.threadcso._

import scala.util.Random

/*
  The first protocol uses two stages. During the first stage, node 0 acts as the initiator, by
  sending a token containing two copies of its value.

  Each node in turn receives the token, containing the minimum and maximum value so far,
  calculates the new minimum and maximum, taking its own value into account, and sends them
  on.
 */
object TwiceRing extends Runnable {
  type IntPair = (Int, Int)
  val N = 10

  private val random = new Random()

  private def node(me: Int, in: ?[IntPair], out: ![IntPair]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Node %d chose %d\n", me, v)

    // receive min and max so far, and pass on possibly updated min and max
    val (min1, max1) = in?()
    out!(Math.min(min1,v), Math.max(max1,v))

    // receive final min and max
    val (min, max) = in?()
    out!(min, max)
    println("Node " + me + " ends with " + (min, max))
  }

  private def initiator(in: ?[IntPair], out: ![IntPair]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Initiator chose %d\n", v)

    // Start the communications going
    out!(v, v)

    // Receive min and max back, and send them round
    val (min1, max1) = in?()
    out!(min1, max1)

    // Receive them back at the end
    val (min, max) = in?()
    println("Initiator ends with "+ (min, max))
  }

  override def run(): Unit = {
    val toNode = for (_ <- 0.until(N)) yield OneOne[IntPair]
    val nodes = || (for (i <- 1.until(N)) yield node(i, toNode(i - 1), toNode(i)))

    (initiator(toNode(N - 1), toNode(0)) || nodes)()
  }
}
