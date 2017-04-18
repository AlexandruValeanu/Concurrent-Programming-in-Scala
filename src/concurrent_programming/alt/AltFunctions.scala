package concurrent_programming.alt

import io.threadcso._

object AltFunctions {
  def tagger[T](inl: ?[T], inr: ?[T], out: ![(Int, T)]): PROC = proc{
    repeat{
      alt(inl =?=> {vl => out!(0, vl)} |
          inr =?=> {vr => out!(1, vr)}
      )
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def tagger_eff[T](inl: ?[T], inr: ?[T], out: ![(Int, T)]): PROC = proc{
    serve(inl =?=> {lv => out!(0, lv)} |
          inr =?=> {rv => out!(1, rv)}
    )

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def detagger[T](in: ?[(Int, T)], inl: ![T], inr: ![T]): PROC = proc{
    repeat{
      val (ind, v) = in?()

      if (ind == 0)
        inl!v
      else
        inr!v
    }

    in.closeIn()
    inl.closeOut(); inr.closeOut()
  }

  def fairTagger[T](inl: ?[T], inr: ?[T], out: ![(Int, T)]): PROC = proc{
    var diff = 0

    // terminate when: diff > -5 and inr closed || diff < 5 and inl closed
    repeat{
      alt (((diff < 5) && inl) =?=> { lv => out!(0, lv); diff+=1 } |
           ((diff > -5) && inr) =?=> { rv => out!(1, rv); diff-=1 }
      )
    }

    // at most one of these will read more than 0 times
    repeat{
      out!(0, inl?())
    }
    repeat{
      out!(1, inr?())
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def fairTagger_eff[T](inl: ?[T], inr: ?[T], out: ![(Int, T)]): PROC = proc{
    var diff = 0

    // terminate when: diff > -5 and inr closed || diff < 5 and inl closed
    serve (((diff < 5) && inl) =?=> { lv => out!(0, lv); diff+=1 } |
        ((diff > -5) && inr) =?=> { rv => out!(1, rv); diff-=1 }
      )

    // at most one of these will read more than 0 times
    repeat{
      out!(0, inl?())
    }
    repeat{
      out!(1, inr?())
    }

    inl.closeIn(); inr.closeIn()
    out.closeOut()
  }

  def tee[T](in: ?[T], out1: ![T], out2: ![T]): PROC = proc {
    var v: T = in.nothing

    serve(out1 =!=> {v = in?(); v} ==> {out2!v} |
          out2 =!=> {v = in?(); v} ==> {out1!v}
    )

    in.closeIn()
    out1.closeOut(); out2.closeOut()
  }
}
