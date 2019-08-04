package io.github.erenavsarogullari.actorsystem.health.check.actor

import akka.actor.{Actor, Props}

case object TriggerStackOverflowError

object StackOverflowErrorActor {
  def props() = Props(new StackOverflowErrorActor())
}

class StackOverflowErrorActor extends Actor {
  override def receive: Receive = {
    case TriggerStackOverflowError => throw new StackOverflowError("StackOverflow Error occurred!!!")
  }
}
