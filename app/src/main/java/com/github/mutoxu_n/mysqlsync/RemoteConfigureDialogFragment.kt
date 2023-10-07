package com.github.mutoxu_n.mysqlsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.mysqlsync.databinding.FragmentRemoteConfigureDialogBinding

class RemoteConfigureDialogFragment: DialogFragment() {
    private lateinit var binding: FragmentRemoteConfigureDialogBinding
    private lateinit var viewModel: RemoteConfigureDialogViewModel

    companion object {
        fun newInstance(): RemoteConfigureDialogFragment {
            return RemoteConfigureDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRemoteConfigureDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[RemoteConfigureDialogViewModel::class.java]
    }
}