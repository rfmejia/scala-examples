package demo.finagle

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}
import com.twitter.finagle.SimpleFilter
import com.twitter.finagle.ListeningServer

/* In this setup, we create a few HTTP servers and attempt to load-balance them.
 * Each server has a debug filter (i.e., middleware) to inspect incoming requests. */
object SimpleHttpServer {
  def stringLengthService = new Service[Request, Response] {
    override def apply(request: Request): Future[Response] =
      Future {
        val computationResult =
          Option(request.getParam("name")).map(_.length).getOrElse(-1)
        val response = Response(Status.Ok)
        response.setContentString(computationResult.toString)
        response
      }
  }

  def debugFilter(id: String) = new SimpleFilter[Request, Response] {
    override def apply(
        request: Request,
        service: Service[Request, Response]
    ): Future[Response] = {
      println(s"[$id] received request $request")
      service(request)
    }
  }

  def simpleHttpServer(port: Int): ListeningServer =
    Http.serve(s":$port", debugFilter(s"server-$port").andThen(stringLengthService))

  def loadBalancingSetup: IndexedSeq[ListeningServer] =
    (9090 to 9092).map(simpleHttpServer)

  def main(args: Array[String]): Unit =
    loadBalancingSetup.foreach(server => Await.ready(server))
}
