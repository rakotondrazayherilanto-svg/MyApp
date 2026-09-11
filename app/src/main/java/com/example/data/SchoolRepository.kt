package com.example.data

import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val dao: SchoolDao) {
    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    fun getStudentsByClass(classe: String): Flow<List<StudentEntity>> = dao.getStudentsByClass(classe)
    fun getStudentById(id: Long): Flow<StudentEntity?> = dao.getStudentById(id)
    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    // Teachers
    val allTeachers: Flow<List<TeacherEntity>> = dao.getAllTeachers()
    suspend fun insertTeacher(teacher: TeacherEntity): Long = dao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: TeacherEntity) = dao.updateTeacher(teacher)
    suspend fun deleteTeacher(teacher: TeacherEntity) = dao.deleteTeacher(teacher)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    suspend fun insertSubject(subject: SubjectEntity): Long = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = dao.deleteSubject(subject)

    // Schedules
    val allSchedules: Flow<List<ScheduleEntity>> = dao.getAllSchedules()
    fun getSchedulesByClassAndDay(classe: String, jour: String): Flow<List<ScheduleEntity>> =
        dao.getSchedulesByClassAndDay(classe, jour)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long = dao.insertSchedule(schedule)
    suspend fun deleteSchedule(schedule: ScheduleEntity) = dao.deleteSchedule(schedule)

    // Grades
    val allGrades: Flow<List<GradeEntity>> = dao.getAllGrades()
    fun getGradesByStudent(studentId: Long): Flow<List<GradeEntity>> = dao.getGradesByStudent(studentId)
    fun getGradesByClassAndTrimestre(classe: String, trimestre: String): Flow<List<GradeEntity>> =
        dao.getGradesByClassAndTrimestre(classe, trimestre)
    suspend fun insertGrade(grade: GradeEntity): Long = dao.insertGrade(grade)
    suspend fun deleteGrade(grade: GradeEntity) = dao.deleteGrade(grade)

    // Fee Payments (Ariary)
    val allFeePayments: Flow<List<FeePaymentEntity>> = dao.getAllFeePayments()
    fun getFeePaymentsByStudent(studentId: Long): Flow<List<FeePaymentEntity>> = dao.getFeePaymentsByStudent(studentId)
    suspend fun insertFeePayment(payment: FeePaymentEntity): Long = dao.insertFeePayment(payment)
    suspend fun deleteFeePayment(payment: FeePaymentEntity) = dao.deleteFeePayment(payment)

    // Attendances
    val allAttendances: Flow<List<AttendanceEntity>> = dao.getAllAttendances()
    fun getAttendancesByDateAndClass(date: String, classe: String): Flow<List<AttendanceEntity>> =
        dao.getAttendancesByDateAndClass(date, classe)
    fun getAttendancesByStudent(studentId: Long): Flow<List<AttendanceEntity>> = dao.getAttendancesByStudent(studentId)
    suspend fun getAttendanceRecord(studentId: Long, date: String, matiere: String): AttendanceEntity? =
        dao.getAttendanceRecord(studentId, date, matiere)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long = dao.insertAttendance(attendance)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>) = dao.insertAttendances(attendances)
    suspend fun updateAttendance(attendance: AttendanceEntity) = dao.updateAttendance(attendance)
    suspend fun deleteAttendance(attendance: AttendanceEntity) = dao.deleteAttendance(attendance)

    // Clear data to start with a blank database
    suspend fun clearAllData(keepSubjects: Boolean = true) {
        dao.deleteAllStudents()
        dao.deleteAllTeachers()
        dao.deleteAllSchedules()
        dao.deleteAllGrades()
        dao.deleteAllFeePayments()
        dao.deleteAllAttendances()
        if (!keepSubjects) {
            dao.deleteAllSubjects()
        }
    }

    // Reload demo sample data if requested by user
    suspend fun reloadDemoData() {
        dao.deleteAllStudents()
        dao.deleteAllTeachers()
        dao.deleteAllSubjects()
        dao.deleteAllSchedules()
        dao.deleteAllGrades()
        dao.deleteAllFeePayments()
        dao.deleteAllAttendances()

        dao.insertSubjects(InitialData.sampleSubjects)
        dao.insertTeachers(InitialData.sampleTeachers)
        dao.insertStudents(InitialData.sampleStudents)
        dao.insertSchedules(InitialData.sampleSchedules)
        dao.insertFeePayments(InitialData.sampleFees)
        dao.insertGrades(InitialData.sampleGrades)
        dao.insertAttendances(InitialData.sampleAttendances)
    }

    // Seeding if empty
    suspend fun seedIfEmpty() {
        dao.insertSubjects(InitialData.sampleSubjects)
    }

    suspend fun seedAdditionalCycles() {
        dao.insertSubjects(InitialData.additionalSubjects)
    }
}
