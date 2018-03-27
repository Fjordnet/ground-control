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
 * Unit test cases for the {@link OnPermissionDenied} annotation.
 */
public class OnPermissionDeniedTest extends GroundControlTest {

    @Test
    public void successMultiplePermissionsDifferentOrder() {
        assertThat("DeniedMultiplePermissionsDifferentOrder").compilesWithoutWarnings();
    }

    @Test
    public void warningUnmatchedPermissions() {

        assertThat("DeniedUnmatchedPermissions")
                .compilesWithoutError()
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void warningUnmatchedPermissionsMultipleWarnings() {

        assertThat("DeniedUnmatchedPermissionsMultipleWarnings")
                .compilesWithoutError()
                .withWarningCount(3)
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void warningUnmatchedPermissionsNoGrantedCallback() {

        assertThat("DeniedUnmatchedPermissionsNoGrantedCallback")
                .compilesWithoutError()
                .withWarningCount(2)
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void successUnmatchedPermissions() {
        assertThat("DeniedUnmatchedPermissionsWarningsSuppressed").compilesWithoutWarnings();
    }

    @Test
    public void errorMultipleCallbacksSamePermissions() {

        assertThat("DeniedMultipleCallbacksSamePermissions").
                failsToCompile()
                .withErrorContaining("Cannot have multiple OnPermissionDenied annotations "
                        + "using the same permissions.");
    }

    @Test
    public void duplicatePermissionsEquality() {
        assertThat("DuplicatePermissionsEquality").compilesWithoutWarnings();
    }

    @Test
    public void errorPrivateMethod() {

        assertThat("AnnotatedPrivateMethod")
                .failsToCompile()
                .withErrorContaining("@OnPermissionDenied cannot be private");
    }

    @Test
    public void errorStaticMethod() {

        assertThat("AnnotatedStaticMethod")
                .failsToCompile()
                .withErrorContaining("@OnPermissionDenied cannot be static");
    }

    @Test
    public void errorMethodNonVoidReturn() {

        assertThat("AnnotatedNonVoidMethod")
                .failsToCompile()
                .withErrorContaining("@OnPermissionDenied must have void return type");
    }

    @Test
    public void errorMethodNonEmptyParameterList() {

        assertThat("AnnotatedNonEmptyParameterList")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_DENIED_PARAMETERS);
    }

    @Test
    public void warningAnnotatedInheritedMethods() {

        assertThat("DeniedAnnotatedActivityLifecycleMethods", "DuplicatePermissionsEquality")
                .compilesWithoutError()
                .withWarningCount(4)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromInterface() {

        assertThat("DeniedAnnotatedInterfaceMethodImplementation", "LogReader")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromAbstractClass() {

        assertThat("DeniedAnnotatedMethodFromAbstractClass", "LogWriterActivity")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void successAnnotatedInheritedMethods() {

        assertThat("DeniedAnnotatedActivityLifecycleMethodsWarningsSuppressed",
                "DuplicatePermissionsEquality")
                .compilesWithoutWarnings();
    }

    @Test
    public void successAnnotatedNonInheritedMethod() {
        assertThat("DeniedAnnotatedNonInheritedMethod", "LogWriterActivity")
                .compilesWithoutWarnings();
    }

    @Test
    public void successAnnotatedAbstractMethod() {
        assertThat("LogWriterActivity").compilesWithoutWarnings();
    }
}
