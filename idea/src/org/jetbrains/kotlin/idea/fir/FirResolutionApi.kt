/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.fir

import org.jetbrains.kotlin.fir.builder.RawFirBuilder
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirTypedDeclaration
import org.jetbrains.kotlin.fir.java.FirJavaModuleBasedSession
import org.jetbrains.kotlin.fir.java.FirProjectSessionProvider
import org.jetbrains.kotlin.fir.resolve.transformers.FirImportResolveTransformer
import org.jetbrains.kotlin.fir.resolve.transformers.FirStatusResolveTransformer
import org.jetbrains.kotlin.fir.resolve.transformers.FirTypeResolveTransformer
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.idea.caches.project.ModuleSourceInfo
import org.jetbrains.kotlin.idea.caches.project.getModuleInfo
import org.jetbrains.kotlin.psi.KtDeclaration

enum class FirStage(val index: Int, val stubMode: Boolean) {
    // Just build FIR without resolve and without function bodies
    BUILD_ONLY(index = 0, stubMode = true) {
        override val transformers: List<FirTransformer<Nothing?>>
            get() = emptyList()
    },
    // Build FIR for declarations without function bodies, resolve imports, explicit types, visibility & modality
    DECLARATIONS(index = 3, stubMode = true) {
        override val transformers: List<FirTransformer<Nothing?>>
            get() = listOf(
                FirImportResolveTransformer(),
                FirTypeResolveTransformer(),
                FirStatusResolveTransformer()
            )
    };

    abstract val transformers: List<FirTransformer<Nothing?>>
}

fun KtDeclaration.getOrBuildFir(stage: FirStage = FirStage.DECLARATIONS): FirDeclaration {
    val moduleInfo = this.getModuleInfo() as ModuleSourceInfo
    val sessionProvider = FirProjectSessionProvider(project)
    val session = FirJavaModuleBasedSession(
        moduleInfo, sessionProvider, moduleInfo.contentScope(),
        IdeFirDependenciesSymbolProvider(moduleInfo, project, sessionProvider)
    )

    val builder = RawFirBuilder(session, stubMode = stage.stubMode)
    val file = this.containingKtFile
    val firFile = builder.buildFirFile(file)

    for (transformer in stage.transformers) {
        transformer.transformFile(firFile, null)
    }

    return session.getFir(this) as FirDeclaration
}

val FirTypedDeclaration.coneTypeSafe: ConeKotlinType? get() = (this.returnTypeRef as? FirResolvedTypeRef)?.type