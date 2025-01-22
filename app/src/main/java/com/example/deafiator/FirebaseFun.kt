package com.example.deafiator

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseFun {

    private val firestore = FirebaseFirestore.getInstance()

    fun addUser(userName: String, email: String, password: String) {
        val nameData = hashMapOf(
            "admin" to true,
            "email" to email,
            "age" to 19,
            "password" to password,
            "latitude" to 28.678302,
            "longitude" to 77.261116
        )

        // Reference to the 'name' subcollection inside the 'robogyan' document
        val userDocRef = firestore.collection("group").document("robogyan")

        // First, update the 'name' field with the userName
        userDocRef.update(userName, userName)
            .addOnSuccessListener {
                // Successfully added name, now add data inside the name field
                userDocRef.update(mapOf(
                    "$userName.admin" to nameData["admin"],
                    "$userName.email" to nameData["email"],
                    "$userName.age" to nameData["age"],
                    "$userName.password" to nameData["password"],
                    "$userName.latitude" to nameData["latitude"],
                    "$userName.longitude" to nameData["longitude"]
                )).addOnSuccessListener {
                    Log.d("Firestore", "Data inside name field successfully added")
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding data inside name field", e)
                }
            }
            .addOnFailureListener { e ->
                // If the document doesn't exist, create the document with both name and additional data
                userDocRef.set(
                    mapOf(
                        "name" to nameData["name"],
                    )
                ).addOnSuccessListener {
                    Log.d("Firestore", "Document created with name and data")
                }.addOnFailureListener { error ->
                    Log.e("Firestore", "Error creating document with name and data", error)
                }
            }
    }

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun fetchUserData(username: String, onSuccess: (Map<String, Any?>) -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = database.child("groups").child("Robogyan").child(username) // Adjust path as needed

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val userData = dataSnapshot.value as Map<String, Any?>
                onSuccess(userData)
            } else {
                onFailure(Exception("User data not found"))
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun storeUserData(userData: Map<String, Any?>) {
        val admin = userData["admin"] as Boolean
        val hardware = userData["hardware"] as Boolean
        val emergency = userData["emergency"] as Boolean
        val username = userData["username"] as String
        val email = userData["email"] as String
        val password = userData["password"] as String
        val latitude = userData["latitude"] as Double
        val longitude = userData["longitude"] as Double

        UserManager.saveUserData(
            admin = admin,
            hardware = hardware,
            emergency = emergency,
            username = username,
            email = email,
            password = password,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun authenticateUser(inputUsername: String, inputPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        fetchUserData(inputUsername, { userData ->
            val storedPassword = userData["password"] as String
            if (inputPassword == storedPassword) {
                // Authentication successful
                onSuccess()
            } else {
                onFailure("Incorrect password")
            }
        }, { exception ->
            onFailure(exception.message ?: "Failed to fetch user data")
        })
    }


}