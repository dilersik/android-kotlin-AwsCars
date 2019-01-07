package com.dilerdesenvolv.carros.provider

import android.content.SearchRecentSuggestionsProvider
/**
 * Created by dilerdesenvolv on 10/11/2017.
 */
class BuscaProvider : SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        val AUTHORITY = "com.dilerdesenvolv.carros.provider.BuscaProvider"
        val MODE = DATABASE_MODE_QUERIES
    }

}