package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope:CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            //db operation
            val dao = database.get().taskDao() //Used to instantiate the database lazily
            //Wehn the callback class is being created, the dabase in the constructor won't be provided by hilt. Instead, it will wait auntil
            //it executes databse.get()
            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Wash the clothes", important = true))
                dao.insert(Task("Wash the house", completed = true))
                dao.insert(Task("Visit everyone"))
            }



        }
    }

}