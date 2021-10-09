package uz.mobilestudio.photo.retrofit

object Common {
    const val BASE_URL = "https://api.unsplash.com/"
    const val ACCESS_KEY = "-PLCsfxUJM0h-yx7crTlfCybrk7Xt7MJk1vXRUXXBQw"
    const val SECRET_KEY = "02MVURPo0WO2ygX3FtvZ9aGwbMKmiGvs2j7P1TmStHE"

    const val PER_PAGE = 30

    fun retrofitService(): RetrofitService {
        return RetrofitClient.getRetrofit(BASE_URL).create(RetrofitService::class.java)
    }
}