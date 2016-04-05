package models.store

import utils.ObjectCaller

object ActionMethodCaller extends ObjectCaller {
    override def callMethod[T](objName: String, methodName: String, parameter: String, inputMessage: String, userKey: String): T = {
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
