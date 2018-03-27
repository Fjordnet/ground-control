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
 * Method annotation indicating the callback for showing a custom rationale.
 * If this annotation is used, there should be another method in the same class
 * making use of the {@link NeedsPermission} annotation with the same permission or set of
 * permissions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface OnShowRationale {

    /**
     * One or more runtime permissions for which to display a rationale for their use.
     * These permissions must be the same as those supplied in the {@link NeedsPermission}
     * annotation on a different method in the same class.
     *
     * @return one or more runtime permissions for which to display a rationale.
     */
    String[] value();

    /**
     * <p>
     * An optional attribute specifying whether to re-invoke the callback annotated by
     * this annotation upon re-creation of the activity or fragment containing it,
     * assuming the rationale was being displayed at the time of the restart.
     * </p><p>
     * This is useful if your app does not manually handle configuration changes
     * (i.e. the activity or fragment is destroyed and recreated), and the rationale
     * being displayed doesn't have its own mechanism for handling configuration changes.
     * </p><p>
     * Note that if this value is {@code false}, you will be responsible for saving the
     * {@code OnRationaleAcknowledgedListener} instance passed into the annotated method,
     * should a configuration change occur while the rationale is displayed, so that its
     * callback can be invoked when the rationale is finally acknowledged by the user.
     * </p><p>
     * The default value is {@code false}.
     * </p>
     *
     * @return {@code true} if Ground Control should re-invoke the callback annotated by
     * this annotation upon re-creation of the activity or fragment containing it,
     * assuming the rationale was being displayed at the time of the restart.
     */
    boolean handleRestarts() default false;

    /**
     * An optional attribute specifying whether to display compilation warnings
     * for this annotation. The default value is {@code true}.
     *
     * @return {@code true} if compilation warnings for this annotation should be displayed.
     */
    boolean usageWarnings() default true;
}
