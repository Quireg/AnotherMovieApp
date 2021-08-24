/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

data class ModelTopRatedListRespond(
    var page: Long,
    var totalPages: Long,
    var data: List<EntityDBMovie>?
)