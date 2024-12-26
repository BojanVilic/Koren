package com.koren.common.services

class UserNotLoggedInException(override val message: String) : IllegalStateException()