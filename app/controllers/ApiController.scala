package controllers

import models.cook.DaumCook
import models.{KeyboardType, Keyboard}
import play.api.mvc.{BodyParsers, Controller}

trait ApiController extends Controller with Actions{
    def message
    def keyboard
}