package concurrent_programming

import io.threadcso.{PROC, proc, run, sleep}

object Main {
  def main(args: Array[String]): Unit = {
    val N = 1000000

    for (_ <- 0.to(10)){
      count = 0
      run(increment(N) || decrement(N))
      println(count)
    }
  }

  def pprint[T](iterable: Iterable[T]): PROC = proc{
    for (x <- iterable){
      println(x)
      sleep(200 * io.threadcso.milliSec)
    }
  }

  def range(begin: Int, end: Int, step: Int): Iterable[Int] = {
    for (e <- begin.to(end, step))
      yield e
  }

  var count = 0
  val any = new Object

  def increment(N: Int): PROC = proc{
    for (_ <- 0.to(N)) {
      any synchronized {
        count += 1
      }
    }
  }

  def decrement(N: Int): PROC = proc{
    for (_ <- 0.to(N)){
      any synchronized {
        count -= 1
      }
    }
  }
}
