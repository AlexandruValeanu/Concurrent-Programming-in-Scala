package concurrent_programming.integration

object IntegralMain {
  def main(args: Array[String]): Unit = {
    def f: Integral.Function = x => x * Math.sin(x)
    Integral.time(Integral.sequentialComputation(f, 0, 10000000))
    Integral.time(Integral.parallelComputation(f, 0, 10000000))
  }
}
