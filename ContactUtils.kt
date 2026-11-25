package com.example.contactbook

fun isValidName(name: String): Boolean {
    return name.isNotBlank()
}

fun isValidPhone(phone: String): Boolean {
    return phone.length == 11 && phone.all { it.isDigit() }
}

fun searchContacts(
    contacts: List<Contact>,
    search: String
): List<Contact> {
    return contacts.filter {
        it.name.contains(search, ignoreCase = true) ||
                it.phone.contains(search)
    }
}

fun getNextId(contacts: List<Contact>): Int {
    return (contacts.maxOfOrNull { it.id } ?: 0) + 1
}
