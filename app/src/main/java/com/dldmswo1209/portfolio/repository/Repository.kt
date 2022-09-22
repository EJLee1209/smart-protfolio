package com.dldmswo1209.portfolio.repository

import android.content.Context
import com.dldmswo1209.portfolio.database.MyPortfolioDB
import com.dldmswo1209.portfolio.entity.CardEntity
import com.dldmswo1209.portfolio.entity.ChatEntity
import com.dldmswo1209.portfolio.entity.UserEntity

class Repository(context: Context) {
    val db = MyPortfolioDB.getDatabase(context)

    // 모든 카드 리스트를 가져오는 메소드
    fun getAllCard() = db.cardDao().getAllCard()

    // 카드 관련 메소드
    fun insertCard(card: CardEntity) = db.cardDao().insertCard(card)

    fun updateCard(card: CardEntity) = db.cardDao().updateCard(card)

    fun deleteCard(card: CardEntity) = db.cardDao().deleteCard(card)

    // 채팅 관련 메소드

    fun getAllChat() = db.chatDao().getAllChat()

    fun insertChat(chat: ChatEntity) = db.chatDao().insertChat(chat)

    fun updateChat(chat: ChatEntity) = db.chatDao().updateChat(chat)

    fun deleteChat(chat: ChatEntity) = db.chatDao().deleteChat(chat)

    // 유저 정보 관련 메소드
    fun insertUser(user: UserEntity) = db.userDao().insertUser(user)

    fun updateUser(user: UserEntity) = db.userDao().updateUser(user)

    fun getAllUser() = db.userDao().getAllUser()

}