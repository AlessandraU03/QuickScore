package com.ale.quickscore.features.rooms.domain.entities

import com.google.gson.JsonElement

data class WsMessage(
    val event: String,
    val room: String,
    val payload: JsonElement?
)

object WsEvents {
    // Scores
    const val SCORE_UPDATE             = "score_update"
    // Session
    const val SESSION_STARTED          = "session_started"
    const val SESSION_ENDED            = "session_ended"
    // Presencia
    const val PARTICIPANT_CONNECTED    = "participant_connected"
    const val PARTICIPANT_DISCONNECTED = "participant_disconnected"
    const val ONLINE_LIST              = "online_list"
    const val PARTICIPANT_KICKED       = "participant_kicked"
    // Preguntas
    const val NEW_QUESTION             = "new_question"
    const val QUESTION_CLOSED          = "question_closed"
    const val ANSWER_CORRECT           = "answer_correct"
}
