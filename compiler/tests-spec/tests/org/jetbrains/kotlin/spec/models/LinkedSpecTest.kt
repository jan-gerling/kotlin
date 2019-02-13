/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.spec.models

import org.jetbrains.kotlin.spec.SpecTestCasesSet
import org.jetbrains.kotlin.spec.SpecTestInfoElementType
import org.jetbrains.kotlin.spec.TestArea
import org.jetbrains.kotlin.spec.TestType
import org.jetbrains.kotlin.spec.parsers.CommonPatterns.ls
import org.jetbrains.kotlin.spec.parsers.CommonParser.withUnderscores
import org.jetbrains.kotlin.spec.parsers.CommonParser.splitByPathSeparator
import org.jetbrains.kotlin.spec.parsers.CommonParser.withSpaces
import org.jetbrains.kotlin.spec.parsers.LinkedSpecTestPatterns.placePattern
import org.jetbrains.kotlin.spec.parsers.LinkedSpecTestPatterns.relevantPlacesPattern
import java.util.regex.Matcher
import java.util.regex.Pattern

enum class LinkedSpecTestFileInfoElementType(
    override val valuePattern: Pattern? = null,
    override val required: Boolean = false
) : SpecTestInfoElementType {
    SPEC_VERSION(required = true),
    PLACE(valuePattern = placePattern, required = true),
    RELEVANT_PLACES(valuePattern = relevantPlacesPattern)
}

data class SpecPlace(
    val sections: List<String>,
    val paragraphNumber: Int,
    val sentenceNumber: Int
)

class LinkedSpecTest(
    private val specVersion: String,
    testArea: TestArea,
    testType: TestType,
    val place: SpecPlace,
    private val relevantPlaces: List<SpecPlace>?,
    testNumber: Int,
    description: String,
    cases: SpecTestCasesSet,
    unexpectedBehavior: Boolean,
    issues: Set<String>
) : AbstractSpecTest(testArea, testType, place.sections, testNumber, description, cases, unexpectedBehavior, issues) {
    override fun checkPathConsistency(pathMatcher: Matcher) =
        testArea == TestArea.valueOf(pathMatcher.group("testArea").withUnderscores())
                && testType == TestType.fromValue(pathMatcher.group("testType"))!!
                && sections == pathMatcher.group("sections").splitByPathSeparator()
                && place.paragraphNumber == pathMatcher.group("paragraphNumber").toInt()
                && place.sentenceNumber == pathMatcher.group("sentenceNumber").toInt()
                && testNumber == pathMatcher.group("testNumber").toInt()

    override fun toString() = buildString {
        append("--------------------------------------------------$ls")
        super.getUnexpectedBehaviourText()?.let { append(it + ls) }
        append("${testArea.name.withSpaces()} $testType SPEC TEST (${testType.toString().withSpaces()})$ls")
        append("SPEC VERSION: $specVersion$ls")
        append("SPEC PLACE: ${sections.joinToString()} -> paragraph: ${place.paragraphNumber} -> sentence: ${place.sentenceNumber}$ls")
        relevantPlaces?.let { append("OTHER RELEVANT SPEC PLACES:${it.joinToString { "$ls\t${sections.joinToString()} -> paragraph: ${place.paragraphNumber} -> sentence: ${place.sentenceNumber}" }}$ls") }
        append("NUMBER: $testNumber$ls")
        append("TEST CASES: ${cases.byNumbers.size.coerceAtLeast(1)}$ls")
        append("DESCRIPTION: $description$ls")
        super.getIssuesText()?.let { append(it + ls) }
    }
}
