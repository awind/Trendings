/*
 * Copyright (c) 2016 Phillip Song (http://github.com/awind).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phillipsong.gittrending.data.api;

import com.phillipsong.gittrending.data.models.Developers;
import com.phillipsong.gittrending.data.models.Support;
import com.phillipsong.gittrending.data.models.Trending;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TrendingService {

    @GET("/v2")
    Observable<Trending> getTrending(@Query("language") String language,
                                     @Query("since") String since);

    @GET("/v2/support")
    Observable<Support> getSupport();

    @GET("/v2/developers")
    Observable<Developers> getDevelopers(@Query("language") String language,
                                         @Query("since") String since);
}