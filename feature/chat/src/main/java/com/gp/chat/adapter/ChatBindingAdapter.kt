package com.gp.chat.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.model.RecentChat
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.chat.util.RemoveSpecialChar.restoreOriginalEmail

@BindingAdapter("users:imageUrl")
fun ImageView.setProfilePicture(picUrl: String?) {
    if (picUrl != null) {
        Glide.with(this.context)
            .load(picUrl)
            .placeholder(R.drawable.ic_person_24)
            .apply(RequestOptions.circleCropTransform())
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_person_24)
    }
}
@SuppressLint("SetTextI18n")
@BindingAdapter("chat:setTitle")
fun TextView.setTitle(chat: RecentChat){
    val currentEmail= removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    var email = ""
    if (currentEmail==chat.receiverName){
        email = restoreOriginalEmail(chat.senderName!!)
    }
    else{
         email = restoreOriginalEmail(chat.receiverName!!)
    }
     Firebase.firestore.collection("users").get().addOnSuccessListener {
        for (document in it){
            if (document.data["userEmail"]==email){
                text=document.data["userFirstName"].toString()+" "+document.data["userLastName"].toString()

            }

        }
    }

}