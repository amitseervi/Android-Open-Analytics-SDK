package com.rignis.analyticssdk.mock

import com.rignis.analyticssdk.config.MetaDataReader

object MockMetaDataReader : MetaDataReader {
    override fun getClientId(): String {
        return "test-client-id"
    }
}