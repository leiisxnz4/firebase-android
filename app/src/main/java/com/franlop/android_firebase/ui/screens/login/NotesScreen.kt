package com.franlop.android_firebase.ui.screens.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.model.Note
import com.franlop.android_firebase.utils.RealtimeManager

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesScreen(realtime: RealtimeManager) {
    var showAddNoteDialog by remember { mutableStateOf(false) }

    val notes by realtime.getNotesFlow().collectAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddNoteDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Nota")
            }

            if (showAddNoteDialog) {
                AddNoteDialog(
                    onNoteAdded = { note ->
                        realtime.addNote(note)
                        showAddNoteDialog = false
                    },
                    onDialogDismissed = { showAddNoteDialog = false }
                )
            }
        }
    ) {
        if (notes.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(notes) { note ->
                    NoteItem(note = note, realtime = realtime)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron \nNotas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, realtime: RealtimeManager) {
    var showDeleteNoteDialog by remember { mutableStateOf(false) }
    var showEditNoteDialog by remember { mutableStateOf(false) }

    if (showDeleteNoteDialog) {
        DeleteNoteDialog(
            onConfirmDelete = {
                realtime.deleteNote(note.key ?: "")
                showDeleteNoteDialog = false
            },
            onDismiss = { showDeleteNoteDialog = false }
        )
    }

    if (showEditNoteDialog) {
        EditNoteDialog(
            note = note,
            onNoteUpdated = { updatedNote ->
                realtime.updateNote(note.key ?: "", updatedNote)
                showEditNoteDialog = false
            },
            onDialogDismissed = { showEditNoteDialog = false }
        )
    }

    Card(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 0.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(16.dp)
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .weight(1.4f)
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { showEditNoteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Nota"
                    )
                }
                IconButton(
                    onClick = { showDeleteNoteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Nota"
                    )
                }
            }
        }
    }
}

@Composable
fun AddNoteDialog(
    onNoteAdded: (Note) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text(text = "Agregar Nota") },
        confirmButton = {
            Button(
                onClick = {
                    onNoteAdded(Note(title = title, content = content))
                    title = ""
                    content = ""
                }
            ) {
                Text(text = "Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Contenido") }
                )
            }
        }
    )
}

@Composable
fun EditNoteDialog(
    note: Note,
    onNoteUpdated: (Note) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text(text = "Editar Nota") },
        confirmButton = {
            Button(
                onClick = {
                    onNoteUpdated(Note(title = title, content = content))
                }
            ) {
                Text(text = "Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Contenido") }
                )
            }
        }
    )
}

@Composable
fun DeleteNoteDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Nota") },
        text = { Text("¿Estás seguro que deseas eliminar la nota?") },
        confirmButton = {
            Button(onClick = onConfirmDelete) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}