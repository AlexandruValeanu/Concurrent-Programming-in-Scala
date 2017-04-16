package concurrent_programming.data_parallel.system_equations


object MatrixUtils {
  type Type = Double
  type Vector = Array[Double]
  type Matrix = Array[Vector]

  def getNullMatrix(n: Int): Matrix = {
    Array.ofDim[Type](n, n)
  }

  def getNullVector(n: Int): Vector = {
    Array.ofDim[Type](n)
  }

  def getIdentityMatrix(n: Int): Matrix ={
    val matrix = getNullMatrix(n)

    for (i <- 0.until(n))
      matrix(i)(i) = 1

    matrix
  }

  def getRows(matrix: Matrix): Int = {
    matrix.length
  }

  def getColumns(matrix: Matrix): Int = {
    if (getRows(matrix) == 0)
      0
    else
      matrix(0).length
  }

  def getDimensions(matrix: Matrix): (Int, Int) = {
    (getRows(matrix), getColumns(matrix))
  }

  def printMatrix(matrix: Matrix): Unit ={
    for (row <- matrix){
      for (e <- row)
        printf("%f ", e)
      println()
    }
  }

  def printVector(vector: Vector): Unit ={
    for (e <- vector)
      printf("%f ", e)
    println()
  }
}
