package com.example.contactbook

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testValidName() {
        assertTrue(isValidName("Анна"))
        assertFalse(isValidName(""))
        assertFalse(isValidName("   "))
    }

    @Test
    fun testValidPhone() {
        assertTrue(isValidPhone("89001234567"))
        assertFalse(isValidPhone("12345"))
        assertFalse(isValidPhone("abcdefghijk"))
        assertFalse(isValidPhone(""))
    }

    @Test
    fun testSearchByName() {
        val contacts = listOf(
            Contact(1, "Анна", "89001234567"),
            Contact(2, "Мария", "89005554433")
        )

        assertEquals(
            listOf(contacts[0]),
            searchContacts(contacts, "анна")
        )
        assertTrue(
            searchContacts(contacts, "Несуществующий").isEmpty()
        )
    }

    @Test
    fun testSearchByPhone() {
        val contacts = listOf(
            Contact(1, "Анна", "89001234567"),
            Contact(2, "Мария", "89005554433")
        )

        assertEquals(
            listOf(contacts[1]),
            searchContacts(contacts, "5554433")
        )
        assertEquals(contacts, searchContacts(contacts, ""))
    }

    @Test
    fun testNextId() {
        assertEquals(1, getNextId(emptyList()))

        val contacts = listOf(
            Contact(1, "Анна", "89001234567"),
            Contact(5, "Мария", "89005554433")
        )

        assertEquals(6, getNextId(contacts))
    }
}
