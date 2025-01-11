package com.koren.data.services

sealed class SignInMethod {
    data class Email(val email: String, val password: String): SignInMethod()
    data object Google: SignInMethod()
}