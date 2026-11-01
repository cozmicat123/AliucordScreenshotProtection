package com.example.screenshotprotection

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*

@AliucordPlugin
class ScreenshotProtection : Plugin() {
    override fun start(context: Context) {
        val flags = WindowManager.LayoutParams.FLAG_SECURE

        patcher.after<com.discord.app.AppActivity>("onCreate", Bundle::class.java) { param ->
            (param.thisObject as com.discord.app.AppActivity).window.addFlags(flags)
        }

        patcher.after<com.discord.app.AppActivity>("onResume") { param ->
            (param.thisObject as com.discord.app.AppActivity).window.addFlags(flags)
        }

        patcher.before<Window>("setFlags", Int::class.java, Int::class.java) { param ->
            val newFlags = param.args[0] as Int
            param.args[0] = newFlags or flags
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
