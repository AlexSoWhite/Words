package com.nafanya.words.core.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.di.ViewModelFactory
import com.nafanya.words.core.di.WordApplication
import dagger.Lazy
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    @Inject
    lateinit var factory: Lazy<ViewModelFactory>

    private var mBinding: VB? = null
    protected val binding
        get() = mBinding!!

    abstract fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onInject((requireActivity().application as WordApplication).applicationComponent)
    }

    abstract fun onInject(applicationComponent: ApplicationComponent)

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    protected fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
}
