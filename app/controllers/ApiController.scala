package controllers

import play.api.mvc.Controller
import utils.Slick

trait ApiController extends Controller with Actions with Slick{
    def message
    def keyboard
}