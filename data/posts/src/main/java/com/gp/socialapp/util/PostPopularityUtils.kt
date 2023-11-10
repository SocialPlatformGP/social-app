package com.gp.socialapp.util

object PostPopularityUtils {
    private const val VOTE_WIEGHT = 2
    private const val REPLY_WIEGHT = 3
    fun calculateInteractionValue(votes: Int, replies: Int): Int{
        return votes * VOTE_WIEGHT + replies * REPLY_WIEGHT
    }
}