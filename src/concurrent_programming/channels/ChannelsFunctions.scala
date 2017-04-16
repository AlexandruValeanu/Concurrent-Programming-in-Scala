package concurrent_programming.channels

import io.threadcso.{!, ?, PROC, proc}

object ChannelsFunctions {
  def copy[T](in: ?[T], out: ![T]): PROC = proc{
    while (true){
      val x = in?()
      out!x
    }
    in.closeIn(); out.closeOut()
  }

  def console[T](in: ?[T]): PROC = proc{
    while (true){
      println(in?())
    }
  }
}
