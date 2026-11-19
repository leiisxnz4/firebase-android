package com.franlop.android_firebase.utils

import android.content.Context
import com.franlop.android_firebase.model.Note
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeManager(context: Context) {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://fir-android-leilani-default-rtdb.firebaseio.com/")
            .reference.child("notes")
    private val auth = AuthManager(context)

    fun addNote(note: Note) {
        val key = databaseReference.push().key
        if (key != null) {
            note.key = key
            note.uid = auth.getCurrentUser()?.uid.toString()
            databaseReference.child(key).setValue(note)
        }
    }

    fun deleteNote(noteId: String) {
        databaseReference.child(noteId).removeValue()
    }

    fun updateNote(noteId: String, updatedNote: Note) {
        updatedNote.key = noteId
        updatedNote.uid = auth.getCurrentUser()?.uid.toString()
        databaseReference.child(noteId).setValue(updatedNote)
    }

    fun getNotesFlow(): Flow<List<Note>> = callbackFlow {
        val idFilter = auth.getCurrentUser()?.uid
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = snapshot.children.mapNotNull { dataSnapshot ->
                    val note = dataSnapshot.getValue(Note::class.java)
                    note?.key = dataSnapshot.key
                    note
                }
                trySend(notes.filter { it.uid == idFilter }).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
        awaitClose { databaseReference.removeEventListener(valueEventListener) }
    }
}