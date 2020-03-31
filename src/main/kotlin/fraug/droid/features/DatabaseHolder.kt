package fraug.droid.features

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import fraug.droid.db.FishDatabase
import java.util.*


val fishQueries by lazy {
    DbHelper("fish.db").database.fishQueries
}

class DbHelper(private val filePath: String) {
    val driver by lazy {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + filePath, Properties())
    }

    val database by lazy {
        val currentVer = version
        if (currentVer == 0) {
            FishDatabase.Schema.create(driver)
            version = 1
            println("init: created tables, setVersion to 1")
        } else {
            val schemaVer: Int = FishDatabase.Schema.version
            if (schemaVer > currentVer) {
                FishDatabase.Schema.migrate(driver, currentVer, schemaVer)
                version = schemaVer
                println("init: migrated from $currentVer to $schemaVer")
            } else {
                //println("init")
            }
        }
        FishDatabase(driver)
    }

    private var version: Int
        get() {
            val sqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
            return sqlCursor.getLong(0)!!.toInt()
        }
        private set(version) {
            driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
        }
}