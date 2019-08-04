package io.github.erenavsarogullari.actorsystem.health.check

import akka.actor.ActorRef
import io.github.erenavsarogullari.actorsystem.health.check.actor.TriggerStackOverflowError
import io.github.erenavsarogullari.actorsystem.health.check.status.HealthStatusType

import scala.concurrent.duration._
import scala.language.postfixOps
import io.github.erenavsarogullari.actorsystem.health.check.utils.AkkaUtils
import org.slf4j.LoggerFactory

object ActorSystemHealthApp {

  private val logger = LoggerFactory.getLogger(ActorSystemHealthApp.getClass)

  def main(args: Array[String]): Unit = {
    setConfig()

    val actorSystemHealthChecker = ActorSystemHealthChecker(AkkaUtils.heartbeatActorRef)(AkkaUtils.appHealthEC)
    val actorSystemCheckRunnable = new ActorSystemCheckRunnable(actorSystemHealthChecker)
    AkkaUtils.appHealthActorSystem.scheduler.schedule(0 seconds, 5 seconds, actorSystemCheckRunnable)(AkkaUtils.appHealthEC)

    val errorOnActorSystemRunnable = new ErrorOnActorSystemRunnable(AkkaUtils.stackOverflowErrorActorRef)
    AkkaUtils.appActorSystem.scheduler.scheduleOnce(12 seconds, errorOnActorSystemRunnable)(AkkaUtils.appEC)
  }

  private def setConfig(): Unit = {
    import com.typesafe.config.Config
    import com.typesafe.config.ConfigFactory

    System.setProperty("akka.jvm-exit-on-fatal-error", "off")
    val conf: Config = ConfigFactory.load()
    require(conf.getString("akka.jvm-exit-on-fatal-error") == "off", "akka.jvm-exit-on-fatal-error should be off")
  }

  private class ActorSystemCheckRunnable(actorSystemHealthChecker: ActorSystemHealthChecker) extends Runnable {

    override def run(): Unit = {
      actorSystemHealthChecker.checkActorSystem() match {
        case t @ _ => logger.info(s"${AkkaUtils.appActorSystem.name} is ${t.toString}")
      }
    }

  }

  private class ErrorOnActorSystemRunnable(stackOverflowErrorActorRef: ActorRef) extends Runnable {

    override def run(): Unit = stackOverflowErrorActorRef ! TriggerStackOverflowError

  }

}
