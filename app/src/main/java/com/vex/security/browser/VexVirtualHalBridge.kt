package com.vex.security.browser

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.vex.security.defense.InterceptedBehaviorLog
import com.vex.security.defense.VexAiDefenseEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VexVirtualHalBridge(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    @JavascriptInterface
    fun onMediaAccessIntercepted(callerUrl: String, constraintsJson: String) {
        Log.i("VEX_VIRTUAL_HAL", "🚨 Intercepted web media stream request from: $callerUrl (constraints: $constraintsJson)")

        scope.launch {
            val log = InterceptedBehaviorLog(
                eventType = "MEDIA_DEVICES_GETUSERMEDIA",
                sourcePackage = context.packageName,
                callerUrl = callerUrl,
                requestedConstraints = constraintsJson,
                isBackgroundProcess = false
            )

            // Gemma 2 2B evaluates the behavioral intent
            val evaluation = VexAiDefenseEngine.evaluateBehavior(context, log)
            Log.i("VEX_VIRTUAL_HAL", "Gemma 2 2B Evaluation: Score ${evaluation.threatScore} | Action: ${evaluation.mitigationAction} | Mitre: ${evaluation.mitreTechnique}")

            if (evaluation.isThreat || evaluation.mitigationAction == "DECOY_CANARY_FEED") {
                // Command Virtual HAL decoy feed and trigger central SOC radar lock
                VexAiDefenseEngine.dispatchCanaryTelemetry(
                    context = context,
                    tokenId = "honeypot_selfie",
                    evaluation = evaluation
                )
            }
        }
    }
}
