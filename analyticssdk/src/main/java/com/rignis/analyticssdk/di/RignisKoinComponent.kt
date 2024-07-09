/*
 * Copyright (c) [2024] Amitkumar Chaudhary
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
package com.rignis.analyticssdk.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

abstract class RignisKoinComponent : KoinComponent {
    override fun getKoin(): Koin {
        return RignisIsolationContext.koin
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T> inject(
            clazz: Class<*>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null,
        ): Lazy<T> {
            return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
                get(clazz, qualifier, parameters)
            }
        }

        /**
         * Retrieve given dependency lazily if available
         * @param clazz - dependency class
         * @param qualifier - bean canonicalName / optional
         * @param scope - scope
         * @param parameters - dependency parameters / optional
         */

        @JvmStatic
        @JvmOverloads
        fun <T> injectOrNull(
            clazz: Class<*>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null,
        ): Lazy<T?> {
            return lazy {
                getOrNull(clazz, qualifier, parameters)
            }
        }

        /**
         * Retrieve given dependency
         * @param clazz - dependency class
         * @param qualifier - bean canonicalName / optional
         * @param scope - scope
         * @param parameters - dependency parameters / optional
         */

        @JvmStatic
        @JvmOverloads
        fun <T> get(
            clazz: Class<*>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null,
        ): T {
            val kClass = clazz.kotlin
            return RignisIsolationContext.koin.get(
                kClass,
                qualifier,
                parameters,
            )
        }

        /**
         * Retrieve given dependency if available
         * @param clazz - dependency class
         * @param qualifier - bean canonicalName / optional
         * @param scope - scope
         * @param parameters - dependency parameters / optional
         */

        @JvmStatic
        @JvmOverloads
        fun <T> getOrNull(
            clazz: Class<*>,
            qualifier: Qualifier? = null,
            parameters: ParametersDefinition? = null,
        ): T? {
            val kClass = clazz.kotlin
            return RignisIsolationContext.koin.getOrNull(
                kClass,
                qualifier,
                parameters,
            )
        }
    }
}