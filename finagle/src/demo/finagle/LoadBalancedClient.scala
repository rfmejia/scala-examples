package demo.finagle

import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.{Address, Http, Name}
import com.twitter.util.{Await, Duration, Future, JavaTimer}
import scala.util.Random

object LoadBalancedClient {
  def send: Unit = {
    val addresses = (9090 to 9092).map(port => Address("localhost", port))
    val name = Name.bound(addresses: _*)
    val client = Http.newService(name, "lb-client")

    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    val requests = (1 to 20).map { i =>
      val input = List
        .fill(Random.between(1, 200))(
          chars(Random.between(0, chars.size))
        )
        .mkString
      Request(Method.Get, s"?name=$input")
    }

    val responses = requests.map(req => client(req).map(_.getContentString))
    Await.ready(Future.collect(responses).foreach(println))
  }
}