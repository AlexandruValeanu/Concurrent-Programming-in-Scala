package concurrent_programming.data_parallel.smoothing

import scala.util.Random

object SmoothUtils {
  type Type = Boolean
  type Row = Array[Type]
  type Image = Array[Row]

  val BLACK = true
  val WHITE = false

  val dirs: Array[(Int, Int)] = Array.apply((-1, 0), (1, 0), (0, -1), (0, 1))

  def generateEmptyImage(n: Int, m: Int): Image = {
    Array.ofDim[Type](n, m)
  }

  def copy(source: Image, rowSource: Int, colSource: Int)(destination: Image, rowDest: Int, colDest: Int)
          (numRows: Int, numCols: Int): Unit = {
    for (i <- 0.until(numRows); j <- 0.until(numCols)){
      destination(rowDest + i)(colDest + j) = source(rowSource + i)(colSource + j)
    }
  }

  def copyImage(image: Image): Image = {
    val (n, m) = (image.length, image(0).length)
    val newImage = generateEmptyImage(n, m)

    for (i <- 0.until(n))
      for (j <- 0.until(m))
        newImage(i)(j) = image(i)(j)

    newImage
  }

  def makeLocalCopy(startRow: Int, numRows: Int, M: Int, image: Image): SmoothUtils.Image = {
    val localImage = SmoothUtils.generateEmptyImage(numRows, M)

    for (step <- 0.until(numRows))
      for (j <- 0.until(M))
        localImage(step)(j) = image(startRow + step)(j)

    localImage
  }

  def generateRandomImage(n: Int, m: Int): SmoothUtils.Image = {
    val image = generateEmptyImage(n, m)
    val random = new Random

    for (i <- 0.until(n); j <- 0.until(m)){
      image(i)(j) = random.nextBoolean()
    }

    image
  }

  def printImage(image: Image): Unit ={
    val (n, m) = (image.length, image(0).length)

    for (i <- 0.until(n)){
      for (j <- 0.until(m)){
        if (image(i)(j) == BLACK)
          printf("1")
        else
          printf("0")
      }
      println()
    }
  }
}
