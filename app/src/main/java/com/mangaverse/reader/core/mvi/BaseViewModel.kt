package com.mangaverse.reader.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementation for MVI architecture.
 * Handles state updates and side effects.
 *
 * @param STATE The type of state managed by this ViewModel
 * @param INTENT The type of intents this ViewModel can handle
 * @param EFFECT The type of side effects this ViewModel can emit
 */
abstract class BaseViewModel<STATE, INTENT, EFFECT> : ViewModel(), MviModel<STATE, INTENT, EFFECT> {

    // The initial state should be provided by the implementing class
    private val initialState: STATE by lazy { createInitialState() }
    
    // Internal mutable state flow
    private val _state = MutableStateFlow(initialState)
    
    // Public immutable state flow
    override val state: StateFlow<STATE> = _state.asStateFlow()
    
    // Channel for side effects
    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    
    // Current state shorthand
    protected val currentState: STATE
        get() = _state.value
    
    /**
     * Create the initial state for this ViewModel
     */
    abstract fun createInitialState(): STATE
    
    /**
     * Handle the given intent and perform the necessary actions
     */
    abstract fun handleIntent(intent: INTENT)
    
    /**
     * Process a new intent
     */
    override fun processIntent(intent: INTENT) {
        handleIntent(intent)
    }
    
    /**
     * Update the current state
     */
    protected fun setState(reduce: STATE.() -> STATE) {
        val newState = currentState.reduce()
        _state.value = newState
    }
    
    /**
     * Emit a side effect
     */
    protected fun emitEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}