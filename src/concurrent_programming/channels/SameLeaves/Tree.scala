package concurrent_programming.channels.SameLeaves

import io.threadcso._

trait Tree[V] {
  def subtrees(): Seq[Tree[V]]
  def value: V
  def isLeaf: Boolean

  private def leaves(out: ![V]) = {
    if (isLeaf)
      out!value
    else
      for (t <- subtrees())
        t.leavesTo(out)
  }

  def leavesTo(out: ![V]): Unit = {
    attempt{this.leaves(out)}{}
    out.closeOut()
  }
}
