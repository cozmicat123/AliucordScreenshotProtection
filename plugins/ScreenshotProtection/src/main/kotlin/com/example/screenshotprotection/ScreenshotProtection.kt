package com.example.screenshotprotection

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*

@AliucordPlugin
class ScreenshotProtection : Plugin() {
    override fun start(context: Context) {
        patcher.after<com.discord.app.AppActivity>("onCreate", Bundle::class.java) { param ->
            val activity = param.thisObject as com.discord.app.AppActivity
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
