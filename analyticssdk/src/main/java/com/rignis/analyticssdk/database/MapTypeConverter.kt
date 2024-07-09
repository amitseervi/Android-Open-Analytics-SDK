package com.rignis.analyticssdk.database

import androidx.room.TypeConverter
import com.rignis.analyticssdk.JsonProvider
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

internal class MapTypeConverter {
    @TypeConverter
    fun fromMapToString(value: Map<String, String>): String =
        JsonProvider.json.encodeToString(
            MapSerializer(
                String.serializer(),
                String.serializer(),
            ),
            value,
        )

    @TypeConverter
    fun fromStringToMap(value: String): Map<String, String> =
        JsonProvider.json.decodeFromString<Map<String, String>>(
            MapSerializer(
                String.serializer(),
                String.serializer(),
            ),
            value,
        )
}
