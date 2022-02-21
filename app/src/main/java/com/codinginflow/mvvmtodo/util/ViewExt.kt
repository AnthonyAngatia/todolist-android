package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView

/**
 * Created an extension function OnQueryTextChanged
 * T@param1 - Another function called listenert that takes in string and returns Unit/void
 * */
inline fun SearchView.onQueryTextChanged(crossinline listener:(String)->Unit){
    this.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }


    })
}