package org.eigengo.cqrsrest.write

import akka.pattern.AskSupport
import akka.persistence.PersistentActor
import akka.util.Timeout
import org.eigengo.cqrsrest.write.ExerciseProtocol.ExerciseBack
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport
import spray.routing.{HttpService, Directives, Route}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Defines the REST protocol for the ``ExerciseRoute``. At this moment, it includes the ``Exercise`` request and
 * ``ExerciseBack`` response.
 *
 * It is expected that these values will be marshalled and unmarshalled into JSON
 */
object ExerciseProtocol {
  sealed trait ExerciseActorMessage
  case class Exercise(what: String) extends ExerciseActorMessage
  case class ExerciseBack(result: String) extends ExerciseActorMessage
}

/**
 * Defines the ``Route`` that handles the ``/exercise`` URLs. The route uses the protocol defined above, and uses
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
trait ExerciseRoute extends HttpService with Directives with AskSupport with Json4sSupport {
  import ExerciseProtocol._

  implicit val ec: ExecutionContext
  implicit def timeout: Timeout
  implicit def json4sFormats: Formats = DefaultFormats

  lazy val exerciseRoute: Route =
    post {
      path("exercise") {
        entity(as[Exercise]) { cmd =>
          onComplete((actors.exercise.apply ? cmd).mapTo[ExerciseBack]) {
            case Success(v) => complete(v)
            case Failure(e) => complete(e.getMessage)
          }
        }
      }
    }

}

/**
 * This actor collects the performed exercise
 */
class ExerciseActor extends PersistentActor {

  // TODO: make me do something interesting

  override def receiveRecover: Receive = {
    case _ => sender ! ExerciseBack("recover")
  }

  override def receiveCommand: Receive = {
    case _ => sender ! ExerciseBack("foo")
  }

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

}