package concurrent_programming.data_parallel.system_equations

import java.io.FileInputStream
import java.util.Scanner


object MatrixMain {
  def main(args: Array[String]): Unit = {
    val scanner = new Scanner(new FileInputStream("src\\concurrent_programming\\data_parallel\\system_equations\\test.in"))
    val N = scanner.nextInt()
    val matrix = MatrixUtils.getNullMatrix(N)

    for (i <- 0.until(N); j <- 0.until(N))
      matrix(i)(j) = scanner.nextInt()

    val bs = MatrixUtils.getNullVector(N)

    for (i <- 0.until(N))
      bs(i) = scanner.nextInt()

    val x = new ConcurrentJacobiIteration(matrix, bs).solve()
    MatrixUtils.printVector(x)
  }
}
