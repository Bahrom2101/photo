package uz.mobilestudio.photo.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import uz.mobilestudio.photo.models.api.collection.CollectionsResponse
import uz.mobilestudio.photo.repositories.CollectionsResponseRepo
import uz.mobilestudio.photo.retrofit.Common.ACCESS_KEY
import uz.mobilestudio.photo.retrofit.Common.PER_PAGE
import uz.mobilestudio.photo.retrofit.Common.SECRET_KEY

class CollectionsResponseViewModel : ViewModel() {

    private var collectionsResponseRepo = CollectionsResponseRepo()

    fun getResponseSearchCollections(page: Int, query: String): LiveData<CollectionsResponse> {
        return collectionsResponseRepo.getResponseSearchCollections(
            ACCESS_KEY,
            page,
            PER_PAGE,
            query
        )
    }

}