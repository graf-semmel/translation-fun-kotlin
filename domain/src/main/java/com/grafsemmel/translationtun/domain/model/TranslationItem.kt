package com.grafsemmel.translationtun.domain.model

import java.util.*

data class TranslationItem(
        val text: String,
        val translation: String,
        val source: String,
        val target: String,
        val date: Date,
        val views: Int = 1
) 
