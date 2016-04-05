package base

import models.store.{UserStepAction, StepAction, ProductAction, OrdersAction}
import org.specs2.mock.Mockito
import org.specs2.specification.{BeforeAfterAll, BeforeAfterEach}
import play.api.{Application, Logger}
import play.api.test.{FakeApplication, PlaySpecification}
import utils.Slick
import slick.driver.MySQLDriver.api._
import scala.concurrent.Await

trait SpecBase extends PlaySpecification with Mockito with BeforeAfterEach with BeforeAfterAll with Slick{
    val logger = Logger.logger

    val app = {
        val application = FakeApplication(
            additionalConfiguration = Map("isTestApp" -> true))
        play.api.Play.start(application)
        application
    }

    private def setupData(application: Application) {
        if (application.configuration.getBoolean("isTestApp").getOrElse(false)) {
            Await.result( db.run(
                DBIO.seq(
                    OrdersAction.delete,
                    ProductAction.delete,
//                    StepAction.delete,
                    UserStepAction.delete
                )
            ),duration)
        }

    }

    def beforeAll(): Unit = {
    }

    override protected def before: Any = {
        setupData(app)
    }

    override protected def after: Any = {

    }

    def afterAll(): Unit = {
    }
}
