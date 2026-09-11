package com.example.data

object SchoolConstants {

    // Cycles d'enseignement
    const val CYCLE_MATERNELLE = "Maternelle"
    const val CYCLE_PRIMAIRE = "Primaire"
    const val CYCLE_COLLEGE = "Collège"
    const val CYCLE_LYCEE = "Lycée"

    val CYCLES = listOf("Tous les Cycles", CYCLE_MATERNELLE, CYCLE_PRIMAIRE, CYCLE_COLLEGE, CYCLE_LYCEE)

    // Classes par cycle
    val MATERNELLE_CLASSES = listOf(
        "Garderie",
        "Petite Section",
        "Moyenne Section",
        "Grande Section"
    )

    val PRIMAIRE_CLASSES = listOf(
        "11ème (CP1)",
        "10ème (CP2)",
        "9ème (CE)",
        "8ème (CM1)",
        "7ème (CM2)"
    )

    val COLLEGE_CLASSES = listOf(
        "6ème",
        "5ème",
        "4ème",
        "3ème"
    )

    val LYCEE_CLASSES = listOf(
        "2nde",
        "1ère A",
        "1ère C",
        "1ère D",
        "Tle A",
        "Tle C",
        "Tle D"
    )

    // Liste complète et ordonnée de la Garderie à la Terminale (20 classes)
    val ALL_CLASSES: List<String> = MATERNELLE_CLASSES + PRIMAIRE_CLASSES + COLLEGE_CLASSES + LYCEE_CLASSES

    /**
     * Retourne les classes correspondant à un cycle donné
     */
    fun getClassesForCycle(cycle: String): List<String> {
        return when (cycle) {
            CYCLE_MATERNELLE -> MATERNELLE_CLASSES
            CYCLE_PRIMAIRE -> PRIMAIRE_CLASSES
            CYCLE_COLLEGE -> COLLEGE_CLASSES
            CYCLE_LYCEE -> LYCEE_CLASSES
            else -> ALL_CLASSES
        }
    }

    /**
     * Détermine le cycle d'enseignement pour une classe
     */
    fun getCycleForClass(classe: String): String {
        val trimmed = classe.trim()
        return when {
            MATERNELLE_CLASSES.any { it.equals(trimmed, ignoreCase = true) } ||
                    trimmed.contains("Garderie", ignoreCase = true) ||
                    trimmed.contains("Section", ignoreCase = true) ||
                    trimmed.contains("Maternelle", ignoreCase = true) -> CYCLE_MATERNELLE

            PRIMAIRE_CLASSES.any { it.equals(trimmed, ignoreCase = true) } ||
                    trimmed.startsWith("11ème", ignoreCase = true) ||
                    trimmed.startsWith("10ème", ignoreCase = true) ||
                    trimmed.startsWith("9ème", ignoreCase = true) ||
                    trimmed.startsWith("8ème", ignoreCase = true) ||
                    trimmed.startsWith("7ème", ignoreCase = true) ||
                    trimmed.contains("CP", ignoreCase = true) ||
                    trimmed.contains("CE", ignoreCase = true) ||
                    trimmed.contains("CM", ignoreCase = true) -> CYCLE_PRIMAIRE

            COLLEGE_CLASSES.any { it.equals(trimmed, ignoreCase = true) } ||
                    trimmed.startsWith("6ème", ignoreCase = true) ||
                    trimmed.startsWith("5ème", ignoreCase = true) ||
                    trimmed.startsWith("4ème", ignoreCase = true) ||
                    trimmed.startsWith("3ème", ignoreCase = true) -> CYCLE_COLLEGE

            else -> CYCLE_LYCEE
        }
    }

    /**
     * Écolage mensuel standard en Ariary (Ar) selon la classe et le cycle
     */
    fun getDefaultTuitionForClass(classe: String): Long {
        val cycle = getCycleForClass(classe)
        return when (cycle) {
            CYCLE_MATERNELLE -> 25000L
            CYCLE_PRIMAIRE -> 28000L
            CYCLE_COLLEGE -> 32000L
            CYCLE_LYCEE -> when {
                classe.contains("2nde", ignoreCase = true) -> 35000L
                classe.contains("1ère", ignoreCase = true) -> 40000L
                else -> 45000L
            }
            else -> 35000L
        }
    }

    /**
     * Comparaison tolérante de classe (ex: "11ème" matche "11ème (CP1)")
     */
    fun classMatches(itemClass: String, filterClass: String): Boolean {
        if (filterClass == "Toutes" || filterClass == "Tous") return true
        if (itemClass.equals(filterClass, ignoreCase = true)) return true

        val s1 = itemClass.lowercase().replace(" ", "")
        val s2 = filterClass.lowercase().replace(" ", "")
        return s1.startsWith(s2) || s2.startsWith(s1)
    }
}
