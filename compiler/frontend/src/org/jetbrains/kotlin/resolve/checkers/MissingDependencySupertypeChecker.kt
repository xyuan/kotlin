/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.SupertypesChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

object MissingDependencySupertypeChecker {
    object ForDeclarations : DeclarationChecker {
        override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
            val trace = context.trace
            val supertypesChecker = context.supertypesChecker

            if (descriptor is ClassDescriptor) {
                checkSupertypes(descriptor.defaultType, declaration, trace, supertypesChecker)
            }

            if (declaration is KtTypeParameterListOwner) {
                for (ktTypeParameter in declaration.typeParameters) {
                    val typeParameterDescriptor = trace.bindingContext.get(BindingContext.TYPE_PARAMETER, ktTypeParameter) ?: continue
                    for (upperBound in typeParameterDescriptor.upperBounds) {
                        checkSupertypes(upperBound, ktTypeParameter, trace, supertypesChecker)
                    }
                }
            }
        }
    }

    object ForCalls : CallChecker {
        override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
            val descriptor = resolvedCall.resultingDescriptor

            // Constructor call leads to resolution of supertypes of enclosing class if it's an inner class constructor
            checkHierarchy(descriptor.dispatchReceiverParameter?.declaration, reportOn, context)
            // The constructed class' own supertypes are not resolved after constructor call,
            // so its containing declaration should not be checked.
            if (descriptor !is ConstructorDescriptor) {
                checkHierarchy(descriptor.containingDeclaration, reportOn, context)
                checkHierarchy(descriptor.extensionReceiverParameter?.declaration, reportOn, context)
            }
        }

        private val ReceiverParameterDescriptor.declaration
            get() = value.type.constructor.declarationDescriptor

        private fun checkHierarchy(declaration: DeclarationDescriptor?, reportOn: PsiElement, context: CallCheckerContext) {
            if (declaration !is ClassifierDescriptor) return

            checkSupertypes(declaration.defaultType, reportOn, context.trace, context.supertypesChecker)
        }
    }

    fun checkSupertypes(
        classifierType: KotlinType,
        reportOn: PsiElement,
        trace: BindingTrace,
        supertypesChecker: SupertypesChecker
    ) {
        for (missingClassifier in supertypesChecker.getMissingSupertypes(classifierType)) {
            val classifierDescriptor = classifierType.constructor.declarationDescriptor
                ?: error("Missing supertypes should not be present for type without declaration, but found for type $classifierType")
            trace.report(
                Errors.MISSING_DEPENDENCY_SUPERCLASS.on(
                    reportOn,
                    missingClassifier.fqNameSafe,
                    classifierDescriptor.fqNameSafe
                )
            )
        }
    }
}
