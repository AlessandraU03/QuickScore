package com.ale.quickscore.features.rooms.domain.entities

data class WsMessage(
    val event: String,
    val room: String,
    val payload: Map<String, Any>?
)

object WsEvents {
    const val SCORE_UPDATE    = "score_update"
    const val SESSION_STARTED = "session_started"
    const val SESSION_ENDED   = "session_ended"
    const val USER_JOINED     = "user_joined"
}