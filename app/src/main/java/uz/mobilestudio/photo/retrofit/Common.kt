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
        "Yg8pWYO7B06ZGGxZkbptghyNt4_WAybQ9_OekRzA7vc",
        "l9ItMfoKBUhpDwilnLN6YJ7raNWz_dXqWP-5GCAK0NA",
        "5J5e46nyO-sXL4FZub7olWh1-g_QraRV1EjVx_e4xbA",
        "tKdejYyaIr2Mi0qzP29h9Sgvj3hjDl-wimE7igHckcU",
        "VSsqOlWJhV8DoPxb5PVtkjn2opeBqPp_P0QMPZcsbas",
        "rmhjx4lXisi34rnZhqegWb3GJWXo03gXLBGJkxG5Z6c",
        "ix2WAOEQwVZxEcPHPiqsmHnbgjw11briOcUinlNLuNM",
        "kXKvthxRRNDdnwcWJ1tjlSeR_P5ZHX7yXySGuqF11_8",
        "kz5J-5-FOiPxo-CdgwDU3BPKVXWrHum4i-p9NvqQQi4",
        "2_ZcpJ_ZsWHpDWbimQ6x62n0KG0yMz6_m4oBhigNwuU",
        "nvFL2be_IA02Riu0oFqWhndXPWnKRu2RKbF5mAl9p_k",
        "JctMJxMaiBpQLQJdj0wmf2qdVSrWZT4j9QYGLl_dt60",
        "dk6XzCyFWd1wa3pwQddv-TeMfipLcCM7_5iX3H5bkRs",
        "RKdGLvYOMR5hokNBcYXt3Fi6qhoYKsETzZcsSeVdBxc",
        "DDlF_JNPk2QUg83j7T3qut82TeKASxoJqS7nO_zhfc8",
        "1mH4A7-TqSZJBTNtvxuV4qS2aRwHV9PaU60UmJGF7yw",
    )

    const val SECRET_KEY = "02MVURPo0WO2ygX3FtvZ9aGwbMKmiGvs2j7P1TmStHE"

    const val PER_PAGE = 30

    fun retrofitService(): RetrofitService {
        return RetrofitClient.getRetrofit(BASE_URL).create(RetrofitService::class.java)
    }
}