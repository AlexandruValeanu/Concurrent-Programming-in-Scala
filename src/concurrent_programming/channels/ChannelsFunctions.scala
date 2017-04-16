package concurrent_programming.channels

import io.threadcso._

object ChannelsFunctions {
  def copy[T](in: ?[T], out: ![T]): PROC = proc{
    repeat{
      val x = in?()
      out!x
    }

    in.closeIn()
    out.closeOut()
  }

  def console[T](in: ?[T]): PROC = proc{
    repeat{
      println(in?())
    }

    in.closeIn()
  }

  def slowConsole[T](in: ?[T]): PROC = proc{
    repeat{
      println(in?())
      sleep(300 * milliSec)
    }

    in.closeIn()
  }

  def nats(out: ![Int]): PROC = proc{
    var n = 0

    repeat{
      out!n
      n += 1
    }

    out.closeOut()
  }

  def alts[T](in: ?[T], out: ![T]): PROC = proc{
    repeat{
      out!(in?())
      val _ = in?()
    }

    in.closeIn()
    out.closeOut()
  }

  def tee[T](in: ?[T], outs: ![T]*): PROC = proc{
    // deadlock-free design of tee
    repeat{
      val v = in?()

      var system = proc{}

      for (out <- outs)
        system = system || proc{out!v}

      run(system)
    }

    in.closeIn()

    for (out <- outs)
      out.closeOut()
  }

  def equal[T](inl: ?[T], inr: ?[T], ans: ![Boolean]): PROC = proc{
    var l, r = inl.nothing
    var ln, rn = 0
    var same = true

    repeat(same){
      run(proc{l = inl?(); ln += 1} || proc{r = inr?(); rn += 1})
      same = l == r
      ans!(same && ln == rn)
    }

    inl.closeIn(); inr.closeIn()
    ans.closeOut()
  }

  def sorted_merge(inl: ?[Int], inr: ?[Int], out: ![Int]): PROC = proc("sorted-merge"){
    var l = inl.nothing
    var r = inr.nothing

    repeat{
      (proc{if (l == inl.nothing) l = inl?()} ||
        proc{if (r == inr.nothing) r = inr?()})()

      if (l == r){
        out!l
        l = inl.nothing
        r = inr.nothing
      }
      else if (l < r){
        out!l
        l = inl.nothing
      }
      else{
        out!r
        r = inr.nothing
      }
    }

    if (l != inl.nothing){
      repeat{
        out!l
        l = inl?()
      }
    }

    if (r != inr.nothing){
      repeat{
        out!r
        r = inr?()
      }
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def merge(inl: ?[Int], inr: ?[Int], out: ![Int]): PROC = proc("merge"){
    var l = inl.nothing
    var r = inr.nothing

    repeat{
      (proc{if (l == inl.nothing) l = inl?()} ||
        proc{if (r == inr.nothing) r = inr?()})()

      run(proc{out!l} || proc{out!r})
      l = inl.nothing
      r = inr.nothing
    }

    if (l != inl.nothing){
      repeat{
        out!l
        l = inl?()
      }
    }

    if (r != inr.nothing){
      repeat{
        out!r
        r = inr?()
      }
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def zipWith[U, V, T](f: (U, V) => T)(inl: ?[U], inr: ?[V], out: ![T]): PROC = proc{
    var l = inl.nothing
    var r = inr.nothing

    repeat{
      run(proc{l = inl?()} || proc{r = inr?()})
      out!f(l, r)
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def map[U, V](f: U => V)(in: ?[U], out: ![V]): PROC = proc{
    repeat{
      out! f(in ? ())
    }

    in.closeIn()
    out.closeOut()
  }

  def prefix[T](vs: T*)(in: ?[T], out: ![T]): PROC = proc{
    attempt{
      for (v <- vs)
        out!v

      repeat{
        out!(in?())
      }
    }{}


    in.closeIn()
    out.closeOut()
  }

  def unzip[U, V](in: ?[(U, V)], l: ![U], r: ![V]): PROC = proc{
    repeat{
      val (x, y) = in?()
      run(proc{l!x} || proc{r!y})
    }

    in.closeIn()
    l.closeOut(); r.closeOut()
  }

  def simpleExchanger(l: ?[Int], r: ?[Int], lo: ![Int], hi: ![Int]): PROC = proc{
    var x, y = 0

    repeat{
      run(proc{x = l?()} || proc{y = r?()})

      if (x > y){
        val t = x
        x = y
        y = t
      }

      run(proc{lo!x} || proc{hi!y})
    }

    l.closeIn(); r.closeIn()
    lo.closeOut(); hi.closeOut()
  }

  def smartExchanger(l: ?[Int], r: ?[Int], lo: ![Int], hi: ![Int]): PROC = proc{
    val tmp = OneOne[(Int, Int)]
    run(zipWith((x: Int, y: Int) => if (x <= y) (x, y) else (y, x))(l, r, tmp) ||
        unzip(tmp, lo, hi))
  }

  def drop[T](n: Int)(in: ?[T], out: ![T]): PROC = proc{
    var k = n

    repeat(k > 0){
      in?()
      k -= 1
    }

    repeat{
      out!(in?())
    }

    in.closeIn()
    out.closeOut()
  }

  def take[T](n: Int)(in: ?[T], out: ![T]): PROC = proc{
    var k = n

    repeat(k > 0){
      out!(in?())
      k -= 1
    }

    in.closeIn()
    out.closeOut()
  }

  def tail[T](in: ?[T], out: ![T]): PROC = proc{
    attempt{
      in?()

      repeat{
        out!(in?())
      }
    }{}

    in.closeIn()
    out.closeOut()
  }

  def takeWhile[T](f: T => Boolean)(in: ?[T], out: ![T]): PROC = proc{
    repeat{
      val v = in?()

      if (!f(v))
        stop

      out!v
    }

    in.closeIn()
    out.closeOut()
  }

  def untilZero(in: ?[Long], out: ![Long]): PROC = proc{
    var finished = false

    repeat(!finished){
      val v = in?()

      if (v == 0)
        finished = true
      else
        out!v
    }

    in.closeIn()
    out.closeOut()
  }
}
