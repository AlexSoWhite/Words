package com.nafanya.words.core.coroutines

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

class AppDispatchers @Inject constructor() {

    val io = Dispatchers.IO
    val default = Dispatchers.Default
    val unconfined = Dispatchers.Unconfined
    val main = Dispatchers.Main
}
