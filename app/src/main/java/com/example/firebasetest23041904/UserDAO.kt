package com.example.firebasetest23041904

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class UserDAO {
    private var databaseReference :DatabaseReference?=null
    init {
        // 실시간 DB 연결
        var db= FirebaseDatabase.getInstance()
        databaseReference =db.getReference("user") //user 테이블 생성

    }
    //insert INTO user values(_,_,_,_)
    fun fbInsert(user:User?): Task<Void> {
        return databaseReference!!.push().setValue(user)
    }
    fun userSelect():Query?{
    //SELECT * FROM table WHERE id = 'user';
        return databaseReference
    }
    // update user set where
    fun userUpdate(userKey:String,hashMap: HashMap<String,Any>):Task<Void>{
        return databaseReference!!.child(userKey).updateChildren(hashMap)
    }
    //delete from user where userKey= ?
    fun userDelete(userKey:String) :Task<Void>{
        return databaseReference!!.child(userKey).removeValue()
    }
}