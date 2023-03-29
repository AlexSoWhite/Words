package com.nafanya.words.feature.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.nafanya.words.R
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentChooseModeBinding
import com.nafanya.words.feature.test.TestFragment.Companion.MODE

class ChooseModeFragment : BaseFragment<FragmentChooseModeBinding>() {

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentChooseModeBinding {
        return FragmentChooseModeBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nativeToJp.setOnClickListener {
            val bundle = bundleOf(MODE to 0)
            view.findNavController().navigate(R.id.action_nav_choose_mode_to_nav_test, bundle)
        }
        binding.jpToNative.setOnClickListener {
            val bundle = bundleOf(MODE to 1)
            view.findNavController().navigate(R.id.action_nav_choose_mode_to_nav_test, bundle)
        }
    }
}
