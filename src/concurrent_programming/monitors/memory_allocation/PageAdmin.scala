package concurrent_programming.monitors.memory_allocation

trait PageAdmin {
  def hasAvailable(count: Int): Boolean
  def request(count: Int): Set[Int]
  def release(pages: Set[Int])
}
