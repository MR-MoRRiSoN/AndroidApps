package com.example.smartug


data class ItemDatabase(
    var uid: String,
    var title: String,
    var disc: String,
    var countRoom: String,


) {

    constructor() : this(
        "",
        "",
        "",
        "",
    )
}
