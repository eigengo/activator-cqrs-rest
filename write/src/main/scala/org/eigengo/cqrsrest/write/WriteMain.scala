package org.eigengo.cqrsrest.write

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import org.eigengo.cqrsrest.router.{RouterProtocol, Router, Registration}
import org.json4s.{DefaultFormats, Formats}
import spray.can.Http
import spray.routing.HttpServiceActor

class WriteMainServiceActor extends HttpServiceActor with ExerciseRoute {
  import scala.concurrent.duration._

  implicit val timeout: Timeout = Timeout(1000 milliseconds)
  implicit val ec = context.dispatcher
  override def receive: Receive = runRoute(exerciseRoute)
}

object WriteMain extends App {
  import scala.concurrent.duration._

  implicit def json4sFormats: Formats = DefaultFormats
  implicit val timeout: Timeout = Timeout(1000 milliseconds)

  implicit private val system = ActorSystem()
  system.actorOf(Props(new ExerciseActor), actors.exercise.name)
  val service = system.actorOf(Props(new WriteMainServiceActor), "write-service")

  // bind the the router running at localhost:8080, specifying the write side and 1.0.0 API version
  Router("http://localhost:8080", RouterProtocol.Write, "1.0.0") { parameters =>
    IO(Http)(system) ! Http.Bind(service, interface = parameters.host, port = parameters.port)
  }

}
