package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ScratchCard
import com.example.data.model.TaskItem
import com.example.data.model.TransactionRecord
import com.example.data.model.UserStats
import com.example.data.model.WithdrawalRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsSync(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStats)

    @Update
    suspend fun update(stats: UserStats)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionRecord): Long

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getCount(): Int
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskItem>)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setCompleted(taskId: String, completed: Boolean = true)

    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :taskId")
    suspend fun markCompleted(taskId: String)

    @Query("UPDATE tasks SET isCompleted = 0")
    suspend fun resetAllTasks()

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getCount(): Int
}

@Dao
interface ScratchDao {
    @Query("SELECT * FROM scratch_cards")
    fun getAllScratchCards(): Flow<List<ScratchCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<ScratchCard>)

    @Query("UPDATE scratch_cards SET isScratched = 1 WHERE id = :cardId")
    suspend fun markScratched(cardId: Long)

    @Query("UPDATE scratch_cards SET isScratched = 0")
    suspend fun resetAllCards()

    @Query("SELECT COUNT(*) FROM scratch_cards")
    suspend fun getCount(): Int

    @Query("DELETE FROM scratch_cards")
    suspend fun deleteAll()
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalRecord>>

    @Query("SELECT * FROM withdrawals WHERE id = :id")
    suspend fun getWithdrawalById(id: Long): WithdrawalRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(withdrawal: WithdrawalRecord): Long

    @Update
    suspend fun update(withdrawal: WithdrawalRecord)

    @Query("UPDATE withdrawals SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM withdrawals WHERE id = :id")
    suspend fun deleteWithdrawal(id: Long)
}
