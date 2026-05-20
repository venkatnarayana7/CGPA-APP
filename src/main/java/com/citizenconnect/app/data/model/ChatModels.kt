package com.citizenconnect.app.data.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Gemini API Request Models
data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

// Gemini API Response Models
data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ResponseContent?
)

data class ResponseContent(
    val parts: List<Part>?
)
