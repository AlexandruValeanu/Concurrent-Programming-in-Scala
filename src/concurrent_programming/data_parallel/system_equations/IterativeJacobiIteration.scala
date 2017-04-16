package concurrent_programming.data_parallel.system_equations

class IterativeJacobiIteration(A: MatrixUtils.Matrix, b: MatrixUtils.Vector){
  private val N = MatrixUtils.getRows(A)
  private val x = MatrixUtils.getNullVector(N)
  private val tmpX = MatrixUtils.getNullVector(N)

  private val EPSILON: Double = 0.00000001
  private val MAX_ROUNDS = 100

  private def iteration(): Boolean ={
    for (i <- 0.until(N)){
      var sum: MatrixUtils.Type = 0

      for (j <- 0.until(N))
        if (i != j)
          sum += A(i)(j) * x(j)

      tmpX(i) = (b(i) - sum) / A(i)(i)
    }

    var stable = true

    for (i <- 0.until(N)){
      stable = stable && (Math.abs(x(i) - tmpX(i)) < EPSILON)
      x(i) = tmpX(i)
    }

    stable
  }

  def solve(): MatrixUtils.Vector = {
    for (i <- 0.until(N))
      x(i) = 0

    var finished = false
    var round = 0

    while (!finished){
      round += 1
      finished = iteration() || (round > MAX_ROUNDS)
    }

    x
  }
}
