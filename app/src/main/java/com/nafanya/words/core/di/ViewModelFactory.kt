package com.nafanya.words.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.MapKey
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class ViewModelFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        providers[modelClass]?.let {
            return it.get() as T
        }
        throw IllegalArgumentException("No such provider for" + modelClass.canonicalName)
    }
}

@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
