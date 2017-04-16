package concurrent_programming.channels.SameLeaves

import concurrent_programming.channels.ChannelsFunctions
import io.threadcso._

object SameLeaves {
  def sameLeaves[V](tl: Tree[V], tr: Tree[V]): Boolean = {
    val left, right = OneOne[V]
    val answer = OneOneBuf[Boolean](1)
    (proc{tl.leavesTo(left)} ||
     proc{tr.leavesTo(right)} ||
     ChannelsFunctions.equal(left, right, answer)
    )()
    answer?()
  }
}
