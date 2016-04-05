package utils

import scala.reflect.runtime.{universe => ru}

trait ObjectCaller {
    def callMethod[T](objName: String, methodName: String, parameter1: String,parameter2: String,parameter3: String): T = {
        val (objMirror, method) = getObjMirror(objName,methodName)
        objMirror.reflectMethod(method)(parameter1, parameter2, parameter3).asInstanceOf[T]
    }

    def callMethod[T](objName: String, methodName: String, parameter1: String,parameter2: String): T = {
        val (objMirror, method) = getObjMirror(objName,methodName)
        objMirror.reflectMethod(method)(parameter1, parameter2).asInstanceOf[T]
    }

    def callMethod[T](objName: String, methodName: String, parameter: String): T = {
        val (objMirror, method) = getObjMirror(objName,methodName)
        objMirror.reflectMethod(method)(parameter).asInstanceOf[T]
    }

    protected def getObjMirror(objName:String, methodName:String) ={
        val m = ru.runtimeMirror(getClass.getClassLoader)
        val module = m.staticModule(s"service.store.${objName}")
        val im = m.reflectModule(module)
        val method = im.symbol.info.decl(ru.TermName(methodName)).asMethod

        (m.reflect(im.instance),method)
    }
}
object ObjectCaller extends ObjectCaller