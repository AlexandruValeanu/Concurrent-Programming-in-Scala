package concurrent_programming.monitors.producer_consumer

trait Slot[T] {
  def put(v: T)
  def get: T
}
