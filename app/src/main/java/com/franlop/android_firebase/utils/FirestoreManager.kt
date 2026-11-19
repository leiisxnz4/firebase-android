package com.franlop.android_firebase.utils

import android.content.Context
import com.franlop.android_firebase.model.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = AuthManager(context)
    var userId = auth.getCurrentUser()?.uid

    suspend fun addContact(contact: Contact) {
        contact.uid = userId.toString()
        firestore.collection("contacts").add(contact).await()
    }

    suspend fun updateContact(contact: Contact) {
        val contactRef = contact.key?.let { firestore.collection("contacts").document(it) }
        contactRef?.set(contact)?.await()
    }

    suspend fun deleteContact(contactId: String) {
        val contactRef = firestore.collection("contacts").document(contactId)
        contactRef.delete().await()
    }

    fun getContactsFlow(): Flow<List<Contact>> = callbackFlow {
        val contactsRef = firestore.collection("contacts")
            .whereEqualTo("uid", userId)

        val subscription = contactsRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let { querySnapshot ->
                val contacts = mutableListOf<Contact>()
                for (document in querySnapshot.documents) {
                    val contact = document.toObject(Contact::class.java)
                    contact?.key = document.id
                    contact?.let { contacts.add(it) }
                }
                trySend(contacts).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }
}