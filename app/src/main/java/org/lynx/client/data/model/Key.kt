package org.lynx.client.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class AbonentKey(
    @PrimaryKey val abonentName: String,
    @ColumnInfo(name = "abonentPublicKey") val abonentPublicKey: String,
    @ColumnInfo(name = "sharedKey") val sharedKey: String,
    @ColumnInfo(name = "creationDate") val creationDate: Date,
    @ColumnInfo(name = "lastModifiedDate") val lastModifiedDate: Date
)

@Entity
data class PhoneKey(
    @PrimaryKey(autoGenerate = true) val keyId: Long,
    @ColumnInfo(name = "privateKey") val privateKey: String,
    @ColumnInfo(name = "publicKey") val publicKey: String,
    @ColumnInfo(name = "creationDate") val creationDate: Date,
    @ColumnInfo(name = "lastModifiedDate") val lastModifiedDate: Date
)