package com.myapp.travelize.models

import com.google.firebase.firestore.GeoPoint

data class User(
    val name: String? = null,
    val email: String? = null,
    val dob: String? = null,
    val gender: String? = null,
    val insitute: String? = null,
    val passions: List<String>? = null,
    val imageURL: String? = null,
    val description: String? = null,
    val ageLL: Int? = null,
    val ageUL: Int? = null,
    val city: String? = null,
    val location: GeoPoint? = null
)
