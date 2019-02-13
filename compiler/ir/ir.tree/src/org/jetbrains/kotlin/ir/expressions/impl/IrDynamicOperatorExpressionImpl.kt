/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrDynamicOperator
import org.jetbrains.kotlin.ir.expressions.IrDynamicOperatorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrDynamicOperatorExpressionImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val operator: IrDynamicOperator
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrDynamicOperatorExpression {

    override lateinit var receiver: IrExpression

    override val arguments: MutableList<IrExpression> = ArrayList()

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitDynamicOperatorExpression(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        for (valueArgument in arguments) {
            valueArgument.accept(visitor, data)
        }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        for (i in arguments.indices) {
            arguments[i] = arguments[i].transform(transformer, data)
        }
    }
}