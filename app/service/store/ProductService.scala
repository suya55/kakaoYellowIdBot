package service.store

import models.store.ProductAction

object ProductService {
    def getAllName(userKey:String, input:String) : String = ProductAction.getProductNames().mkString("|")
    def productMessage(userKey:String, input:String):String = {
        val product = ProductAction.findByName(input)
        s"""
           |[${product.name}]
           |<img>${product.imageSrc.get}</img>
           |${product.description}
         """.stripMargin
    }
}
