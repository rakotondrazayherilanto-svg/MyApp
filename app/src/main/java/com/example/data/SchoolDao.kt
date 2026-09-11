package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY nom ASC, prenoms ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE classe = :classe ORDER BY nom ASC")
    fun getStudentsByClass(classe: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // --- Teachers ---
    @Query("SELECT * FROM teachers ORDER BY nom ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherEntity>)

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity)

    @Delete
    suspend fun deleteTeacher(teacher: TeacherEntity)

    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY nom ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    // --- Schedules ---
    @Query("SELECT * FROM schedules ORDER BY heureDebut ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE classe = :classe AND jourSemaine = :jour ORDER BY heureDebut ASC")
    fun getSchedulesByClassAndDay(classe: String, jour: String): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ScheduleEntity>)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    // --- Grades ---
    @Query("SELECT * FROM grades ORDER BY id DESC")
    fun getAllGrades(): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId ORDER BY trimestre ASC, matiere ASC")
    fun getGradesByStudent(studentId: Long): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE classe = :classe AND trimestre = :trimestre")
    fun getGradesByClassAndTrimestre(classe: String, trimestre: String): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<GradeEntity>)

    @Delete
    suspend fun deleteGrade(grade: GradeEntity)

    // --- Fees / Écolages (Ariary) ---
    @Query("SELECT * FROM fee_payments ORDER BY id DESC")
    fun getAllFeePayments(): Flow<List<FeePaymentEntity>>

    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeePaymentsByStudent(studentId: Long): Flow<List<FeePaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeePayment(payment: FeePaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeePayments(payments: List<FeePaymentEntity>)

    @Delete
    suspend fun deleteFeePayment(payment: FeePaymentEntity)

    // --- Attendances ---
    @Query("SELECT * FROM attendances ORDER BY dateJour DESC, id DESC")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE dateJour = :date AND classe = :classe")
    fun getAttendancesByDateAndClass(date: String, classe: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE studentId = :studentId ORDER BY dateJour DESC")
    fun getAttendancesByStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances WHERE studentId = :studentId AND dateJour = :date AND sessionMatiere = :matiere LIMIT 1")
    suspend fun getAttendanceRecord(studentId: Long, date: String, matiere: String): AttendanceEntity?

    @Query("SELECT * FROM attendances WHERE dateJour = :date AND sessionMatiere = :matiere")
    fun getAttendancesByDateAndSubject(date: String, matiere: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)

    // --- Bulk Clearing Operations (Reset app to blank state) ---
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()

    @Query("DELETE FROM teachers")
    suspend fun deleteAllTeachers()

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()

    @Query("DELETE FROM grades")
    suspend fun deleteAllGrades()

    @Query("DELETE FROM fee_payments")
    suspend fun deleteAllFeePayments()

    @Query("DELETE FROM attendances")
    suspend fun deleteAllAttendances()
}
