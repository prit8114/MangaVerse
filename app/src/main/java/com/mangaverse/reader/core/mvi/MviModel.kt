package com.mangaverse.reader.core.mvi

import kotlinx.coroutines.flow.StateFlow

/**
 * Base interface for MVI architecture Model component.
 * 
 * @param STATE The type of state managed by this model
 * @param INTENT The type of intents this model can handle
 * @param EFFECT The type of side effects this model can emit
 */
interface MviModel<STATE, INTENT, EFFECT> {
    /**
     * The current state as an observable StateFlow
     */
    val state: StateFlow<STATE>
    
    /**
     * Process a new intent
     * 
     * @param intent The intent to process
     */
    fun processIntent(intent: INTENT)
}