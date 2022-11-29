package demo

import lambdaweb4s.models.Methods._
import lambdaweb4s.LambdaWebHandler
import lambdaweb4s.DevWebServer
import lambdaweb4s.models._
import lambdaweb4s.models.Path.Root
import lambdaweb4s.models.HttpCodes._
import lambdaweb4s.util.staticContent

class Handler extends LambdaWebHandler {
  def accountInfo(accountId: String): Response = {
    Response.ok(s"Info for account: $accountId")
  }

  def routes: PartialFunction[Request, Response] = {
    case GET -> Root                                   => Response.ok("Hello World")
    case GET -> Root / "_health"                       => Response.ok("healthy")
    case GET -> Root / "accounts" / accountId / "info" => accountInfo(accountId)
    case req @ GET -> Root / "debug"                   => Response.ok(req.toString)
    case GET -> Path("static" :: path) =>
      val fullPath = path.mkString("/")
      if (fullPath.endsWith(".jpg") || fullPath.endsWith(".jpeg"))
        staticContent.contentFromResources(fullPath, ContentType.IMAGE_JPEG)
      else if (fullPath.endsWith(".png"))
        staticContent.contentFromResources(fullPath, ContentType.IMAGE_PNG)
      else if (fullPath.endsWith(".css"))
        staticContent.contentFromResources(fullPath, ContentType.TEXT_CSS)
      else if (fullPath.endsWith(".js"))
        staticContent.contentFromResources(fullPath, ContentType.TEXT_JAVASCRIPT)
      else if (fullPath.endsWith(".html"))
        staticContent.contentFromResources(fullPath, ContentType.TEXT_HTML)
      else
        staticContent.contentFromResources(fullPath, ContentType.TEXT_PLAIN)

    case _ => Response.text(NOT_FOUND, "page not found")
  }
}

object Handler extends App {
  val server = new DevWebServer(new Handler)
  println("Server started")
  server.listen(8080)
}
