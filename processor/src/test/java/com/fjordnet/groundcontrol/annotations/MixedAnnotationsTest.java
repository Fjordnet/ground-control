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
 * Unit test cases involving some or all the Ground Control annotations.
 */
public class MixedAnnotationsTest extends GroundControlTest {

    private static final String ERROR_MULTIPLE_ANNOTATIONS = "can only be annotated by one of "
            + "NeedsPermission, OnPermissionDenied, or OnShowRationale";

    @Test
    public void errorNeedsPermissionOnPermissionDenied() {

        assertThat("MixedNeedsPermissionOnPermissionDenied")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_MULTIPLE_ANNOTATIONS);
    }

    @Test
    public void errorNeedsPermissionOnShowRationale() {

        assertThat("MixedNeedsPermissionOnShowRationale")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_MULTIPLE_ANNOTATIONS);
    }

    @Test
    public void errorOnPermissionDeniedOnShowRationale() {

        assertThat("MixedOnPermissionDeniedOnShowRationale")
                .failsToCompile()
                .withErrorCount(2)
                .withErrorContaining(ERROR_MULTIPLE_ANNOTATIONS);
    }

    @Test
    public void errorAllAnnotations() {

        assertThat("MixedAllAnnotations")
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining(ERROR_MULTIPLE_ANNOTATIONS);
    }
}
