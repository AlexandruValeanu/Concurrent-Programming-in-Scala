package concurrent_programming.data_parallel

import io.threadcso.semaphore.BooleanSemaphore

class Barrier(private val n: Int) {
  private [this] var waiting = 0 // # processes waiting
  private [this] val gate = new BooleanSemaphore(available = false)
  private [this] val mutex = new BooleanSemaphore(available = true) // add a mutex to ensure atomicity of enlistment

  def sync(): Unit ={
    mutex.acquire()

    if (waiting == n - 1){
      gate.release()            // last enlisting process opens gate
    }
    else{
      waiting += 1
      mutex.release()
      gate.acquire()            // enlistee waits at the gate
      waiting -= 1

      if (waiting > 0)
        gate.release()          // enlistee opens gate for waiting successor
      else
        mutex.release()         // allow next round to start
    }
  }
}
