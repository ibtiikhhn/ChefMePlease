package com.muibsols.chefmeplease.Services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    val TAG = "mFirebaseIIDService"
    val SUBSCRIBE_TO="userABC"

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);

    }
}