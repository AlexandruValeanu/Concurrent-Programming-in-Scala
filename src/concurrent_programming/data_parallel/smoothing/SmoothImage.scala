package concurrent_programming.data_parallel.smoothing

import io.threadcso._


class SmoothImage(val N: Int, val M: Int, image: SmoothUtils.Image, val WORKERS: Int = 4) extends Runnable{
  private [this] val originalImage: SmoothUtils.Image = image

  var maxRounds = 100
  private [this] val computed = new Barrier(WORKERS)
  private [this] val updated = new CombiningBarrier[Boolean](WORKERS, true, _ && _)


  private def isValid(x: Int, y: Int): Boolean = {
    0 <= x && x < N && 0 <= y && y < M
  }

  private def average(x: Int, y: Int): SmoothUtils.Type = {
    var count, all = 0

    for ((dx, dy) <- SmoothUtils.dirs){
      if (isValid(x + dx, y + dy))
        if (originalImage(x + dx)(y + dy) == SmoothUtils.BLACK)
          count += 1
        all += 1
    }

    if (2 * count >= all)
      SmoothUtils.BLACK
    else
      SmoothUtils.WHITE
  }

  private def worker(startRow: Int, numRows: Int): PROC = proc{
    val localImage = SmoothUtils.makeLocalCopy(startRow, numRows, M, originalImage) // local copy of the image region
    var (finished, stable) = (false, true)
    var rounds = 0

    while (!finished){
      for (row <- startRow.until(startRow + numRows); col <- 0.until(M)){
        val v = average(row, col)                                   // READ neighbours from global image
        localImage(row - startRow)(col) = v                         // update the local pixel
        stable = stable && (originalImage(row)(col) == v)
      }

      computed.sync() // sync ends of READ phase
      rounds += 1

      SmoothUtils.copy(localImage, 0, 0)(originalImage, startRow, 0)(numRows, M) // publish image region

      finished = updated.sync(stable || rounds >= maxRounds)         // sync ends of WRITE phase
      stable = true
    }
  }

  override def run(): Unit = {
    val slice = N / WORKERS
    var bigproc = worker(slice * (WORKERS - 1), N - slice * (WORKERS - 1))
    0.until(WORKERS - 1).foreach(i => bigproc = bigproc || worker(slice * i, slice))
    bigproc()
  }
}
