package com.gradeflow.engine

import com.gradeflow.data.model.RegulationConfig
import com.gradeflow.data.model.UniversityConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Regulation Engine - Dynamically switches formulas and grade mappings
 * based on the selected regulation year (e.g., R18, R22, CBCS).
 *
 * Architecture:
 * 1. Load base university config
 * 2. If a regulation is selected, overlay regulation-specific overrides
 * 3. Return a resolved config that the FormulaEngine can use directly
 *
 * This means the FormulaEngine never needs to know about regulations -
 * it just receives a fully resolved config.
 */
@Singleton
class RegulationEngine @Inject constructor() {

    /**
     * Resolve a university config for a specific regulation.
     * Overlays regulation-specific overrides onto the base config.
     *
     * @param baseConfig The root university configuration
     * @param regulationId The selected regulation (e.g., "R22", "R18", "CBCS")
     * @return A resolved config with regulation overrides applied
     */
    fun resolveForRegulation(
        baseConfig: UniversityConfig,
        regulationId: String?
    ): UniversityConfig {
        if (regulationId.isNullOrBlank()) return baseConfig

        val regulationConfig = baseConfig.regulationConfigs[regulationId] ?: return baseConfig

        return applyRegulationOverrides(baseConfig, regulationConfig)
    }

    /**
     * Get available regulations for a university.
     */
    fun getAvailableRegulations(config: UniversityConfig): List<String> {
        // Return from regulation_configs keys if available, else from regulations list
        return if (config.regulationConfigs.isNotEmpty()) {
            config.regulationConfigs.keys.toList()
        } else {
            config.regulations
        }
    }

    /**
     * Get regulation display name.
     */
    fun getRegulationName(config: UniversityConfig, regulationId: String): String {
        return config.regulationConfigs[regulationId]?.regulationName
            ?: regulationId
    }

    /**
     * Check if a university has multiple regulations.
     */
    fun hasMultipleRegulations(config: UniversityConfig): Boolean {
        return config.regulationConfigs.size > 1 || config.regulations.size > 1
    }

    /**
     * Apply regulation-specific overrides to the base config.
     * Only non-null fields in RegulationConfig override the base.
     */
    private fun applyRegulationOverrides(
        base: UniversityConfig,
        regulation: RegulationConfig
    ): UniversityConfig {
        return base.copy(
            gradeMapping = regulation.gradeMapping ?: base.gradeMapping,
            percentageFormula = regulation.percentageFormula ?: base.percentageFormula,
            tgpaFormula = regulation.tgpaFormula ?: base.tgpaFormula,
            cgpaFormula = regulation.cgpaFormula ?: base.cgpaFormula,
            failGrades = regulation.failGrades ?: base.failGrades,
            graceRules = regulation.graceRules ?: base.graceRules,
            backlogPolicy = regulation.backlogPolicy ?: base.backlogPolicy
        )
    }
}
