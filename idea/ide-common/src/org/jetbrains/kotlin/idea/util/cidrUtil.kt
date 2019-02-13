/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("CidrUtil")

package org.jetbrains.kotlin.idea.util

import com.intellij.psi.PsiElement
import com.intellij.util.PlatformUtils
import org.jetbrains.kotlin.psi.KtFile

// Currently CIDR IDEs (CLion and AppCode) have no Java support.
// Use this property to bypass Java-specific logic in CIDR.
val isRunningInCidrIde: Boolean by lazy(LazyThreadSafetyMode.PUBLICATION) {
    PlatformUtils.isCidr()
}

// Use this property to avoid running analysis and highlighting for KTS in CIDR.
val PsiElement?.doNotAnalyzeInCidrIde: Boolean
    get() = isRunningInCidrIde && (this?.containingFile as? KtFile)?.isScript() == true
