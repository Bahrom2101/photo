package uz.mobilestudio.photo.retrofit

object Common {
    const val BASE_URL = "https://api.unsplash.com/"
    const val ACCESS_KEY1 = "ztWk7xgIELmPVeVlrpbwC8zmy8XWfx5IHeGPybvBIGA"
    const val ACCESS_KEY2 = "GvkXC5a27OTubqeucaBgaKFbooecpbC9CGgDl9MWsKY"
    const val ACCESS_KEY3 = "YNJiqfXBdTbRElslO0buHKNGQZT3EtjjLm_VQ_R-GOc"

    const val SECRET_KEY = "02MVURPo0WO2ygX3FtvZ9aGwbMKmiGvs2j7P1TmStHE"

    const val PER_PAGE = 30

    fun retrofitService(): RetrofitService {
        return RetrofitClient.getRetrofit(BASE_URL).create(RetrofitService::class.java)
    }
}