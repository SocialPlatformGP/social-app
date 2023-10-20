package com.gp.socialapp.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name="id") val id: String,
    @ColumnInfo(name="author_id") val authorID: Long,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="body") val body: String,
    @ColumnInfo(name="upvotes") var upvotes: Int,
    @ColumnInfo(name="downvotes") val downvotes: Int,
    @ColumnInfo(name="moderation_status") val moderationStatus: String,
    @ColumnInfo(name="edit_status") val editStatus: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeLong(authorID)
        parcel.writeString(publishedAt)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeInt(upvotes)
        parcel.writeInt(downvotes)
        parcel.writeString(moderationStatus)
        parcel.writeByte(if (editStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostEntity> {
        override fun createFromParcel(parcel: Parcel): PostEntity {
            return PostEntity(parcel)
        }

        override fun newArray(size: Int): Array<PostEntity?> {
            return arrayOfNulls(size)
        }
    }
}
