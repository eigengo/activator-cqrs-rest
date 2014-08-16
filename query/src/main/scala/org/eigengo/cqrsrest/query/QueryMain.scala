package org.eigengo.cqrsrest.query

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import org.eigengo.cqrsrest.router.{RouterProtocol, Router, Registration}
import org.json4s.{DefaultFormats, Formats}
import spray.can.Http
import spray.routing.HttpServiceActor

class QueryMainServiceActor extends HttpServiceActor with ExerciseRoute {
  import scala.concurrent.duration._

  implicit val timeout: Timeout = Timeout(1000 milliseconds)
  implicit val ec = context.dispatcher
  override def receive: Receive = runRoute(exerciseRoute)
}

object QueryMain extends App {
  import scala.concurrent.duration._

  implicit def json4sFormats: Formats = DefaultFormats
  implicit val timeout: Timeout = Timeout(1000 milliseconds)

  implicit private val system = ActorSystem()
  val service = system.actorOf(Props(new QueryMainServiceActor))

  // bind the the router running at localhost:8080, specifying the write side and 1.0.0 API version
  Router("http://localhost:8080", RouterProtocol.Query, "1.0.0") { parameters =>
    IO(Http)(system) ! Http.Bind(service, interface = parameters.host, port = parameters.port)
  }

}
