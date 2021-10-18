package uz.mobilestudio.photo.retrofit

object Common {
    const val BASE_URL = "https://api.unsplash.com/"

    val ACCESS_KEYS = mutableListOf(
        "ztWk7xgIELmPVeVlrpbwC8zmy8XWfx5IHeGPybvBIGA",
        "GvkXC5a27OTubqeucaBgaKFbooecpbC9CGgDl9MWsKY",
        "YNJiqfXBdTbRElslO0buHKNGQZT3EtjjLm_VQ_R",
        "Hy--y8TTnKNrQbu5f5YH27QhT-09bVhjP0IJsWLZNAE",
        "8R1OWA2UmBa7uUWCBw6LjO81uVIoo3-f-YD9QCaD63A",
        "KlArHI18G3d1PeqNj4OMfK_IV-Vhbe7aM_S_5EAi-vw",
        "hq_Z52uspI1F3jjlW34JsYicPW2-A3RGLd2Ag2QUd04",
        "Yg8pWYO7B06ZGGxZkbptghyNt4_WAybQ9_OekRzA7vc"
    )

    const val SECRET_KEY = "02MVURPo0WO2ygX3FtvZ9aGwbMKmiGvs2j7P1TmStHE"

    const val PER_PAGE = 30

    fun retrofitService(): RetrofitService {
        return RetrofitClient.getRetrofit(BASE_URL).create(RetrofitService::class.java)
    }
}