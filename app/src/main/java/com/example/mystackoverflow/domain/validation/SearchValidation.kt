package com.example.mystackoverflow.domain.validation

sealed class SearchValidationResult {
    object Valid : SearchValidationResult()
    data class Invalid(val message: String) : SearchValidationResult()
}

object SearchValidator {
    fun validate(query: String): SearchValidationResult {
        return when {
            query.isEmpty() -> SearchValidationResult.Invalid("Enter three or more characters")
            query.length == 1 -> SearchValidationResult.Invalid("Two more characters required")
            query.length == 2 -> SearchValidationResult.Invalid("One more character required")
            else -> SearchValidationResult.Valid
        }
    }
} 