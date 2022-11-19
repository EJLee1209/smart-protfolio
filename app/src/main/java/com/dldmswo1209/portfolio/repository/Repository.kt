package com.dldmswo1209.portfolio.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dldmswo1209.portfolio.Model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class Repository() {
    val database = Firebase.database.reference
    val storage = FirebaseStorage.getInstance().reference

    // 유저 정보 가져오기
    fun getUser(uid: String): LiveData<User>{
        val user = MutableLiveData<User>()

        val dbRef = database.child("User/${uid}")
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    user.value = snapshot.getValue(User::class.java)
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })

        return user
    }

    // 프로필 수정하기
    fun updateUser(user: User, imageUri: Uri?){
        Log.d("testt", "updateUser: ${imageUri}")
        if(imageUri == null){ // 프로필 사진이 아예 없음
            Log.d("testt", "updateUser: ${user}")
            database.child("User/${user.uid}").setValue(user) // 새로운 데이터 저장
        }
        else{
            val imgFileName = "${imageUri.lastPathSegment}.png"
            val imagePath = "Profile_Images/${user.uid}/${imgFileName}.png"

            storage.child(imagePath).putFile(imageUri) // 이미지 업로드
                .addOnSuccessListener {
                    Log.d("testt", "updateUser imagePath: ${user.profile!!.imageUri}")
                    storage.child(imagePath).downloadUrl.addOnSuccessListener { uri ->
                        // 이미지 다운로드
                        Log.d("testt", "download image : ${uri}")
                        val newProfile = user
                        newProfile.profile?.image = uri.toString() // 다운로드 받은 이미지 uri 저장
                        database.child("User/${newProfile.uid}").setValue(newProfile) // 새로운 데이터 저장

                    }

                }
        }
    }

    fun updatePrivacyInfo(uid: String, profile: UserProfile){
        database.child("User/${uid}/profile").setValue(profile)
    }

    // 타임라인 가져오기
    fun getTimeLine(uid: String) : LiveData<MutableList<TimeLine>> {
        val timeLines = MutableLiveData<MutableList<TimeLine>>()

        database.child("Portfolio/${uid}/TimeLine")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<TimeLine>()
                    if(snapshot.exists()){
                        snapshot.children.forEach {
                            val data = it.getValue(TimeLine::class.java) ?: return@forEach
                            dataList.add(data)
                        }
                        timeLines.postValue(dataList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        return timeLines
    }

    // 타임라인 생성
    fun createTimeLine(uid: String, timeLine: TimeLine){
        val db = database.child("Portfolio/${uid}/TimeLine").push()
        val key = db.key // 새로운 키 생성

        timeLine.key = key.toString()
        db.setValue(timeLine)

    }

    fun getChat(uid: String): LiveData<MutableList<Chat>> {
        val chatList = MutableLiveData<MutableList<Chat>>()

        database.child("Portfolio/${uid}/Chat")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<Chat>()
                    if(snapshot.exists()){
                        snapshot.children.forEach {
                            val data = it.getValue(Chat::class.java) ?: return@forEach
                            dataList.add(data)
                        }
                        chatList.postValue(dataList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        return chatList
    }

    // 채팅 포트폴리오 아이템 생성
    fun sendChat(uid: String, chat: Chat){
        val db = database.child("Portfolio/${uid}/Chat").push()
        val key = db.key // 새로운 키 생성

        chat.key = key.toString()
        db.setValue(chat)
    }

    // 채팅 삭제
    fun deleteChat(uid: String, key: String){
        database.child("Portfolio/${uid}/Chat/${key}").removeValue()
    }

    // 채팅 수정
    fun modifyChat(uid: String, chat: Chat){
        database.child("Portfolio/${uid}/Chat/${chat.key}").setValue(chat)
    }

    // 채팅 새로고침
    fun refreshChat(uid: String){
        val db = database.child("Portfolio/${uid}/Chat").push()
        val key = db.key.toString() // 새로운 키 생성

        val dummyChat = Chat("",0,key)
        db.setValue(dummyChat)
            .addOnSuccessListener {
                db.removeValue()
            }
    }

    // 카드 가져오기
    fun getCard(uid: String) : LiveData<MutableList<Card>> {
        val cardList = MutableLiveData<MutableList<Card>>()

        database.child("Portfolio/${uid}/Card")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<Card>()
                    if(snapshot.exists()){
                        snapshot.children.forEach {
                            val data = it.getValue(Card::class.java) ?: return@forEach
                            dataList.add(data)
                        }
                        cardList.postValue(dataList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        return cardList
    }

    // 카드 생성
    fun createCard(uid: String, card: Card){
        val db = database.child("Portfolio/${uid}/Card").push()
        val key = db.key // 새로운 키 생성
        card.key = key.toString()

        if(card.imageUri == null){ // 프로필 사진이 아예 없음
            db.setValue(card) // 새로운 데이터 저장
        }
        else{
            val imgFileName = "${card.imageUri?.toUri()?.lastPathSegment}.png"
            val imagePath = "Portfolio_Images/${uid}/${imgFileName}.png"

            storage.child(imagePath).putFile(card.imageUri!!.toUri()) // 이미지 업로드
                .addOnSuccessListener {
                    storage.child(imagePath).downloadUrl.addOnSuccessListener { uri ->
                        // 이미지 다운로드
                        val newCard = card
                        newCard.image = uri.toString() // 다운로드 받은 이미지 uri 저장
                        db.setValue(card) // 새로운 데이터 저장
                    }

                }
        }
    }

    // 카드 삭제
    fun deleteCard(uid: String, card: Card){
        database.child("Portfolio/${uid}/Card/${card.key}").removeValue()
        // 사진도 지워야 함
        if(card.imageUri != "" && card.imageUri != null){
            val imgFileName = "${card.imageUri?.toUri()?.lastPathSegment}.png"
            val imagePath = "Portfolio_Images/${uid}/${imgFileName}.png"
            storage.child(imagePath).delete()
        }
    }

}