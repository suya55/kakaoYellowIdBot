package service.store

import models.store.{ProductAction, OrdersAction}

object OrderService {
    def createOrder(userKey: String, productName: String): Int = {
        val product = ProductAction.findByName(productName)
        OrdersAction.createOrder(userKey, product.id)
    }

    def completeOrder(userKey: String): Unit = {
        OrdersAction.updateStatus(userKey,OrdersAction.STATUS_COMPLETE)
    }

    def requestPaymentCheck(userKey: String): Unit = {
        OrdersAction.updateStatus(userKey,OrdersAction.STATUS_CHECK_PAYMENT)
    }


    def updateReceiverName(userKey: String, name: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_name", name)
    }

    def updateReceiverPhone(userKey: String, phone: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_phone", phone)
    }

    def updateReceiverAddress(userKey: String, address: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_address", address)
    }

    def updateDeliverMessage(userKey: String, message: String): Unit = {
        OrdersAction.updateField(userKey, "deliver_message", message)
    }

    def updatePayerName(userKey: String, name: String): Unit = {
        OrdersAction.updateField(userKey, "payer", name)
    }
}

