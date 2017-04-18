package concurrent_programming.peers

import io.threadcso._

import scala.util.Random

/*
  In this protocol, each client node sends its value to a central node,
  which calculates the minimum and maximum, and sends those back.
 */
object CentralizedProtocol extends Runnable{
  type IntPair = (Int, Int)
  private val toController = ManyOne[Int]
  private val fromController = OneMany[IntPair]

  private val random = new Random()
  val N = 10

  def client(me: Int, in: ?[IntPair], out: ![Int]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Client %d chose %d\n", me, v)

    // Send value to controller
    out!v

    val (min, max) = in?()
    println("Client " + me + " ends with values " + (min, max))
  }

  def controller(in: ?[Int], out: ![IntPair]): PROC = proc{
    // Choose value randomly
    val v = random.nextInt(100)
    printf("Controller chose %d\n", v)

    // Receive values, and calculate min and max
    var min = v
    var max = v
    for (_ <- 1.until(N)){
      val w = in?()
      min = Math.min(min, w)
      max = Math.max(max, w)
    }

    println("Controller ends with values " + (min, max))

    for (_ <- 1.until(N))
      out!(min, max)
  }

  override def run(): Unit = {
    val clients = || (for (i <- 1.until(N)) yield client(i, fromController, toController))
    (controller(toController, fromController) || clients)()
  }
}
