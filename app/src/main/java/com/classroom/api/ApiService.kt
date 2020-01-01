package com.classroom.api

import com.bright.course.http.response.ResponseDataT
import com.bright.course.http.response.ResponseLogin
import com.bright.course.http.response.ResponseRelevanceLogin
import com.bright.course.http.response.ResultInfo
import com.classroom.mvp.model.bean.HandSupBean
import com.classroom.mvp.model.bean.HttpResult
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by wyq on 2018/9/25.
 */
interface ApiService {

    /**
     * 登录
     * @param name
     * @param password
     */
    @POST("student/login")
    @FormUrlEncoded
    fun login(@Field("name") name: String,
              @Field("password") password: String): Observable<HttpResult<ResponseLogin>>

    /**
     * 学生举手表示疑问
     */
    @POST("student/handsUp")
    fun handsup(): Observable<HttpResult<HandSupBean>>

    /**
     * 退出登录
     */
    @POST("student/logout")
    fun logout(): Observable<HttpResult<Object>>

    /**
     * 关联登录
     */
    @FormUrlEncoded
    @POST("studentAccount/login")
    fun relevanceLogin(@Field("studentNo") studentNo: String,
                       @Field("password") password: String):Call<ResponseRelevanceLogin>
}