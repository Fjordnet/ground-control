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
 * Method annotation indicating the callback for when a permission or set of permissions
 * is denied. If this annotation is used, there should be another method in the same class
 * making use of the {@link NeedsPermission} annotation with the same permission or set of
 * permissions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface OnPermissionDenied {

    /**
     * One or more runtime permissions that were denied by the user.
     * These permissions must be the same as those supplied in the {@link NeedsPermission}
     * annotation on a different method in the same class.
     *
     * @return one or more runtime permissions that were denied by the user.
     */
    String[] value();

    /**
     * An optional attribute specifying whether to display compilation warnings
     * for this annotation. The default value is {@code true}.
     *
     * @return {@code true} if compilation warnings for this annotation should be displayed.
     */
    boolean usageWarnings() default true;
}
