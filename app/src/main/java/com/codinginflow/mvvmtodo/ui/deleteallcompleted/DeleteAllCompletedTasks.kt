package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedTasks: DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Confirm you want to delete all messages")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete"){_,_->
                viewModel.onConfirmClick()
            }
            .create()
    }
}