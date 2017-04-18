package concurrent_programming.peers

object ProtocolMain {
  def main(args: Array[String]): Unit = {
    CentralizedProtocol.run()
    SymmetricProtocol.run()
    TwiceRing.run()
    MultiRing.run()
  }
}
