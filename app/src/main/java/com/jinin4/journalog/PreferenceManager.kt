package com.jinin4.journalog

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

//반정현 - 24.01.22

object PreferenceSerializer : Serializer<Preference> {
    override val defaultValue: Preference = Preference.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Preference {
        try {
            return Preference.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Preference, output: OutputStream) = t.writeTo(output)
}

val Context.dataStore: DataStore<Preference> by dataStore(
    fileName = "preference.pb",
    serializer = PreferenceSerializer
)