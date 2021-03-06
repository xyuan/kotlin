/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt3.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("plugins/kapt3/kapt3-compiler/testData/kotlinRunner")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class KotlinKaptContextTestGenerated extends AbstractKotlinKaptContextTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInKotlinRunner() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("plugins/kapt3/kapt3-compiler/testData/kotlinRunner"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @TestMetadata("NestedClasses.kt")
    public void testNestedClasses() throws Exception {
        runTest("plugins/kapt3/kapt3-compiler/testData/kotlinRunner/NestedClasses.kt");
    }

    @TestMetadata("Overloads.kt")
    public void testOverloads() throws Exception {
        runTest("plugins/kapt3/kapt3-compiler/testData/kotlinRunner/Overloads.kt");
    }

    @TestMetadata("Simple.kt")
    public void testSimple() throws Exception {
        runTest("plugins/kapt3/kapt3-compiler/testData/kotlinRunner/Simple.kt");
    }
}
