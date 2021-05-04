package com.myapp.travelize.models

data class Place(
    var id: String? = null,
    var name: String? = null,
    var photoRef: String? = null,
    var address: String? = null,
    var rating: Double? = null,
    var totalRatings:Int,
    var phoneNo: String? = null,
    var workingHours: MutableList<String> =mutableListOf(),
    var isExpanded: Boolean = false
)
