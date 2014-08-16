package org.eigengo.cqrsrest.write

import akka.actor.{ActorRefFactory, ActorSelection}

/**
 * Groups the actors so that their names do not diverge; it also contains convenience functions for actor lookups
 */
object actors {

  /**
   * The ExerciseActor definition.
   */
  object exercise {
    /** The actor name */
    val name = "exercise-actor"
    /** The lookup function */
    def apply(implicit arf: ActorRefFactory): ActorSelection = arf.actorSelection(name)
  }

}
