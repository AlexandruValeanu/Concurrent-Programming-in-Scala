package concurrent_programming.monitors.semaphores

import io.threadcso.semaphore.BooleanSemaphore

/*
Example: this Counter implementation uses a binary semaphore, mutex, to guarantee the
atomicity of (the critical sections of) the counter methods.
 */
class Counter {
  private val mutex = new BooleanSemaphore(available = true)
  private var count: Long = 0

  def increment(): Unit ={
    mutex.down()
    count += 1
    mutex.up()
  }

  def decrement(): Unit ={
    mutex.down()
    count -= 1
    mutex.up()
  }

  def get: Long = {
    mutex.down()
    val nr = count
    mutex.up()
    nr
  }
}
