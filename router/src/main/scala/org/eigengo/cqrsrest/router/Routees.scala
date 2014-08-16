package org.eigengo.cqrsrest.router

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import akka.io.IO
import akka.pattern.AskSupport
import akka.util.Timeout
import org.json4s.{DefaultFormats, Formats}
import spray.can.Http
import spray.http._
import spray.httpx.Json4sSupport
import spray.routing.{Directives, HttpService, RequestContext, Route}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object RouteesActor {
  import org.eigengo.cqrsrest.router.RouterProtocol._

  case class Routee(ref: Reference, host: Host, port: Port, version: Version, side: Side)

}

class RouteesActor extends Actor {
  import org.eigengo.cqrsrest.router.RouteesActor._
  private var routees: List[Routee] = List()
  import org.eigengo.cqrsrest.router.RouterProtocol._

  private implicit class RichList[A](l: List[A]) {
    def randomElement: A = l(Random.nextInt(l.size))
  }

  private def stripHostHeader(headers: List[HttpHeader] = Nil) =
    headers filterNot (header => header is HttpHeaders.Host.lowercaseName)

  private def findRoutee(uri: Uri, method: HttpMethod): Option[Uri] = {
    val path            = uri.path.tail
    val versionPath     = path.head
    val versionlessPath = path.tail

    println(versionlessPath.toString())

    val side = if (method == HttpMethods.GET || method == HttpMethods.OPTIONS || method == HttpMethods.OPTIONS) Query else Write

    routees.filter(r => r.version == versionPath.toString && r.side == side) match {
      case Nil => None
      case elems =>
        val router = elems.randomElement
        Some(uri.withHost(router.host).withPort(router.port).withPath(versionlessPath))
    }
  }

  def receive: Receive = {
    case cmd@RouterProtocol.Register(_, _, _, _) =>
      val ref = UUID.randomUUID().toString
      routees = routees :+ Routee(ref, cmd.host, cmd.port, cmd.version, cmd.side)
      sender() ! Registered(ref)
    case RouterProtocol.Unregister(ref) =>
      routees = routees.filter(_.ref != ref)
      sender() ! Unregistered(ref)

    case ctx: RequestContext =>
      val request = ctx.request
      findRoutee(request.uri, request.method).fold
      {
        ctx.complete(HttpResponse(status = StatusCodes.BadGateway, entity = HttpEntity(s"No routee for path ${request.uri.path}")))
      }
      { updatedUri =>
        val updatedRequest = request.copy(uri = updatedUri, headers = stripHostHeader(request.headers))
        IO(Http)(context.system) tell(updatedRequest, ctx.responder)
      }
  }
}

trait RouteesRoute extends Directives with AskSupport with Json4sSupport {
  import org.eigengo.cqrsrest.router.RouterProtocol._

import scala.concurrent.duration._

  override implicit def json4sFormats: Formats = DefaultFormats + SideSerializer
  implicit val timeout: Timeout = Timeout(1000.milliseconds)

  def routeesRoute(routees: ActorRef)(implicit es: ExecutionContext): Route =
    path("register") {
      post {
        entity(as[Register]) { cmd =>
          complete((routees ? cmd).mapTo[Registered])
        }
      } ~
      delete {
        entity(as[Unregister]) { cmd =>
          complete((routees ? cmd).mapTo[Unregistered])
        }
      }
    }

}

trait ProxyRoute extends Directives with ProxyDirectives with AskSupport {

  /** Route adds user identity header and proxies all requests to given host */
  def proxyRoute(routees: ActorRef): Route = ctx => {
    routees ! ctx
  }

}
