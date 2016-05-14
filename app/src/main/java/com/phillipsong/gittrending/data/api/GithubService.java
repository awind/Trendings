package com.phillipsong.gittrending.data.api;

import com.phillipsong.gittrending.data.models.GithubRepo;
import com.phillipsong.gittrending.data.models.GithubUser;
import com.phillipsong.gittrending.data.models.GithubResponse;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fei on 16/5/12.
 */
public interface GithubService {

    @GET("users")
    Observable<GithubResponse<GithubUser>> searchUser(@Query("q") String keyword,
                                                      @Query("page") int page);

    @GET("repositories")
    Observable<GithubResponse<GithubRepo>> searchRepo(@Query("q") String keyword,
                                                      @Query("page") int page);
}
