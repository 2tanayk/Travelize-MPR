package com.myapp.travelize

object Keys {
    init {
        System.loadLibrary("native-lib")
    }

    external fun apiKey(): String
}