package concurrent_programming.monitors.producer_consumer

/*
One solution to this problem is for a process to wait for the condition it needs to be true, and
to notify its (at most one waiting) peer when the condition changes.
 */
class WaitSlot[T] extends Slot[T]{
  private [this] var value: T = _
  private [this] var empty = true

  override def put(v: T): Unit = synchronized{
    while (!empty) wait()
    value = v
    empty = false
  }

  override def get: T = synchronized{
    while (empty) wait()
    val v = value
    empty = true
    v
  }
}
