package com.nafanya.words.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

suspend inline fun <reified A, reified R> ((A) -> R).inScope(
    scope: CoroutineScope,
    args: A
) {
    withContext(scope.coroutineContext) {
        return@withContext this@inScope(args)
    }
}
