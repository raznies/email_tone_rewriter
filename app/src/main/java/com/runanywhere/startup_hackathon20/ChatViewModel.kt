package com.runanywhere.startup_hackathon20

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import com.runanywhere.sdk.models.ModelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Email Rewrite Data Class
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val originalEmail: String? = null,
    val tone: String? = null,
    val isRewriteResult: Boolean = false
)

// ViewModel
class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId

    private val _statusMessage = MutableStateFlow<String>("Initializing...")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _selectedTone = MutableStateFlow<String>("Professional")
    val selectedTone: StateFlow<String> = _selectedTone

    private val _originalEmail = MutableStateFlow<String>("")
    val originalEmail: StateFlow<String> = _originalEmail

    private val _rewrittenEmail = MutableStateFlow<String>("")
    val rewrittenEmail: StateFlow<String> = _rewrittenEmail

    init {
        loadAvailableModels()
    }

    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                val models = listAvailableModels()
                _availableModels.value = models
                _statusMessage.value = "Ready - Please download and load a model"
            } catch (e: Exception) {
                _statusMessage.value = "Error loading models: ${e.message}"
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Downloading model..."
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                }
                _downloadProgress.value = null
                _statusMessage.value = "Download complete! Please load the model."
            } catch (e: Exception) {
                _statusMessage.value = "Download failed: ${e.message}"
                _downloadProgress.value = null
            }
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Loading model..."
                val success = RunAnywhere.loadModel(modelId)
                if (success) {
                    _currentModelId.value = modelId
                    _statusMessage.value = "Model loaded! Ready to rewrite emails."
                } else {
                    _statusMessage.value = "Failed to load model"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error loading model: ${e.message}"
            }
        }
    }

    fun rewriteEmail(emailText: String, tone: String) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "Please load a model first"
            return
        }

        if (emailText.isBlank()) {
            _statusMessage.value = "Please enter an email to rewrite"
            return
        }

        // Store original email and tone
        _originalEmail.value = emailText
        _selectedTone.value = tone
        _rewrittenEmail.value = ""

        // Build tone-aware prompt
        val prompt = buildEmailRewritePrompt(emailText, tone)

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Rewriting in $tone tone..."

            try {
                // Generate rewritten email with streaming
                var rewrittenText = ""
                RunAnywhere.generateStream(prompt).collect { token ->
                    rewrittenText += token
                    _rewrittenEmail.value = rewrittenText.trim()
                }
                _statusMessage.value = "Rewrite complete!"
            } catch (e: Exception) {
                _rewrittenEmail.value = "Error: ${e.message}"
                _statusMessage.value = "Rewrite failed: ${e.message}"
            }

            _isLoading.value = false
        }
    }

    private fun buildEmailRewritePrompt(email: String, tone: String): String {
        return when (tone.lowercase()) {
            "professional" -> """Rewrite this email in a professional and polished tone. Keep the core message but use formal business language:

$email

Rewritten email:"""
            "friendly" -> """Rewrite this email in a warm and friendly tone. Keep the message casual and approachable:

$email

Rewritten email:"""
            "concise" -> """Rewrite this email to be brief and to-the-point. Remove unnecessary words while keeping the key information:

$email

Rewritten email:"""
            "formal" -> """Rewrite this email in a highly formal and respectful tone suitable for senior executives or official correspondence:

$email

Rewritten email:"""
            else -> """Rewrite this email in a $tone tone:

$email

Rewritten email:"""
        }
    }

    fun setTone(tone: String) {
        _selectedTone.value = tone
    }

    fun clearResult() {
        _originalEmail.value = ""
        _rewrittenEmail.value = ""
        _statusMessage.value = "Ready to rewrite emails."
    }

    fun refreshModels() {
        loadAvailableModels()
    }
}
