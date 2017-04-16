package concurrent_programming.data_parallel.smoothing

object SmoothMain {
  def main(args: Array[String]): Unit = {
    val N = 10
    val M = 10

    val image = SmoothUtils.generateRandomImage(N, M)
    SmoothUtils.printImage(image)
    println()

    new SmoothImage(N, M, image).run()
    SmoothUtils.printImage(image)
  }
}
