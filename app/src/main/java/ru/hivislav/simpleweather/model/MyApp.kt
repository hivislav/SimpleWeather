package ru.hivislav.simpleweather.model

import android.app.Application
import androidx.room.Room
import ru.hivislav.simpleweather.model.entities.room.HistoryDatabase
import ru.hivislav.simpleweather.model.entities.room.HistoryWeatherDao
import java.util.*

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: MyApp? = null
        private const val DB_NAME = "History.db"
        private var database: HistoryDatabase? = null

        fun getHistoryWeatherDao(): HistoryWeatherDao {
            if (database == null) {
                if (appInstance == null) { throw IllformedLocaleException("")
                } else {
                    database = Room.databaseBuilder(appInstance!!.applicationContext, HistoryDatabase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return database!!.historyWeatherDao()
        }
    }
}
