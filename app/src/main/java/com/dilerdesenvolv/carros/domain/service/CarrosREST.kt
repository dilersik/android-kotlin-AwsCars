package com.dilerdesenvolv.carros.domain.service

import com.dilerdesenvolv.carros.domain.Response
import com.dilerdesenvolv.carros.domain.model.Carro
import retrofit2.Call
import retrofit2.http.*

interface CarrosREST {

    @GET("tipo/{tipo}")
    fun getCarros(@Path("tipo") tipo: String,
                  @Query("page") page: Int,
                  @Query("api_key") api_key: String)
            : Call<MutableList<Carro>>

    @GET("busca/{busca}")
    fun searchCarros(@Path("busca") busca: String,
                     @Query("page") page: Int,
                     @Query("api_key") api_key: String)
            : Call<MutableList<Carro>>

    @POST("./")
    @FormUrlEncoded
    fun save(@Field("carro") carroJson: String,
             @Field("api_key") api_key: String)
            : Call<Response>
    // @Body carro: Carro, mas somente o @Body e nada mais de @Form e @Field

    @DELETE("{id}/{api_key}")
    fun delete(@Path("id") id: Long,
               @Path("api_key") api_key: String)
            : Call<Response>

    @FormUrlEncoded
    @POST("postFotoBase64")
    fun postFoto(@Field("id") id:Long?,
                 @Field("fileName") fileName:String,
                 @Field("base64") base64:String,
                 @Field("api_key") api_key: String): Call<Response>

}