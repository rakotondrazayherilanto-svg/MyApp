package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppColorTheme
import com.example.data.AppSettings
import com.example.data.AppSettingsManager
import com.example.data.AttendanceEntity
import com.example.data.FeePaymentEntity
import com.example.data.FontScale
import com.example.data.GradeEntity
import com.example.data.ScheduleEntity
import com.example.data.SchoolProfile
import com.example.data.SchoolRepository
import com.example.data.StudentEntity
import com.example.data.SubjectEntity
import com.example.data.TeacherEntity
import com.example.data.ThemeMode
import com.example.utils.ProductLicenseManager
import com.example.utils.ProductLicenseState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SchoolViewModel(
    private val repository: SchoolRepository,
    private val settingsManager: AppSettingsManager? = null,
    private val licenseManager: ProductLicenseManager? = null
) : ViewModel() {

    val licenseState: StateFlow<ProductLicenseState> = licenseManager?.licenseState
        ?: kotlinx.coroutines.flow.MutableStateFlow(ProductLicenseState(isActivated = false))

    val appSettings: StateFlow<AppSettings> = settingsManager?.settings
        ?: kotlinx.coroutines.flow.MutableStateFlow(AppSettings())

    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<TeacherEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<ScheduleEntity>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val grades: StateFlow<List<GradeEntity>> = repository.allGrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fees: StateFlow<List<FeePaymentEntity>> = repository.allFeePayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendances: StateFlow<List<AttendanceEntity>> = repository.allAttendances
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Formatters ---
    fun formatAriary(amount: Long): String {
        val symbols = DecimalFormatSymbols(Locale.FRENCH).apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat("#,###", symbols)
        return "${formatter.format(amount)} Ar"
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        return sdf.format(Date())
    }

    fun getAdjacentDate(dateStr: String, daysOffset: Int): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val parsed = sdf.parse(dateStr) ?: Date()
            val cal = java.util.Calendar.getInstance().apply {
                time = parsed
                add(java.util.Calendar.DAY_OF_YEAR, daysOffset)
            }
            sdf.format(cal.time)
        } catch (e: Exception) {
            getCurrentDate()
        }
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH)
            formatter.format(date).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.FRENCH) else it.toString() }
        } catch (e: Exception) {
            dateStr
        }
    }

    // --- Student Actions ---
    fun addStudent(
        nom: String,
        prenoms: String,
        classe: String,
        sexe: String,
        dateNaissance: String,
        nomTuteur: String,
        contactTuteur: String,
        adresse: String,
        ecolageMensuelAr: Long
    ) {
        viewModelScope.launch {
            val count = (students.value.size + 1).toString().padStart(3, '0')
            val matricule = "LP3F-26-$count"
            val student = StudentEntity(
                matricule = matricule,
                nom = nom.uppercase().trim(),
                prenoms = prenoms.trim(),
                classe = classe,
                sexe = sexe,
                dateNaissance = dateNaissance,
                nomTuteur = nomTuteur,
                contactTuteur = contactTuteur,
                adresse = if (adresse.isBlank()) "Fenoarivobe" else adresse,
                ecolageMensuelAr = ecolageMensuelAr
            )
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // --- Teacher Actions ---
    fun addTeacher(
        nom: String,
        prenoms: String,
        matieres: String,
        telephone: String,
        email: String,
        classes: String
    ) {
        viewModelScope.launch {
            val teacher = TeacherEntity(
                nom = nom.uppercase().trim(),
                prenoms = prenoms.trim(),
                matieres = matieres.trim(),
                telephone = telephone.trim(),
                email = email.trim(),
                classesAssignees = classes.trim()
            )
            repository.insertTeacher(teacher)
        }
    }

    fun deleteTeacher(teacher: TeacherEntity) {
        viewModelScope.launch {
            repository.deleteTeacher(teacher)
        }
    }

    // --- Subject Actions ---
    fun addSubject(code: String, nom: String, coef: Int, enseignant: String) {
        viewModelScope.launch {
            val subject = SubjectEntity(
                code = code.uppercase().trim(),
                nom = nom.trim(),
                coefficientDefaut = coef,
                enseignantNom = enseignant.trim()
            )
            repository.insertSubject(subject)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // --- Schedule Actions ---
    fun addSchedule(
        classe: String,
        jour: String,
        heureDebut: String,
        heureFin: String,
        matiere: String,
        enseignant: String,
        salle: String
    ) {
        viewModelScope.launch {
            val schedule = ScheduleEntity(
                classe = classe,
                jourSemaine = jour,
                heureDebut = heureDebut,
                heureFin = heureFin,
                matiere = matiere,
                enseignant = enseignant,
                salle = salle
            )
            repository.insertSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    // --- Fee Actions (Ariary) ---
    fun recordFeePayment(
        student: StudentEntity,
        mois: String,
        montantAr: Long,
        modePaiement: String,
        remarques: String = ""
    ) {
        viewModelScope.launch {
            val receiptNum = "REC-LP3F-${(fees.value.size + 1).toString().padStart(4, '0')}"
            val payment = FeePaymentEntity(
                studentId = student.id,
                studentMatricule = student.matricule,
                studentNom = student.nomComplet,
                classe = student.classe,
                mois = mois,
                montantAr = montantAr,
                datePaiement = getCurrentDate(),
                modePaiement = modePaiement,
                referenceRecu = receiptNum,
                remarques = remarques
            )
            repository.insertFeePayment(payment)
        }
    }

    fun deleteFeePayment(payment: FeePaymentEntity) {
        viewModelScope.launch {
            repository.deleteFeePayment(payment)
        }
    }

    // --- Grade Actions ---
    fun addGrade(
        student: StudentEntity,
        matiere: String,
        trimestre: String,
        typeDevoir: String,
        noteSur20: Double,
        coefficient: Int
    ) {
        viewModelScope.launch {
            val grade = GradeEntity(
                studentId = student.id,
                studentMatricule = student.matricule,
                studentNom = student.nomComplet,
                classe = student.classe,
                matiere = matiere,
                trimestre = trimestre,
                typeDevoir = typeDevoir,
                noteSur20 = noteSur20,
                coefficient = coefficient,
                dateSaisie = getCurrentDate()
            )
            repository.insertGrade(grade)
        }
    }

    fun deleteGrade(grade: GradeEntity) {
        viewModelScope.launch {
            repository.deleteGrade(grade)
        }
    }

    // --- Attendance Actions ---
    fun markAttendance(
        student: StudentEntity,
        sessionMatiere: String,
        statut: String,
        motif: String = "",
        dateJour: String = getCurrentDate()
    ) {
        viewModelScope.launch {
            val existing = repository.getAttendanceRecord(student.id, dateJour, sessionMatiere)
            if (existing != null) {
                repository.updateAttendance(existing.copy(statut = statut, motif = motif))
            } else {
                val attendance = AttendanceEntity(
                    studentId = student.id,
                    studentMatricule = student.matricule,
                    studentNom = student.nomComplet,
                    classe = student.classe,
                    dateJour = dateJour,
                    sessionMatiere = sessionMatiere,
                    statut = statut,
                    motif = motif
                )
                repository.insertAttendance(attendance)
            }
        }
    }

    fun markAllPresentForSession(
        students: List<StudentEntity>,
        sessionMatiere: String,
        dateJour: String
    ) {
        viewModelScope.launch {
            students.forEach { st ->
                val existing = repository.getAttendanceRecord(st.id, dateJour, sessionMatiere)
                if (existing != null) {
                    repository.updateAttendance(existing.copy(statut = "Présent", motif = ""))
                } else {
                    repository.insertAttendance(
                        AttendanceEntity(
                            studentId = st.id,
                            studentMatricule = st.matricule,
                            studentNom = st.nomComplet,
                            classe = st.classe,
                            dateJour = dateJour,
                            sessionMatiere = sessionMatiere,
                            statut = "Présent",
                            motif = ""
                        )
                    )
                }
            }
        }
    }

    fun updateAttendanceRecord(attendance: AttendanceEntity) {
        viewModelScope.launch {
            repository.updateAttendance(attendance)
        }
    }

    fun deleteAttendance(attendance: AttendanceEntity) {
        viewModelScope.launch {
            repository.deleteAttendance(attendance)
        }
    }

    // --- Settings & Display Customization ---
    fun setThemeMode(mode: ThemeMode) {
        settingsManager?.setThemeMode(mode)
    }

    fun setColorTheme(theme: AppColorTheme) {
        settingsManager?.setColorTheme(theme)
    }

    fun setFontScale(scale: FontScale) {
        settingsManager?.setFontScale(scale)
    }

    fun updateSchoolProfile(profile: SchoolProfile) {
        settingsManager?.updateSchoolProfile(profile)
    }

    fun resetSettingsToDefault() {
        settingsManager?.resetToDefaults()
    }

    // --- Product License & Activation ---
    fun activateProduct(key: String, clientName: String = "Lycée Privé FJKM Fenoarivobe"): Boolean {
        return licenseManager?.activate(key, clientName) ?: false
    }

    fun deactivateProduct() {
        licenseManager?.deactivate()
    }

    fun getInstallationId(): String {
        return licenseManager?.getOrCreateInstallationId() ?: "LP3F-2026"
    }

    fun generateKeyForInstallationId(id: String): String {
        return licenseManager?.generateValidKeyForInstallationId(id) ?: "LP3F-0000-0000-0000"
    }

    fun verifyOwnerPin(pin: String): Boolean {
        return licenseManager?.verifyOwnerPin(pin) ?: (pin == "2026")
    }

    fun setOwnerPin(newPin: String) {
        licenseManager?.setOwnerPin(newPin)
    }

    // --- Data Management: Clear App / Blank Slate or Reload Demo ---
    fun clearAllData(keepSubjects: Boolean = true, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllData(keepSubjects)
            onComplete()
        }
    }

    fun reloadDemoData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.reloadDemoData()
            onComplete()
        }
    }
}

class SchoolViewModelFactory(
    private val repository: SchoolRepository,
    private val settingsManager: AppSettingsManager? = null,
    private val licenseManager: ProductLicenseManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SchoolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SchoolViewModel(repository, settingsManager, licenseManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
