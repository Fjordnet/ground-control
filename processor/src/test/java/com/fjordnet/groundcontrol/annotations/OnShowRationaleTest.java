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
 * Unit test cases for the {@link OnShowRationale} annotation.
 */
public class OnShowRationaleTest extends GroundControlTest {

    protected static final String ERROR_DEFAULT_CUSTOM_RATIONALE =
            "conflicts with use of default rationale specified in "
            + "NeedsPermission annotating method";

    @Test
    public void successAllGroundControlAnnotations() {
        assertThat("AllGroundControlAnnotations").compilesWithoutWarnings();
    }

    @Test
    public void successMultiplePermissionsDifferentOrder() {
        assertThat("RationaleMultiplePermissionsDifferentOrder").compilesWithoutWarnings();
    }

    @Test
    public void warningUnmatchedPermissions() {

        assertThat("RationaleUnmatchedPermissions")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void warningUnmatchedPermissionsMultipleWarnings() {

        assertThat("RationaleUnmatchedPermissionsMultipleWarnings")
                .compilesWithoutError()
                .withWarningCount(4)
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void warningUnmatchedPermissionsNoGrantedCallback() {

        assertThat("RationaleUnmatchedPermissionsNoGrantedCallback")
                .compilesWithoutError()
                .withWarningCount(5)
                .withWarningContaining(WARNING_UNMATCHED_CALLBACK);
    }

    @Test
    public void successUnmatchedPermissions() {
        assertThat("RationaleUnmatchedPermissionsWarningsSuppressed").compilesWithoutWarnings();
    }

    @Test
    public void errorMultipleCallbacksSamePermissions() {

        assertThat("RationaleMultipleCallbacksSamePermissions")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining("Cannot have multiple OnShowRationale annotations "
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
                .withErrorContaining("@OnShowRationale cannot be private");
    }

    @Test
    public void errorStaticMethod() {

        assertThat("AnnotatedStaticMethod")
                .failsToCompile()
                .withErrorContaining("@OnShowRationale cannot be static");
    }

    @Test
    public void errorMethodNonVoidReturn() {

        assertThat("AnnotatedNonVoidMethod")
                .failsToCompile()
                .withErrorContaining("@OnShowRationale must have void return type");
    }

    @Test
    public void errorMethodIncorrectParameterList() {

        assertThat("RationaleIncorrectParameterList")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_RATIONALE_PARAMETERS);
    }


    @Test
    public void warningAnnotatedInheritedMethods() {

        assertThat("RationaleAnnotatedInheritedMethod", "DuplicatePermissionsEquality")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromInterface() {

        assertThat("RationaleAnnotatedInterfaceMethodImplementation", "LogReader")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void warningAnnotatedMethodFromAbstractClass() {

        assertThat("RationaleAnnotatedMethodFromAbstractClass", "LogWriterActivity")
                .compilesWithoutError()
                .withWarningCount(1)
                .withWarningContaining(WARNING_INHERITED_METHODS);
    }

    @Test
    public void successAnnotatedNonInheritedMethod() {
        assertThat("RationaleAnnotatedNonInheritedMethod", "LogWriterActivity")
                .compilesWithoutWarnings();
    }

    @Test
    public void successAnnotatedAbstractMethod() {
        assertThat("LogWriterActivity").compilesWithoutWarnings();
    }

    @Test
    public void errorDefaultAndCustomRationales() {

        assertThat("RationaleDefaultAndCustom")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_DEFAULT_CUSTOM_RATIONALE);
    }
}
