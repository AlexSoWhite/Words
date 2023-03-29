package com.nafanya.words.core.coroutines

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class IOCoroutineProvider @Inject constructor(
    appDispatchers: AppDispatchers
) {

    val ioScope = CoroutineScope(Job() + appDispatchers.io)
}
