/*
 *    Copyright 2018 Denys Nykyforov (@popalay)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.popalay.hoard.hoard

import com.github.popalay.hoard.Key
import java.util.Date

internal const val DEFAULT_PAGE_SIZE = 20

interface PageKey : Key {
    val pageSize get() = DEFAULT_PAGE_SIZE
    val date: Date get() = Date()
    val offset: Int
}