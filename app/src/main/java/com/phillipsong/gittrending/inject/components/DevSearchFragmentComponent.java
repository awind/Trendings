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
package com.phillipsong.gittrending.inject.components;

import com.phillipsong.gittrending.inject.modules.DevSearchFragmentModule;
import com.phillipsong.gittrending.ui.fragment.DevSearchFragment;
import com.phillipsong.gittrending.ui.misc.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = {DevSearchFragmentModule.class}, dependencies = {AppComponent.class})
public interface DevSearchFragmentComponent extends MainActivityComponent {

    void inject(DevSearchFragment fragment);
}
