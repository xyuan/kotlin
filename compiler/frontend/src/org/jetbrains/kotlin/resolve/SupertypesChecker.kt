/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

class SupertypesChecker(
    storageManager: StorageManager,
    moduleDescriptor: ModuleDescriptor
) {
    private val missingSupertypes = storageManager.createMemoizedFunction { descriptor: ClassifierDescriptor ->
        val missingSupertypes = mutableSetOf<ClassifierDescriptor>()
        val type = descriptor.defaultType

        for (supertype in type.supertypes()) {
            val supertypeDeclaration = supertype.constructor.declarationDescriptor

            /*
            * TODO: expects are not checked, because findClassAcrossModuleDependencies does not work with actualization via type alias
            * Type parameters are skipped here, bounds of type parameters are checked in declaration checker separately
            * Local declarations are ignored for optimization
            */
            if (supertypeDeclaration !is ClassDescriptor || supertypeDeclaration.isExpect) continue
            if (supertypeDeclaration.visibility == Visibilities.LOCAL) continue

            val superTypeClassId = supertypeDeclaration.classId ?: continue
            val dependency = moduleDescriptor.findClassAcrossModuleDependencies(superTypeClassId)

            if (dependency == null || dependency is NotFoundClasses.MockClassDescriptor) {
                missingSupertypes.add(supertypeDeclaration)
            }
        }

        missingSupertypes.toSet()
    }

    fun getMissingSupertypes(descriptor: ClassifierDescriptor) = missingSupertypes(descriptor)
    fun getMissingSupertypes(type: KotlinType) = type.constructor.declarationDescriptor?.let { missingSupertypes(it) } ?: emptySet()
}