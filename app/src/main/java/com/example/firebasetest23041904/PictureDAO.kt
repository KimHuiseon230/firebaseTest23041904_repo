package com.example.firebasetest23041904

import com.bumptech.glide.Glide.init
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class PictureDAO {
    var databaseReference: DatabaseReference? = null
    var storage: FirebaseStorage? = null

    init {
        // 실시간 DB 연결
        var db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("picture") //picture 테이블 생성
        storage = Firebase.storage
    }

    // insert INTO user values(_,_,_,_)
    fun pictureInsert(pictureData: PictureData?): Task<Void> {
        return databaseReference!!.push().setValue(pictureData)
    }

    // SELECT * FROM table WHERE id = 'user';
    fun pictureSelect(): Query? {
        return databaseReference
    }
}