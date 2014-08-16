package org.eigengo.cqrsrest.router

import java.util.UUID

import akka.actor.ActorRefFactory
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport

import scala.util.{Failure, Success}

object RegistrationProtocol {
  type Host      = String
  type Port      = Int
  type Version   = String
  type Reference = UUID

  type Run       = (=> Unit) => Unit

  sealed trait Side
  case object Query extends Side
  case object Write extends Side

  case class Register(host: Host, port: Port, version: Version, side: Side)
  case class Registered(ref: Reference)

  case class Unregister(ref: Reference)
  case class Unregistered(ref: Reference)
}

object Registration extends Json4sSupport {
  import org.eigengo.cqrsrest.router.RegistrationProtocol._
  import spray.client.pipelining._

  override implicit def json4sFormats: Formats = DefaultFormats

  private def unregister(routerUrl: String, ref: Reference)(implicit arf: ActorRefFactory): Unit = {
    import arf.dispatcher
    val unregisterPipeline = sendReceive ~> unmarshal[Unregistered]
    unregisterPipeline(Post(s"$routerUrl/unregister", Unregister(ref)))
  }

  def apply(routerUrl: String, register: RegistrationProtocol.Register)(run: Run)(implicit arf: ActorRefFactory): Unit = {
    import arf.dispatcher
    val registerPipeline = sendReceive ~> unmarshal[Registered]
    registerPipeline(Post(s"$routerUrl/register", register)).onComplete {
      case Success(r) => run(unregister(routerUrl, r.ref))
      case Failure(t) => throw t
    }
  }

}
