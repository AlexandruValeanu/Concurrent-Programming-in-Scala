package concurrent_programming.data_parallel.system_equations

import io.threadcso.{Barrier, CombiningBarrier, PROC, proc, ||}

class ConcurrentJacobiIteration(A: MatrixUtils.Matrix, b: MatrixUtils.Vector){
  val N: Int = MatrixUtils.getRows(A)
  private val x = MatrixUtils.getNullVector(N)
  private val newX = MatrixUtils.getNullVector(N)

  val EPSILON: Double = 0.00000001

  private val W = 2
  private val height = N / W

  private val conjBarrier = new CombiningBarrier[Boolean](W, true, _ && _)
  private val barrier = new Barrier(W)

  private def worker(start: Int, end: Int): PROC = proc{
    var finished = false

    while (!finished){
      // calculate our slice of newX from x
      for (i <- start until end){
        var sum: MatrixUtils.Type = 0

        for (j <- 0.until(N))
          if (i != j)
            sum += A(i)(j) * x(j)

        newX(i) = (b(i) - sum) / A(i)(i)
      }

      barrier.sync()
      // all workers have written their own slice of newX

      // calculate our slice of x from newX
      var finishedLocally = true
      for (i <- start until end){
        var sum: MatrixUtils.Type = 0

        for (j <- 0.until(N))
          if (i != j)
            sum += A(i)(j) * newX(j)

        x(i) = (b(i) - sum) / A(i)(i)
        finishedLocally = finishedLocally && (Math.abs(x(i) - newX(i)) < EPSILON)
      }

      // cast our vote for termination, retrieve the aggregated votes
      finished = conjBarrier.sync(finishedLocally)
      // all workers have written their own slice of x for this iteration
    }
  }

  def solve(): MatrixUtils.Vector = {
    for (i <- 0.until(N))
      x(i) = 0

    val system = || (for (i <- 0 until W) yield worker(height*i, height*(i+1)))
    system()
    x
  }
}
