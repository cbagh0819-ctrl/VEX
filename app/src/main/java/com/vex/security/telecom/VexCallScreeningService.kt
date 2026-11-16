package com.vex.security.telecom

import android.telecom.Call
import android.telecom.CallScreeningService

class VexCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart ?: ""
        val response = CallResponse.Builder()

        val shouldBlock = BombShieldManager.shouldBlockCall(number)

        if (shouldBlock) {
            response
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSilenceCall(true)
                .setSkipNotification(false)
        } else {
            response.setDisallowCall(false)
        }

        respondToCall(callDetails, response.build())
    }
}
