package service.store

import models.store.ProductAction

object ProductService {
    def getAllName(userKey:String, input:String):String = ProductAction.getProductNames().mkString(",")
    def productMessage(userKey:String, input:String):String = {
        val product = ProductAction.findByName(input)
        s"[${product.name}]\n${product.description}\n남은 갯수 : ${product.remainCount}개\n가격 : ${product.price}"
    }
}
