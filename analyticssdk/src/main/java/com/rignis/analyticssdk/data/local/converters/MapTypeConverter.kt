package com.rignis.analyticssdk.data.local.converters

import androidx.room.TypeConverter
import com.rignis.analyticssdk.utils.JsonProvider
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

class MapTypeConverter {
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
