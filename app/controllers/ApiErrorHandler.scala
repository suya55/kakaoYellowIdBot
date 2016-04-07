package controllers

import exceptions.ServiceException
import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent._

class ApiErrorHandler extends HttpErrorHandler {
    def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
        Future.successful(

            Status(statusCode)(Json.obj("code" -> 501, "http_status"->statusCode))
        )
    }

    def onServerError(request: RequestHeader, exception: Throwable) = {
        Future.successful(
            exception match {
                case e: ServiceException =>
                    Logger.warn(s"ServiceException [Code:${e.code}, Msg:${e.message}, URI:${request.uri}]")
                    e.toDefaultKeyboardResponse
                case ex: Throwable =>
                    Logger.error(s"Runtime Exception. - ${request.method} ${request.path}", ex)
                    play.api.mvc.Results.InternalServerError(Json.obj("code" -> 500, "message" -> "Internal server error."))
            }
        )
    }
}
