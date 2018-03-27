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

import com.google.common.truth.Truth;
import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

/**
 * Provides helper methods for subclasses.
 */
public abstract class GroundControlTest {

    protected static final String WARNING_UNMATCHED_CALLBACK =
            "do not match permissions specified in any";
    protected static final String WARNING_INHERITED_METHODS =
            "Annotating methods that override a parent implementation with";
    protected static final String ERROR_DENIED_PARAMETERS =
            "@OnPermissionDenied must not contain any parameters";
    protected static final String ERROR_RATIONALE_PARAMETERS =
            "@OnShowRationale must have exactly 1 parameter of type";
    protected static final String WARNING_UNSUPPORTED_PARAM_TYPE =
            "ensure this callback can be invoked with the appropriate arguments after a "
            + "configuration change";

    protected CompileTester assertThat(String... testClassNames) {

        List<JavaFileObject> testFiles = new ArrayList<>();
        URL source;

        for (String testClassName : testClassNames) {
            source = getClass().getResource(
                    String.format("/com/fjordnet/sample/groundcontrol/%s.java", testClassName));
            Truth.assertThat(source).isNotNull();
            testFiles.add(JavaFileObjects.forResource(source));
        }

        return Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(testFiles)
                .processedWith(new GroundControlProcessor());
    }
}
