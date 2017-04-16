package concurrent_programming.monitors.memory_allocation

/*
An operating system represents the addresses of memory pages as integers, and implements
a page allocation subsystem as an instance of this trait.

When a process calls request(count) it must wait until at least count pages of memory
are available. At least count pages are then allocated to the process and returned to the
caller as the result of request.

A process returns a set of pages that have been allocated to it back to the system by
calling release(pages)
 */
trait Allocator {
  def request(count: Int): Set[Int]
  def release(pages: Set[Int])

}
