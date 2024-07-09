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
package com.rignis.analyticssdk.database

internal interface DBAdapter {
    fun getTotalEventCount(): Int
    fun deleteLongPendingRequests(timestamp: Long)
    fun resetFailedRequest()
    fun hasPendingRequest(): Boolean
    fun readFirstNEvents(n: Int): RequestBatch
    fun handleBatchRequestFail(batch: RequestBatch)
    fun handleBatchRequestSuccess(batch: RequestBatch)
    fun addEvent(name: String, params: Map<String, String>)
}