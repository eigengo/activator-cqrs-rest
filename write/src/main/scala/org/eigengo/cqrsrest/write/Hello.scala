package org.eigengo.cqrsrest.write

import akka.pattern.AskSupport
import akka.persistence.PersistentActor
import akka.util.Timeout
import org.eigengo.cqrsrest.write.HelloProtocol.HelloBack
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport
import spray.routing.{HttpService, Directives, Route}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Defines the REST protocol for the ``HelloRoute``. At this moment, it includes the ``Hello`` request and
 * ``HelloBack`` response.
 *
 * It is expected that these values will be marshalled and unmarshalled into JSON
 */
object HelloProtocol {
  sealed trait HelloActorMessage
  case class Hello(hi: String) extends HelloActorMessage
  case class HelloBack(helloBackTo: String) extends HelloActorMessage
}

/**
 * Defines the ``Route`` that handles the ``/hello`` URLs. The route uses the protocol defined above, and uses
 * Json4s to deal with the JSON gymnastics.
 *
 * Notice in particular the actor lookup (``actorRefFactory.actorSelection(...)``) in order to always get fresh
 * ``ActorRef`` to what could be a remote actor.
 *
 * See the RAML definition that describes the service; see also the ``org.eigengo:sbt-raml:0.1-SNAPSHOT`` SBT plugin
 * that verifies the RAML definition, and generates HTML documentation. Note that in production systems, the RAML
 * definition will most likely live outside this project, possibly maintained by different teams. Nevertheless,
 * in this #Activator, I shall keep it in this module.
 */
trait HelloRoute extends HttpService with Directives with AskSupport with Json4sSupport {
  import HelloProtocol._

  implicit val ec: ExecutionContext
  implicit def timeout: Timeout
  implicit def json4sFormats: Formats = DefaultFormats

  lazy val helloRoute: Route =
    post {
      path("hello") {
        entity(as[Hello]) { cmd =>
          onComplete((actorRefFactory.actorSelection("/user/hello-actor") ? cmd).mapTo[HelloBack]) {
            case Success(v) => complete(v)
            case Failure(e) => complete(e)
          }
        }
      }
    }

}

class HelloActor extends PersistentActor {

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case _ => sender ! HelloBack("foo")
  }

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

}