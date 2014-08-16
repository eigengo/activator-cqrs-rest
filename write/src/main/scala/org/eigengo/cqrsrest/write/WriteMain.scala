package org.eigengo.cqrsrest.write

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import org.json4s.{DefaultFormats, Formats}
import spray.can.Http
import spray.routing.HttpServiceActor

class WriteMainServiceActor extends HttpServiceActor with HelloRoute {
  import scala.concurrent.duration._

  implicit val timeout: Timeout = Timeout(1000 milliseconds)
  implicit val ec = context.dispatcher
  override def receive: Receive = runRoute(helloRoute)
}

object WriteMain extends App {
  import scala.concurrent.duration._

  val host: String = "localhost"
  val port: Int = 8081

  implicit def json4sFormats: Formats = DefaultFormats
  implicit val timeout: Timeout = Timeout(1000 milliseconds)

  // Check authentication arguments and assign to vals.
  private val system = ActorSystem()
  system.actorOf(Props(new HelloActor), "hello-actor")
  val service = system.actorOf(Props(new WriteMainServiceActor), "write-service")
  IO(Http)(system) ! Http.Bind(service, interface = host, port = port)

}
