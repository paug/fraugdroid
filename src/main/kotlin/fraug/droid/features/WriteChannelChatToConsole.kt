package fraug.droid.features

import com.github.philippheuer.events4j.annotation.EventSubscriber
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import java.util.*

object WriteChannelChatToConsole {

    /** Subscribe to the ChannelMessage Event and write the output to the console */
    @EventSubscriber
    fun onChannelMessage(event: ChannelMessageEvent) {
        //println("${event.user.name}: ${event.message}")
        val response = when {
            event.message.startsWith("!help") -> "bit.ly/fraugdroid"
            event.message.startsWith("!zoom") -> "bit.ly/fraugzoom"
            event.message.startsWith("!slido") -> "bit.ly/fraugslido"
            event.message.startsWith("!openfeedback") -> "openfeedback.io/fraug2/"
            event.message.startsWith("!fish") -> addAttempt(event)
            event.message.startsWith("!leaderboard") -> Leaderboard.summary()
            event.message.startsWith("!hug") -> hug(event)
            else -> null
        }

        if (response  != null) {
            event.twitchChat.sendMessage(event.channel.name, response)
        }
    }

    private fun hug(event: ChannelMessageEvent): String? {
        var username = event.message.substringAfter("!hug").trim()
        if (username.isNullOrBlank()) {
            username = event.user.name
        }
        return "FraugDroid envoie un hug à ${username} <3 <3"
    }

    private fun addAttempt(event: ChannelMessageEvent): String? {
        fishQueries.addAttempt(event.user.name, Date().time)
        val visibleFish = try {
            fishQueries.selectLastVisibleFish().executeAsOne()
        } catch (e: Exception) {
            null
        }
        return when {
            visibleFish != null -> listOf("🎣", "🐟", "🐠", "🐡").random()
            else  -> listOf("👞", "👠", "🥾", "⛸").random()
        }
    }
}
