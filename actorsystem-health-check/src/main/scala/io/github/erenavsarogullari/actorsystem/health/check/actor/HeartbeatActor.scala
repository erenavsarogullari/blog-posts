package io.github.erenavsarogullari.actorsystem.health.check.actor

import akka.actor.{Actor, Props}

case object HeartbeatRequest
case object HeartbeatResponse

object HeartbeatActor {
  def props() = Props(new HeartbeatActor())
}

class HeartbeatActor extends Actor {

  override def receive: Receive = {
    case HeartbeatRequest => sender ! HeartbeatResponse
  }

}
