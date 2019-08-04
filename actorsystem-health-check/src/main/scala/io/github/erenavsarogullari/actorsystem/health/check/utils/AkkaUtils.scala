package io.github.erenavsarogullari.actorsystem.health.check.utils

import akka.actor.ActorSystem
import io.github.erenavsarogullari.actorsystem.health.check.actor.{HeartbeatActor, StackOverflowErrorActor}

object AkkaUtils {

  private def createActorSystem(name: String) = ActorSystem(name)

  val appActorSystem = createActorSystem("app-actor-system")
  val appHealthActorSystem = createActorSystem("app-health-actor-system")

  implicit val appHealthEC = appHealthActorSystem.dispatcher
  implicit val appEC = appActorSystem.dispatcher

  val heartbeatActorRef = appActorSystem.actorOf(HeartbeatActor.props(), "heartbeat-actor")
  val stackOverflowErrorActorRef = appActorSystem.actorOf(StackOverflowErrorActor.props(), "stackoverflow-error-actor")

}
