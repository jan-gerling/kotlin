/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline

import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.js.backend.ast.JsFunction
import org.jetbrains.kotlin.js.backend.ast.JsInvocation
import org.jetbrains.kotlin.js.backend.ast.metadata.descriptor
import org.jetbrains.kotlin.js.backend.ast.metadata.inlineStrategy
import org.jetbrains.kotlin.js.backend.ast.metadata.psiElement
import org.jetbrains.kotlin.js.inline.context.FunctionContext
import org.jetbrains.kotlin.js.inline.util.FunctionWithWrapper
import org.jetbrains.kotlin.resolve.inline.InlineStrategy
import java.util.*

/**
 *  There are two ways the inliner may arrive to an inline function declaration. Either the topmost visitor arrive to a declaration, which
 *  has never been used before. Or it visits it's invocation, and has to obtain the inline function body. Current strategy is to processes
 *  the inline function declaration before inlining it's invocation.
 *
 *  Thus the inliner effectively implements a DFS. InlinerCycleReporter manages the DFS state. Also it detects and reports cycles.
 */
class InlinerCycleReporter(
    val trace: DiagnosticSink,
    private val functionContext: FunctionContext
) {

    private enum class VisitedState { IN_PROCESS, PROCESSED }

    private val functionVisitingState = mutableMapOf<JsFunction, VisitedState>()

    // these are needed for error reporting, when inliner detects cycle
    private val namedFunctionsStack = Stack<JsFunction>()

    private val currentNamedFunction: JsFunction?
        get() = if (namedFunctionsStack.empty()) null else namedFunctionsStack.peek()

    private val inlineCallInfos = LinkedList<JsCallInfo>()

    // Puts `function` on the `namedFunctionsStack` for inline call cycles reporting
    fun <T> withFunction(function: JsFunction, body: () -> T): T {
        if (function in functionContext.functionsByFunctionNodes.keys) {
            namedFunctionsStack.push(function)
        }

        val result = body()

        if (currentNamedFunction == function) {
            namedFunctionsStack.pop()
        }

        return result
    }

    fun processInlineFunction(definition: FunctionWithWrapper, call: JsInvocation?, doProcess: () -> Unit) {

        when (functionVisitingState[definition.function]) {
            VisitedState.IN_PROCESS -> {
                reportInlineCycle(call, definition.function)
                return
            }
            VisitedState.PROCESSED -> return
        }

        val function = definition.function

        functionVisitingState[function] = VisitedState.IN_PROCESS

        val result = withFunction(function, doProcess)

        functionVisitingState[function] = VisitedState.PROCESSED

        return result
    }


    fun <T> inlineCall(call: JsInvocation, doInline: () -> T): T {
        currentNamedFunction?.let {
            inlineCallInfos.add(JsCallInfo(call, it))
        }

        val result = doInline()

        if (!inlineCallInfos.isEmpty()) {
            if (inlineCallInfos.last.call == call) {
                inlineCallInfos.removeLast()
            }
        }

        return result
    }

    private fun reportInlineCycle(call: JsInvocation?, calledFunction: JsFunction) {
        call?.inlineStrategy = InlineStrategy.NOT_INLINE
        val it = inlineCallInfos.descendingIterator()

        while (it.hasNext()) {
            val callInfo = it.next()
            val psiElement = callInfo.call.psiElement

            val descriptor = callInfo.call.descriptor
            if (psiElement != null && descriptor != null) {
                trace.report(Errors.INLINE_CALL_CYCLE.on(psiElement, descriptor))
            }

            if (callInfo.containingFunction == calledFunction) {
                break
            }
        }
    }
}

private class JsCallInfo(val call: JsInvocation, val containingFunction: JsFunction)