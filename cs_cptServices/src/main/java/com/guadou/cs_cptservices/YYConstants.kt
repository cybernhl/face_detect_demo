package com.guadou.cs_cptservices

class YYConstants {

    companion object {

        const val BASE_URL = "http://yyjobs-api-dev.guabean.com"
        const val NETWORK_CONTENT_TYPE = "application/x-www-form-urlencoded"
        const val NETWORK_ACCEPT_V1 = "application/x.yyjobs-api.v1+json"
        const val NETWORK_ACCEPT_V9 = "application/x.yyjobs-api.v9+json"

        const val APP_ID = "EfQHE72TVT9U9qebXPFzjpCkAZWETCpzPodHhRJ6sQvQ"
        const val SDK_KEY = "2jFw3qo8ES5pqBPZSayFwV4etoCkoo8dJKwCEL83wJkP"

        /**
         * IR预览数据相对于RGB预览数据的横向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
         */
        const val HORIZONTAL_OFFSET = 0

        /**
         * IR预览数据相对于RGB预览数据的纵向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
         */
        const val VERTICAL_OFFSET = 0


        //Key----------------->
        const val SP_KEY_TOKEN = "user_token"   //用户token
    }

}