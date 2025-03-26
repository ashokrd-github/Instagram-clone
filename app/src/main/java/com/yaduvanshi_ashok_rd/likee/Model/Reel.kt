package com.yaduvanshi_ashok_rd.likee.Model



class Reel {
    private var reelId:String=""
    private var reels:String=""
    private var publisher:String=""
    private var caption:String=""

    constructor(){}

    constructor(reelId: String, reels: String, publisher: String, caption: String) {
        this.reelId = reelId
        this.reels = reels
        this.publisher = publisher
        this.caption = caption
    }

    //getters
    fun getReelId():String{
        return reelId
    }

    fun getReels():String{
        return reels
    }
    fun getPublisher():String{
        return publisher
    }
    fun getCaption():String{
        return caption
    }


    //setters


    fun setReelId(reelId: String)
    {
        this.reelId=reelId
    }

    fun setReels(reels: String)
    {
        this.reels=reels
    }

    fun setPublisher(publisher: String)
    {
        this.publisher=publisher
    }

    fun setCaption(caption: String)
    {
        this.caption=caption
    }
}




