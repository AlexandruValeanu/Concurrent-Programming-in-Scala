package concurrent_programming.peers

import io.threadcso._

import scala.collection.immutable
import scala.util.Random

/**
  * Created by alex on 18/04/2017.
  */
object TreeProtocol extends Runnable{
  type IntPair = (Int, Int)

  private val random = new Random()
  val N = 10

  private val up: immutable.IndexedSeq[Chan[(Int, Int)]] = for (i <- 0 until N) yield OneOne[IntPair](s"up$i")
  private val down: immutable.IndexedSeq[Chan[(Int, Int)]] = for (i <- 0 until N) yield OneOne[IntPair](s"down$i")

  private def parent(n: Int): Int = {
    (n - 1) / 2
  }

  private def node(me: Int): PROC = proc("Node" + me){
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Node %d chose %d\n", me, v)

    var min = v
    var max = v

    // Receive min and max values from both children (if they exist)
    val leftChild = 2 * me + 1
    val rightChild = 2 * me + 2

    if (leftChild < N){
      val (min1, max1) = up(leftChild)?()
      min = Math.min(min, min1)
      max = Math.max(max, max1)
    }

    if (rightChild < N){
      val (min2, max2) = up(rightChild)?()
      min = Math.min(min, min2)
      max = Math.max(max, max2)
    }

    // Send min and max to parent, and wait for overall min and max to return
    if (me != 0){
      up(me)!(min, max)
      val (x, y) = down(me)?()
      min = x
      max = y
    }

    // Send min and max to children
    if (leftChild < N)
      down(leftChild)!(min, max)

    if (rightChild < N)
      down(rightChild)!(min, max)

    println("Node " + me + " ends with " + (min, max))
  }

  override def run(): Unit = {
    ||(for (i <- 0.until(N)) yield node(i))()
  }
}
