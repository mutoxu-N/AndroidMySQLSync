package com.github.mutoxu_n.mysqlsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.mysqlsync.databinding.FragmentEditWordDialogBinding

class EditWordDialogFragment : DialogFragment() {
    private lateinit var viewModel: EditWordDialogViewModel
    private lateinit var binding: FragmentEditWordDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditWordDialogBinding.inflate(inflater)

        binding.done.setOnClickListener {
            viewModel.setJp(binding.etJp.text.toString())
            viewModel.setEn(binding.etEn.text.toString())
            viewModel.save()
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[EditWordDialogViewModel::class.java]

        viewModel.id.observe(this) { binding.tvId.setText("ID: $it") }
        viewModel.jp.observe(this) { binding.etJp.setText(it) }
        viewModel.en.observe(this) { binding.etEn.setText(it) }

        arguments?.let {args ->
            viewModel.setId(args.getLong("id"))
        }

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