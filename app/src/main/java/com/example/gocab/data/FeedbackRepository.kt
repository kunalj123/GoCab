package com.example.gocab.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun submitFeedback(userId: String, rating: Int, comment: String) {
        val payload = mapOf(
            "userId" to userId,
            "rating" to rating,
            "comment" to comment,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("feedback").add(payload).await()
    }
}