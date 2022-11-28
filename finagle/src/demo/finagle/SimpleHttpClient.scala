package demo.finagle

import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.{Address, Http, Name}
import com.twitter.util.{Await, Duration, Future, JavaTimer}

import scala.util.Random

object SimpleHttpClient {
  def send(input: String): Unit = {
    val filteredClient = {
      val baseClient = Http.newService("localhost:9090")
      val timeoutFilter =
        new TimeoutFilter[Request, Response](Duration.fromSeconds(1), new JavaTimer())
      timeoutFilter.andThen(baseClient)
    }

    val request = Request(Method.Get, s"/?name=$input")
    val response = filteredClient(request)
    response.onSuccess(r => println(r.getContentString))
    response.onFailure(e => Console.err.println(e.getMessage))
    Await.ready(response)
  }
}

