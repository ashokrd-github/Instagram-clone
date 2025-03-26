package com.yaduvanshi_ashok_rd.likee.Model

import com.google.firebase.Timestamp

class Comment {

    private var publisher:String=""
    private var comment:String=""
     var timestamp:Long=0

    constructor()

    constructor(publisher: String, comment: String, timestamp: Long) {
        this.publisher = publisher
        this.comment = comment
        this.timestamp = timestamp
    }

    fun getPublisher():String{
        return publisher
    }
    fun getComment():String{
        return comment
    }

    fun setPublisher(publisher: String)
    {
        this.publisher=publisher
    }

    fun setComment(comment: String)
    {
        this.comment=comment
    }

}