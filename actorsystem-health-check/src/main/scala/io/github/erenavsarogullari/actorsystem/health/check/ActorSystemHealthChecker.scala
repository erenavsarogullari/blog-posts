package io.github.erenavsarogullari.actorsystem.health.check

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.LongAdder

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import io.github.erenavsarogullari.actorsystem.health.check.actor.{HeartbeatRequest, HeartbeatResponse}
import io.github.erenavsarogullari.actorsystem.health.check.status.HealthStatusType
import io.github.erenavsarogullari.actorsystem.health.check.utils.AkkaUtils
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object ActorSystemHealthChecker {

  private val logger = LoggerFactory.getLogger(classOf[ActorSystemHealthChecker])

  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  private val counter = new LongAdder()
  private val FailureThreshold = 3

  def apply(heartbeatActorRef: ActorRef)(implicit ec: ExecutionContext) = new ActorSystemHealthChecker(heartbeatActorRef)

}

class ActorSystemHealthChecker(heartbeatActorRef: ActorRef)(implicit ec: ExecutionContext) {

  import ActorSystemHealthChecker._
  import HealthStatusType._

  def checkActorSystem(): HealthStatusType = {
    try {
      checkHeartbeat()
      counter.intValue() match {
        case v if(v == 0) => UP
        case v if(v > 0 && v <= FailureThreshold) => UNKNOWN
        case v if(v > FailureThreshold) => DOWN
      }
    } catch {
      case NonFatal(t) => DOWN
    }
  }

  private def checkHeartbeat(): Unit = {
    val futureHeartbeatResponse = (heartbeatActorRef ? HeartbeatRequest).mapTo[HeartbeatResponse.type]
    futureHeartbeatResponse.onComplete {
      case Success(value) => {
        counter.reset()
        logger.info(s"${AkkaUtils.appActorSystem.name} Heartbeat Call is successful")
      }
      case Failure(t) => {
        counter.increment()
        logger.warn(s"${AkkaUtils.appActorSystem.name} Heartbeat Call is failed. ", t.getMessage)
      }
    }
  }

}
