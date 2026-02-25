package com.example.gocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gocab.data.FeedbackRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val repository: FeedbackRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _submitted = MutableStateFlow(false)
    val submitted: StateFlow<Boolean> = _submitted.asStateFlow()

    fun submit(rating: Int, comment: String) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: "guest"
            repository.submitFeedback(uid, rating, comment)
            _submitted.value = true
        }
    }
}