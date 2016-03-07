package controllers

import play.api.Logger
import play.api.mvc._

import scala.concurrent.Future

trait Actions {

    object LoggingAction extends ActionBuilder[Request] {
        def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
            Logger.info(s"${request.method} ${request.uri} ${request.body}")
            block(request)
        }
    }

}