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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation indicating the necessary runtime permissions that method requires.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface NeedsPermission {

    /**
     * One or more runtime permissions to request from the Android framework.
     *
     * @return one or more runtime permissions that the annotated method requires to run.
     */
    String[] value();

    /**
     * An optional attribute specifying the string resource ID for rationale text.
     * The default value is 0.
     *
     * @return the string resource ID for rationale text.
     */
    int rationaleResourceId() default 0;

    /**
     * An optional attribute specifying whether to display compilation warnings
     * for this annotation. The default value is {@code true}.
     *
     * @return {@code true} if compilation warnings for this annotation should be displayed.
     */
    boolean usageWarnings() default true;
}