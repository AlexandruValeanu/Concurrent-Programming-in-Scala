package concurrent_programming.peers

import io.threadcso._

import scala.util.Random

/*
  Each value gets passed around the ring. Each node keeps track of the minimum and maximum
  values it has seen so far.

  More precisely, on round k, each node i receives a value from node (i - 1) mod N that
  originated with node (i - k) mod N; it sends this value on to node (i + 1) mod N, and keeps
  track of the minimum and maximum values, (min, max)
 */
object MultiRing extends Runnable{
  val N = 10

  private val random = new Random()

  private def node(me: Int, in: ?[Int], out: ![Int]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Node %d chose %d\n", me, v)
    out!v

    // Repeatedly receive value from neighbour, update max and min, and pass value on
    var min = v
    var max = v
    for (_ <- 1.until(N)){
      val w = in?()
      min = Math.min(min, w)
      max = Math.max(max, w)
      out!w
    }

    val w = in?() // get back the value that sent
    assert(w == v)

    println("Node " + me + " ends with " + (min, max))
  }

  override def run(): Unit = {
    val channels = for (_ <- 0.until(N)) yield OneOneBuf[Int](1) // has to be buffered
    ||(for (i <- 0.until(N)) yield node(i, channels((i - 1 + N) % N), channels(i)))()
  }
}
