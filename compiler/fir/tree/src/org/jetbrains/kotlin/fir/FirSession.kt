/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analyzer.ModuleInfo
import kotlin.reflect.KClass

interface FirSession {
    val moduleInfo: ModuleInfo?

    val sessionProvider: FirSessionProvider? get() = null

    val components: Map<KClass<*>, Any>

    fun <T : Any> getService(kclass: KClass<T>): T =
        components[kclass] as T

    fun associate(fir: FirElement, psi: PsiElement) {}

    fun getFir(psi: PsiElement): FirElement? = null
}

interface FirSessionProvider {
    fun getSession(moduleInfo: ModuleInfo): FirSession?
}

inline fun <reified T : Any> FirSession.service(): T =
    getService(T::class)