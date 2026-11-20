
package com.example.contactbook

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

data class Contact(
    val id: Int,
    val name: String,
    val phone: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ContactBookScreen(this)
            }
        }
    }
}

fun loadContacts(context: Context): List<Contact> {
    val preferences = context.getSharedPreferences(
        "contact_book",
        Context.MODE_PRIVATE
    )

    val saved = preferences.getString("contacts", "[]") ?: "[]"

    return try {
        val array = JSONArray(saved)
        val result = mutableListOf<Contact>()

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)

            result.add(
                Contact(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    phone = item.getString("phone")
                )
            )
        }

        result
    } catch (e: Exception) {
        emptyList()
    }
}

fun saveContacts(
    context: Context,
    contacts: List<Contact>
) {
    val array = JSONArray()

    contacts.forEach { contact ->
        val item = JSONObject()
        item.put("id", contact.id)
        item.put("name", contact.name)
        item.put("phone", contact.phone)
        array.put(item)
    }

    context.getSharedPreferences(
        "contact_book",
        Context.MODE_PRIVATE
    ).edit()
        .putString("contacts", array.toString())
        .apply()
}

@Composable
fun ContactBookScreen(context: Context) {
    val contacts = remember {
        mutableStateListOf<Contact>().apply {
            addAll(loadContacts(context))
        }
    }

    var search by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    var nextId by remember {
        mutableIntStateOf(
            (contacts.maxOfOrNull { it.id } ?: 0) + 1
        )
    }

    val filteredContacts = contacts.filter {
        it.name.contains(search, ignoreCase = true) ||
                it.phone.contains(search)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Мои контакты",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Поиск контакта") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить контакт")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredContacts.isEmpty()) {
            Text("Контакты не найдены")
        } else {
            LazyColumn {
                items(filteredContacts, key = { it.id }) { contact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme
                                        .typography.titleMedium
                                )
                                Text(contact.phone)
                            }

                            TextButton(
                                onClick = {
                                    contacts.remove(contact)
                                    saveContacts(context, contacts)
                                }
                            ) {
                                Text("Удалить")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddContactDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, phone ->
                contacts.add(
                    Contact(nextId++, name, phone)
                )
                saveContacts(context, contacts)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый контакт") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAdd(name.trim(), phone.trim())
                },
                enabled = name.isNotBlank() &&
                        phone.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
