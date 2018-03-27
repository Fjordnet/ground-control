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
 * Unit test cases for validating parameters of methods annotated with {@link NeedsPermission}.
 */
public class GrantedCallbackParametersTest extends GroundControlTest {

    @Test
    public void successPrimitiveTypes() {
        assertThat("ParamsPrimitiveTypes").compilesWithoutWarnings();
    }

    @Test
    public void successStringType() {
        assertThat("ParamsStringType").compilesWithoutWarnings();
    }

    @Test
    public void successParcelableType() {
        assertThat("ParamsParcelableType", "AudioConfig").compilesWithoutWarnings();
    }

    @Test
    public void successPrimitiveArrayType() {
        assertThat("ParamsPrimitiveArrayTypes").compilesWithoutWarnings();
    }

    @Test
    public void successPrimitiveVarArgsTypes() {
        assertThat("ParamsPrimitiveVarArgsTypes").compilesWithoutWarnings();
    }

    @Test
    public void successStringArrayType() {
        assertThat("ParamsStringArrayType").compilesWithoutWarnings();
    }

    @Test
    public void successStringVarArgsType() {
        assertThat("ParamsStringVarArgsType").compilesWithoutWarnings();
    }

    @Test
    public void successParcelableArrayTypes() {
        assertThat("ParamsParcelableArrayTypes", "User", "AudioConfig").compilesWithoutWarnings();
    }

    @Test
    public void successParcelableVarArgsType() {
        assertThat("ParamsParcelableVarArgsType", "User").compilesWithoutWarnings();
    }

    @Test
    public void successBoxedPrimitiveTypes() {
        assertThat("ParamsBoxedPrimitiveTypes").compilesWithoutWarnings();
    }

    @Test
    public void warningUnsupportedDeclaredTypes() {

        assertThat("ParamsUnsupportedDeclaredTypes")
                .compilesWithoutError()
                .withWarningCount(4)
                .withWarningContaining(WARNING_UNSUPPORTED_PARAM_TYPE);
    }

    @Test
    public void successUnsupportedDeclaredTypes() {
        assertThat("ParamsUnsupportedDeclaredTypesWarningsSuppressed").compilesWithoutWarnings();
    }

    @Test
    public void warningBoxedPrimitiveArrayTypes() {

        assertThat("ParamsBoxedPrimitiveArrayTypes")
                .compilesWithoutError()
                .withWarningCount(7)
                .withWarningContaining(WARNING_UNSUPPORTED_PARAM_TYPE);
    }

    @Test
    public void successBoxedPrimitiveArrayTypes() {
        assertThat("ParamsBoxedPrimitiveArrayTypesWarningsSuppressed").compilesWithoutWarnings();
    }
}
