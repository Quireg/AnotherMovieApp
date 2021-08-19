package com.anothermovieapp.repository

data class ModelPopularListRespond(
    var page: Long,
    var totalPages: Long,
    var data: List<EntityDBMovie>?
)