package name.lmj0011.courierlocker.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "trips_table")
data class Trip(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "timestamp")
    var timestamp: String = "",

    @ColumnInfo(name = "pickupAddress")
    var pickupAddress: String = "",

    @ColumnInfo(name = "pickupAddressLatitude")
    var pickupAddressLatitude: Double = 0.toDouble(),

    @ColumnInfo(name = "pickupAddressLongitude")
    var pickupAddressLongitude: Double = 0.toDouble(),

    @Nullable
    @ColumnInfo(name = "dropOffAddress")
    var dropOffAddress: String = "",

    @ColumnInfo(name = "dropOffAddressLatitude")
    var dropOffAddressLatitude: Double = 0.toDouble(),

    @ColumnInfo(name = "dropOffAddressLongitude")
    var dropOffAddressLongitude: Double = 0.toDouble(),

    @ColumnInfo(name = "distance")
    var distance: Double = 0.toDouble(),

    @ColumnInfo(name = "payAmount")
    var payAmount: String = "",

    @ColumnInfo(name = "gigName")
    var gigName: String = ""
)