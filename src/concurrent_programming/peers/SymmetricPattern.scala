package concurrent_programming.peers

import io.threadcso._
import scala.util.Random

object SymmetricPattern extends Runnable{
  private val random = new Random()
  val N = 10

  private val toNode = for (_ <- 0.until(N)) yield ManyOne[Int]

  private def node(me: Int, in: ?[Int], toNode: Seq[![Int]]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Client %d chose %d\n", me, v)

    // Process to distribute value to all other nodes
    def sender(): PROC = proc{
      for (i <- 0.until(N))
        if (i != me)
          toNode(i)!v
    }

    // Process to receive values from other nodes, and calculate min and max
    def receiver(): PROC = proc{
      var min = v
      var max = v

      for (_ <- 1.until(N)){
        val w = in?()
        min = Math.min(min, w)
        max = Math.max(max, w)
      }

      println("Node " + me + " ends with " + (min, max))
    }

    // Run sender and receiver in parallel
    (sender() || receiver())()
  }

  override def run(): Unit = {
    || (for (i <- 0.until(N)) yield node(i, toNode(i), toNode))()
  }
}
