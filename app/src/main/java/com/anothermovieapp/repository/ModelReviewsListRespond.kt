package com.anothermovieapp.repository

data class ModelReviewsListRespond (var page: Long,
                                        var totalPages: Long,
                                        var data: List<EntityDBMovieReview>?)