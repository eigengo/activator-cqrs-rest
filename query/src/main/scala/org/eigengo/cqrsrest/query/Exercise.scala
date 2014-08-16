package org.eigengo.cqrsrest.query

import akka.pattern.AskSupport
import akka.util.Timeout
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport
import spray.routing._

import scala.concurrent.ExecutionContext

object ExerciseProtocol {

  case class Exercise(what: String, duration: Int)

}

trait ExerciseRoute extends HttpService with Directives with AskSupport with Json4sSupport {
  import org.eigengo.cqrsrest.query.ExerciseProtocol._

  implicit val ec: ExecutionContext
  implicit def timeout: Timeout
  implicit def json4sFormats: Formats = DefaultFormats

  lazy val exerciseRoute: Route =
    get {
      path("exercise") {
        complete(List(Exercise("gym::abs", 30), Exercise("cycle::intervals", 60), Exercise("cycle::hills", 124)))
      }
    }

}
