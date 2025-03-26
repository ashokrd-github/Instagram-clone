package com.yaduvanshi_ashok_rd.likee.Model

import com.google.firebase.Timestamp

class Notification {
    private var userid:String=""
    private var text:String=""
    private var postid:String=""
    private var ispost:Boolean=false
     var timestamp:Long=0


    constructor()

    constructor(userid: String,text: String,postid:String,ispost:Boolean, timestamp: Long) {
        this.userid=userid
        this.text=text
        this.postid=postid
        this.ispost= ispost
        this.timestamp = timestamp
    }

    fun getPostId():String{
        return postid
    }

    fun getUserId():String{
        return userid
    }
    fun getText():String{
        return text
    }
    fun getIsPost():Boolean{
        return ispost
    }

    fun setPostId(postid: String){
        this.postid= postid
    }

    fun setUserId(userid: String){
        this.userid= userid
    }

    fun setText(text: String){
        this.text= text
    }

    fun setIsPost(ispost: Boolean){
        this.ispost= ispost
    }
}