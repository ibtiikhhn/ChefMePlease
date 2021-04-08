package com.muibsols.chefmeplease.UI.Activities.Stripe

import android.app.Application
import com.muibsols.chefmeplease.R
import com.stripe.android.BuildConfig
import com.stripe.android.PaymentConfiguration

class FirebaseMobilePaymentsApp : Application(){
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(applicationContext, getString(R.string.stripePublishableKey))
    }
}