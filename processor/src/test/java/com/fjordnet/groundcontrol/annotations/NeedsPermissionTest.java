/*
 * Copyright 2017-2018 Fjord
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjordnet.groundcontrol.annotations;

import org.junit.Test;

/**
 * Unit test cases for the {@link NeedsPermission} annotation.
 */
public class NeedsPermissionTest extends GroundControlTest {

    @Test
    public void successActivitySinglePermission() {
        assertThat("StorageManagerActivity").compilesWithoutWarnings();
    }

    @Test
    public void successFragmentSinglePermission() {
        assertThat("StorageManagerFragment").compilesWithoutWarnings();
    }

    @Test
    public void successMultiplePermissionsSingleAnnotation() {
        assertThat("MultiplePermissionsSingleAnnotation").compilesWithoutWarnings();
    }

    @Test
    public void errorNoAccessToActivity() {

        assertThat("NoActivityAccess")
                .failsToCompile()
                .withWarningCount(0)
                .withErrorCount(1)
                .withErrorContaining("must inherit from Activity or Fragment");
    }

    @Test
    public void errorPrivateMethod() {

        assertThat("AnnotatedPrivateMethod")
                .failsToCompile()
                .withWarningCount(0)
                .withErrorContaining("@NeedsPermission cannot be private");
    }

    @Test
    public void errorStaticMethod() {

        assertThat("AnnotatedStaticMethod")
                .failsToCompile()
                .withWarningCount(0)
                .withErrorContaining("@NeedsPermission cannot be static");
    }

    @Test
    public void errorMethodNonVoidReturn() {

        assertThat("AnnotatedNonVoidMethod")
                .failsToCompile()
                .withWarningCount(0)
                .withErrorContaining("@NeedsPermission must have void return type");
    }

    @Test
    public void warningAnnotatedInheritedMethods() {

        // Also include DuplicatePermissionsEquality class in compilation path,
        // because AnnotatedActivityLifecycleMethods extends from it.
        assertThat("AnnotatedActivityLifecycleMethods", "DuplicatePermissionsEquality")
                .compilesWithoutError()
                .withWarningCount(10)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromInterface() {

        assertThat("AnnotatedInterfaceMethodImplementation", "LogReader")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromAbstractClass() {

        assertThat("AnnotatedMethodFromAbstractClass", "LogWriterActivity")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void successAnnotatedInheritedMethods() {

        assertThat("AnnotatedActivityLifecycleMethodsWarningsSuppressed",
                "DuplicatePermissionsEquality")
                .compilesWithoutWarnings();
    }

    @Test
    public void successAnnotatedNonInheritedMethod() {
        assertThat("AnnotatedNonInheritedMethod", "LogWriterActivity").compilesWithoutWarnings();
    }

    @Test
    public void successAnnotatedAbstractMethod() {
        assertThat("LogWriterActivity").compilesWithoutWarnings();
    }
}
