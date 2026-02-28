package com.ale.quickscore.features.scores.data.datasources.remote.api

import com.ale.quickscore.features.rooms.data.datasources.remote.model.RankingDto
import com.ale.quickscore.features.scores.data.datasources.remote.model.AddPointsRequest
import com.ale.quickscore.features.scores.data.datasources.remote.model.ResetPointsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScoresApi {
    @POST("rooms/{code}/score")
    suspend fun addScore(
        @Path("code") code: String,
        @Body body: AddPointsRequest
    ): Response<Map<String, String>>

    @GET("rooms/{code}/ranking")
    suspend fun getRanking(@Path("code") code: String): Response<List<RankingDto>>

    @POST("rooms/{code}/score/reset")
    suspend fun resetUserPoints(
        @Path("code") code: String,
        @Body body: ResetPointsRequest
    ): Response<Map<String, String>>

    @POST("rooms/{code}/score/reset-all")
    suspend fun resetAllPoints(
        @Path("code") code: String
    ): Response<Map<String, String>>
}
