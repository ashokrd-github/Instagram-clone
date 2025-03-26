package com.yaduvanshi_ashok_rd.likee.Model

import com.google.firebase.Timestamp

class Message {
    var message:String? = null
    var senderId:String? = null
    var messageId:String? = null
    var imageUrl:String? = null
    var videoUrl:String?=null
    var timestamp:Long=0


    constructor(){}
    constructor(
        message: String?,
        senderId: String?,
        timestamp: Long
    ) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }




}