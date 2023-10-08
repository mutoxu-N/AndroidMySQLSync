package com.github.mutoxu_n.mysqlsync

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.mysqlsync.databinding.FragmentRemoteConfigureDialogBinding

import android.view.ViewGroup.LayoutParams
class RemoteConfigureDialogFragment: DialogFragment() {
    private lateinit var binding: FragmentRemoteConfigureDialogBinding
    private lateinit var viewModel: RemoteConfigureDialogViewModel

    companion object {
        fun newInstance(): RemoteConfigureDialogFragment {
            return RemoteConfigureDialogFragment()
        }

        private const val KEY_ADDRESS = "address"
        private const val KEY_PORT = "port"
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRemoteConfigureDialogBinding.inflate(inflater)

        binding.etAddress.doOnTextChanged { text, start, before, count ->
            viewModel.setAddress(text.toString())
        }

        binding.etPort.doOnTextChanged { text, start, before, count ->
            viewModel.setPort(Integer.parseInt(text.toString()))
        }


        binding.done.setOnClickListener {

            Toast.makeText(context, "${viewModel.address.value}, ${viewModel.port.value}", Toast.LENGTH_SHORT).show()
            val editor = App.pref.edit()
            editor.putString(KEY_ADDRESS, viewModel.address.value)
            editor.putInt(KEY_PORT, viewModel.port.value!!)
            editor.apply()

            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[RemoteConfigureDialogViewModel::class.java]

        // SharedPreferences があれば読み込む
        val pref = App.pref
        pref.let {
            binding.etAddress.setText(pref.getString(KEY_ADDRESS, "127.0.0.1")!!)
            binding.etPort.setText(pref.getInt(KEY_PORT, 443).toString())
        }
    }
}