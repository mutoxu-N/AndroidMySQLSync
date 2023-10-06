package com.github.mutoxu_n.mysqlsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.mysqlsync.databinding.FragmentAddWordDialogBinding

class EditWordDialogFragment : DialogFragment() {
    private lateinit var viewModel: EditWordDialogViewModel
    private lateinit var binding: FragmentAddWordDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddWordDialogBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[EditWordDialogViewModel::class.java]
    }

    companion object {
        fun newInstance(id: Long): EditWordDialogFragment {
            val dialog = EditWordDialogFragment()
            val args = Bundle()
            args.putLong("id", id)
            dialog.arguments = args
            return dialog
        }

        fun newInstance() = newInstance(0L)
    }
}