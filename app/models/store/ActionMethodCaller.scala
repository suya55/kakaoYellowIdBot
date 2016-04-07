package models.store

import play.api.Logger
import utils.ObjectCaller

object ActionMethodCaller extends ObjectCaller {
    override def callMethod[T](objName: String, methodName: String, parameter: String, inputMessage: String, userKey: String): T = {
        Logger.debug(s"===>> ActionMethodCaller objName: ${objName}, methodName: ${methodName}, parameter: ${parameter}")
        val (objMirror, method) = getObjMirror(objName, methodName)
        val param: String = parameter match {
            case "#{prevInputMessage}" => UserStepAction.findOptByUserKey(userKey).get.inputMessage.get
            case "#{inputMessage}" => inputMessage
            case _ => null
        }
        if (null == param)
            objMirror.reflectMethod(method)(userKey).asInstanceOf[T]
        else
            objMirror.reflectMethod(method)(userKey, param).asInstanceOf[T]

    }
}

object MessageMethodCaller extends ObjectCaller {
    override def callMethod[T](objName: String, methodName: String, inputMessage: String, userKey: String): T = {
        val (objMirror, method) = getObjMirror(objName, methodName)
        objMirror.reflectMethod(method)(userKey, inputMessage).asInstanceOf[T]

    }
}