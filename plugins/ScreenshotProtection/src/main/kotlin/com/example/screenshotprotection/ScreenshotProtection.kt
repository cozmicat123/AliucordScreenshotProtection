package com.example.screenshotprotection

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.settings.delegate
import com.discord.stores.StoreStream

@AliucordPlugin
class ScreenshotProtection : Plugin() {
    private var SettingsAPI.protectedChannels: ArrayList<Long> by settings.delegate(arrayListOf())

    private val currentChannelId: Long
        get() = StoreStream.getChannelsSelected().id

    override fun start(context: Context) {
        val flags = WindowManager.LayoutParams.FLAG_SECURE

        patcher.after<com.discord.app.AppActivity>("onCreate", Bundle::class.java) { param ->
            applyFlag(param.thisObject as com.discord.app.AppActivity)
        }

        patcher.after<com.discord.app.AppActivity>("onResume") { param ->
            applyFlag(param.thisObject as com.discord.app.AppActivity)
        }

        patcher.before<Window>("setFlags", Int::class.java, Int::class.java) { param ->
            val newFlags = param.args[0] as Int
            param.args[0] = if (currentChannelId in protectedChannels) {
                newFlags or flags
            } else {
                newFlags and flags.inv()
            }
        }

        commands.registerCommand(
            "toggleprotection",
            "Toggle screenshot protection for the current channel"
        ) { _ ->
            val channelId = currentChannelId
            if (channelId in protectedChannels) {
                protectedChannels.remove(channelId)
                CommandsAPI.CommandResult("Screenshot protection disabled for this channel.")
            } else {
                protectedChannels.add(channelId)
                CommandsAPI.CommandResult("Screenshot protection enabled for this channel.")
            }
        }
    }

    private fun applyFlag(activity: com.discord.app.AppActivity) {
        val flags = WindowManager.LayoutParams.FLAG_SECURE
        if (currentChannelId in protectedChannels) {
            activity.window.addFlags(flags)
        } else {
            activity.window.clearFlags(flags)
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}
