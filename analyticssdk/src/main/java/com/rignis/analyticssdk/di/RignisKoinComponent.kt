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