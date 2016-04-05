package controllers

import models.store.{UserStep, UserStepAction}
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.Future

trait Actions {
    private def jsonBody[A](body: A): Option[JsValue] = body match {
        case js: JsValue => Some(js)
        case any: AnyContent => any.asJson
    }
    object LoggingAction extends ActionBuilder[Request] {
        def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
            Logger.info(s"${request.method} ${request.uri} ${request.body}")
            block(request)
        }
    }
    case class UserStepRequest[A](val userKey:String, userStep:UserStep, request :Request[A]) extends WrappedRequest[A](request)
    object UserStepsAction extends ActionBuilder[UserStepRequest] with ActionTransformer[Request, UserStepRequest] {
        def transform[A](request: Request[A]) = Future.successful {
            val jsBody = jsonBody(request.body).get
            val userKey:String = (jsBody\"user_key").as[String]
            val userStep = UserStepAction.findOrCreateByUserKey(userKey)
            UserStepRequest(userKey, userStep, request)
        }
    }

    val userAction = LoggingAction andThen UserStepsAction
}