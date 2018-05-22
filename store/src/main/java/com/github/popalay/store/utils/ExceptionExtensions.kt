package com.github.popalay.store.utils

import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun Throwable.isConnectivityExceptions() = this is ConnectException
        || this is UnknownHostException
        || this is SocketTimeoutException
        || this is SocketException