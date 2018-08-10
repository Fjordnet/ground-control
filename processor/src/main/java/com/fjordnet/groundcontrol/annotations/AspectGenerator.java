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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static com.fjordnet.groundcontrol.annotations.ProcessorUtils.findMethod;
import static com.fjordnet.groundcontrol.annotations.ProcessorUtils.getParentClass;
import static com.fjordnet.groundcontrol.annotations.ProcessorUtils.join;
import static com.fjordnet.groundcontrol.annotations.ProcessorUtils.stringifyParameters;
import static java.lang.String.format;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.VOID;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Generates aspect files for Ground Control.
 * The generated aspects contain the logic for handling runtime permissions
 * via Ground Control annotations.
 */
public class AspectGenerator {

    // Fully qualified class names used during aspect generation.
    private static final String ACTIVITY = "android.app.Activity";
    private static final String ALERT_DIALOG = "android.app.AlertDialog";
    private static final String BOOLEAN_TYPE = "java.lang.Boolean";
    private static final String BUNDLE = "android.os.Bundle";
    private static final String BYTE_TYPE = "java.lang.Byte";
    private static final String DIALOG_INTERFACE = "android.content.DialogInterface";
    private static final String DIALOG_ONCLICK_LISTENER = DIALOG_INTERFACE + ".OnClickListener";
    private static final String DOUBLE_TYPE = "java.lang.Double";
    private static final String FLOAT_TYPE = "java.lang.Float";
    private static final String FRAGMENT = "android.app.Fragment";
    private static final String INTEGER_TYPE = "java.lang.Integer";
    private static final String LONG_TYPE = "java.lang.Long";
    private static final String PARCEL = "android.os.Parcel";
    private static final String PARCELABLE = "android.os.Parcelable";
    private static final String PARCELABLE_CREATOR = "android.os.Parcelable.Creator";
    private static final String SHORT_TYPE = "java.lang.Short";
    private static final String STRING = "java.lang.String";
    private static final String SUPPORT_FRAGMENT = "android.support.v4.app.Fragment";

    private static final String GROUND_CONTROL = "com.fjordnet.groundcontrol.GroundControl";
    private static final String RATIONALE = "com.fjordnet.groundcontrol.Rationale";
    private static final String ON_RATIONALE_ACKNOWLEDGED_LISTENER
            = "com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener";

    // Android API constants.
    private static final String METHOD_GET_ACTIVITY = "getActivity";
    private static final String METHOD_ON_REQUEST_PERMISSIONS_RESULT = "onRequestPermissionsResult";
    private static final String METHOD_REQUEST_PERMISSIONS = "requestPermissions";
    private static final String CONSTANT_PERMISSION_GRANTED
            = "android.content.pm.PackageManager.PERMISSION_GRANTED";

    private static final String CALLBACK_PARAM_REQUEST_CODE = "requestCode";
    private static final String CALLBACK_PARAM_PERMISSIONS = "permissions";
    private static final String CALLBACK_PARAM_GRANT_RESULTS = "grantResults";

    // PermissionCallback interface and declared methods.
    private static final String INTERFACE_PERMISSION_CALLBACK = "PermissionCallback";
    private static final String METHOD_GET_REQUEST_CODE = "getRequestCode";
    private static final String METHOD_GRANT = "grant";
    private static final String METHOD_DENY = "deny";
    private static final String METHOD_SET_TARGET_INSTANCE = "setTargetInstance";

    // Member variables for the intermediary class.
    private static final String FIELD_PENDING_PERMISSION = "__groundControlPendingPermission";
    private static final String FIELD_CURRENT_REQUEST_CODE = "__groundControlCurrentRequestCode";
    private static final String FIELD_RATIONALE_PERMISSIONS = "__groundControlRationalePermissions";
    private static final String FIELD_RATIONALE_DIALOG = "__groundControlRationaleDialog";

    // Helper methods in intermediary class.
    private static final String METHOD_WAS_GRANTED = "wasGranted";
    private static final String METHOD_RESTART_RATIONALE = "__groundControlRestartRationale";
    private static final String METHOD_SHOW_DEFAULT_RATIONALE_DIALOG
            = "__groundControlShowDefaultRationaleDialog";
    private static final String METHOD_CAN_HANDLE_REQUEST = "canHandleRequest";

    private static final String VAR_TARGET_INSTANCE = "targetInstance";
    private static final String VAR_PERMISSIONS_ARRAY = "permissionsArray";
    private static final String VAR_RATIONALE_ACK_LISTENER = "ackListener";
    private static final String VAR_REQUEST_CODE = "requestCode";

    // Ground Control API constants.
    private static final String METHOD_FILTER_GRANTED_PERMISSIONS = "filterGrantedPermissions";
    private static final String METHOD_CREATE_RATIONALE_DIALOG = "createPermissionRationaleDialog";
    private static final String METHOD_ON_RATIONALE_ACKNOWLEDGED = "onRationaleAcknowledged";

    // Keys for saving state.
    private static final String STATE_PENDING_PERMISSION
            = "__groundControl.state.pendingPermission";
    private static final String STATE_CURRENT_REQUEST_CODE
            = "__groundControl.state.currentRequestCode";
    private static final String STATE_RATIONALE_PERMISSIONS
            = "__groundControl.state.rationalePermissions";

    private final Elements elements;
    private final Types types;
    private final Filer filer;
    private final Messager messager;

    private int nextRequestCode;

    public AspectGenerator(Elements elements, Types types, Filer filer, Messager messager) {
        this.elements = elements;
        this.types = types;
        this.filer = filer;
        this.messager = messager;

        // For permission request code, use some number that will likely not conflict
        // with other developer-defined request codes.
        nextRequestCode = 0x1357;
    }

    public void generateAspectFor(Element classElement)
            throws IOException {

        // Error checking on class element.
        if (!isValidContainingClass(classElement)) {
            return;
        }

        String packageName = elements.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String aspectName = className + "GroundControlAspect";

        FileObject aspectFile = filer.createResource(StandardLocation.SOURCE_OUTPUT,
                packageName, format("%s.aj", aspectName));

        Writer writer = aspectFile.openWriter();

        // Package.
        writer.write(format("package %s;\n\n", packageName));

        // Aspect declaration.
        writer.write(format("public aspect %s {\n\n", aspectName));

        // PermissionCallback interface, for handling results from permission requests.
        writePermissionCallbackInterface(classElement, writer, "\t");

        // Find all methods annotated with Ground Control annotations and group them.
        List<GroupedAnnotatedMethodSet> callbackSets = findAllAnnotatedCallbacks(classElement);

        // Intermediate class.
        writeGroundControlledClass(classElement, callbackSets, writer, "\t");

        // Declare parents.
        writer.write(format("\tdeclare parents: %s extends %s;\n\n",
                classElement.asType(), getIntermediaryClassName(classElement)));

        // Generate advice for each callback set.
        for (GroupedAnnotatedMethodSet callbackSet : callbackSets) {
            writeAdviceFor(callbackSet, writer, "\t");
        }

        // Display warnings if there are any OnPermissionDenied or OnShowRationale callbacks
        // that don't match permission requirement methods.
        printUnmatchedAnnotationCallbackWarnings(callbackSets);

        // End aspect.
        writer.write("}\n");

        writer.close();
    }

    private void writePermissionCallbackInterface(Element classElement, Writer writer, String tabs)
            throws IOException {

        writer.write(format("%1$sinterface %2$s extends %6$s {\n"
                + "%1$s\tint %3$s();\n"
                + "%1$s\tvoid %4$s();\n"
                + "%1$s\tvoid %5$s();\n"
                + "%1$s\tvoid %7$s(%8$s %9$s);\n"
                + "%1$s}\n\n",

                tabs, // 1
                INTERFACE_PERMISSION_CALLBACK, // 2
                METHOD_GET_REQUEST_CODE, // 3
                METHOD_GRANT, // 4
                METHOD_DENY, // 5
                PARCELABLE, // 6
                METHOD_SET_TARGET_INSTANCE, // 7
                getIntermediaryClassName(classElement), // 8
                VAR_TARGET_INSTANCE // 9
        ));
    }

    private void writeGroundControlledClass(Element classElement,
            List<GroupedAnnotatedMethodSet> callbackSets,
            Writer writer,
            String tabs)
            throws IOException {

        Element parentClass = getParentClass(classElement, types);

        writer.write(format("%1$spublic static abstract class %2$s%3$s {\n\n",
                tabs, // 1
                getIntermediaryClassName(classElement), // 2
                null == parentClass // 3
                        ? ""
                        : format(" extends %s", parentClass.asType())));

        writeFields(writer, tabs + "\t");

        // Override of onRequestPermissionsResult, which will invoke the appropriate callback
        // from the pending permission handler.
        writeOnRequestPermissionsResult(writer, tabs + "\t");

        // Override life cycle methods to manage state across configuration changes.
        writeOverrideLifeCycleMethods(classElement, writer, tabs + "\t");

        // Methods replicating the annotated methods in the target class,
        // so they're accessible from the advice.
        writeReplicatedCallbackMethods(callbackSets, writer, tabs + "\t");

        // Primary method for showing the appropriate rationale
        // for the specified permissions request.
        writeRestartRationaleMethod(callbackSets, classElement, writer, tabs + "\t");

        // Enumerated helper methods for showing specific rationales.
        // Used by the primary show rationale method.
        writeShowDefaultRationaleHelperMethods(callbackSets, classElement, writer, tabs + "\t");

        // Helper method for showing a simple rationale dialog.
        writeShowDefaultRationaleDialog(classElement, writer, tabs + "\t");

        // Helper method for determining whether a specific request code
        // can be handled by this intermediary class.
        writeCanHandleRequest(callbackSets, writer, tabs + "\t");

        // End class.
        writer.write(tabs + "}\n\n");
    }

    private void writeFields(Writer writer, String tabs) throws IOException {

        // Pending permission, which is the callback handler for when the permission is granted.
        writer.write(format("%3$sprotected %1$s %2$s;\n\n",
                INTERFACE_PERMISSION_CALLBACK, FIELD_PENDING_PERMISSION, tabs));

        // Request code for the current permissions request, if any.
        // Together with the permissions array, this determines if a rationale needs to be
        // shown again after a configuration change.
        writer.write(format("%2$sprotected int %1$s;\n\n",
                FIELD_CURRENT_REQUEST_CODE, tabs));

        // Permissions for the currently displaying rationale, if any. May be null.
        // Together with the current request code, this determines if a rationale needs to be
        // shown again after a configuration change.
        writer.write(format("%2$sprotected String[] %1$s;\n\n",
                FIELD_RATIONALE_PERMISSIONS, tabs));

        // Currently displaying rationale dialog. May be null.
        writer.write(format("%3$sprivate %1$s %2$s;\n\n",
                ALERT_DIALOG, FIELD_RATIONALE_DIALOG, tabs));
    }

    private void writeOnRequestPermissionsResult(Writer writer, String tabs) throws IOException {

        // Method implementation.
        writer.write(format("%1$s@Override public void %2$s("
                + "int %3$s, String[] %4$s, int[] %5$s) {\n"

                // Check if this request can be handled by this class.
                // If not, call the super implementation and return.
                + "%1$s\tif (!%11$s(%3$s)) {\n"
                + "%1$s\t\tsuper.%2$s(%3$s, %4$s, %5$s);\n"
                + "%1$s\t\treturn;\n"
                + "%1$s\t}\n\n"

                // Check that there's a handler for the pending permission.
                + "%1$s\tif (null == %6$s || %3$s != %6$s.%7$s()) { return; }\n"

                // Invoke the appropriate callback method on the pending permission handler.
                + "%1$s\tif (%8$s(%4$s, %5$s)) { %6$s.%9$s(); } else { %6$s.%10$s(); }\n"

                // Clear pending permission and request code.
                + "%1$s\t%6$s = null;\n"
                + "%1$s\t%12$s = -1;\n"

                + "%1$s}\n\n",

                tabs, // 1
                METHOD_ON_REQUEST_PERMISSIONS_RESULT, // 2
                CALLBACK_PARAM_REQUEST_CODE, // 3
                CALLBACK_PARAM_PERMISSIONS, // 4
                CALLBACK_PARAM_GRANT_RESULTS, // 5
                FIELD_PENDING_PERMISSION, // 6
                METHOD_GET_REQUEST_CODE, // 7
                METHOD_WAS_GRANTED, // 8
                METHOD_GRANT, // 9
                METHOD_DENY, // 10
                METHOD_CAN_HANDLE_REQUEST, // 11
                FIELD_CURRENT_REQUEST_CODE // 12
        ));

        // Write helper method for reading grant results.
        writeWasGranted(writer, tabs);
    }

    private void writeWasGranted(Writer writer, String tabs) throws IOException {
        writer.write(format("%1$sprivate static boolean %2$s(String[] %3$s, int[] %4$s) {\n"
                + "%1$s\tif (null == %3$s || 0 >= %3$s.length) { return false; }\n"
                + "%1$s\tfor (int result : %4$s) {\n"
                + "%1$s\t\t if (%5$s != result) { return false; }\n"
                + "%1$s\t}\n"
                + "%1$s\treturn true;\n%1$s}\n\n",

                tabs,
                METHOD_WAS_GRANTED,
                CALLBACK_PARAM_PERMISSIONS,
                CALLBACK_PARAM_GRANT_RESULTS,
                CONSTANT_PERMISSION_GRANTED
        ));
    }

    private void writeOverrideLifeCycleMethods(Element classElement, Writer writer, String tabs)
            throws IOException {

        // Restore state and re-display rationale, if previously shown.
        writeOverrideCreationMethod(classElement, writer, tabs);

        // Dismiss the currently displayed rationale dialog in onDestroy, if applicable.
        writeOverrideDestroyMethod(classElement, writer, tabs);

        // Save state of currently displayed rationale and permission callback.
        writeOverrideSaveStateMethod(classElement, writer, tabs);
    }

    private void writeOverrideCreationMethod(Element classElement, Writer writer, String tabs)
            throws IOException {

        // Find the appropriate creation method to override.
        final ExecutableElement creationMethod = findMethod(
                isActivityClass(classElement) ? "onPostCreate" : "onActivityCreated",
                types.getNoType(VOID),
                classElement,
                elements,
                types,
                elements.getTypeElement(BUNDLE).asType());

        if (null == creationMethod) {
            return;
        }

        writer.write(format("%1$s@Override %13$s void %9$s(%2$s %3$s) {\n"
                + "%1$s\tsuper.%9$s(%3$s);\n"
                + "%1$s\tif (null == %3$s) { return; }\n\n"

                // Do nothing if the request code cannot be handled by this class.
                + "%1$s\tint %15$s = %3$s.getInt(\"%5$s\");\n"
                + "%1$s\tif (!%14$s(%15$s)) { return; }\n"

                // Restore request code.
                + "%1$s\t%4$s = %15$s;\n\n"

                // Restore pending permission callback.
                + "%1$s\t%10$s = %3$s.getParcelable(\"%11$s\");\n"
                + "%1$s\tif (null != %10$s) { %10$s.%12$s(this); }\n\n"

                // Restore permissions for rationale and request code.
                + "%1$s\t%6$s = %3$s.getStringArray(\"%7$s\");\n"

                // If rationale was previously displaying, show it again.
                + "%1$s\tif (null != %6$s) {\n"
                + "%1$s\t\t%8$s(%6$s, %4$s);\n"
                + "%1$s\t}\n"

                + "%1$s}\n\n",

                tabs, // 1
                BUNDLE, // 2
                "savedInstanceState", // 3
                FIELD_CURRENT_REQUEST_CODE, // 4
                STATE_CURRENT_REQUEST_CODE, // 5
                FIELD_RATIONALE_PERMISSIONS, // 6
                STATE_RATIONALE_PERMISSIONS, // 7
                METHOD_RESTART_RATIONALE, // 8
                creationMethod.getSimpleName(), // 9
                FIELD_PENDING_PERMISSION, // 10
                STATE_PENDING_PERMISSION, // 11
                METHOD_SET_TARGET_INSTANCE, // 12
                join(creationMethod.getModifiers(), " "), // 13
                METHOD_CAN_HANDLE_REQUEST, // 14
                "requestCode" // 15
        ));
    }

    private void writeOverrideDestroyMethod(Element classElement, Writer writer, String tabs)
            throws IOException {

        // Find the appropriate destroy method to override.
        ExecutableElement destroyMethod = findMethod("onDestroy", types.getNoType(VOID),
                classElement, elements, types);
        if (null == destroyMethod) {
            return;
        }

        writer.write(format("%1$s@Override %3$s void %4$s() {\n"
                + "%1$s\tsuper.%4$s();\n"
                + "%1$s\tif (null != %2$s && %2$s.isShowing()) {\n"
                + "%1$s\t\t%2$s.dismiss();\n"
                + "%1$s\t\t%2$s = null;\n"
                + "%1$s\t}\n"
                + "%1$s}\n\n",

                tabs, // 1
                FIELD_RATIONALE_DIALOG, // 2
                join(destroyMethod.getModifiers(), " "), // 3
                destroyMethod.getSimpleName() // 4
        ));
    }

    private void writeOverrideSaveStateMethod(Element classElement, Writer writer, String tabs)
            throws IOException {

        // Find the appropriate creation method to override.
        ExecutableElement saveMethod = findMethod("onSaveInstanceState",
                types.getNoType(VOID), classElement, elements, types,
                elements.getTypeElement(BUNDLE).asType());
        if (null == saveMethod) {
            return;
        }

        writer.write(format("%1$s@Override %10$s void %11$s(%2$s %3$s) {\n"
                + "%1$s\tsuper.%11$s(%3$s);\n"
                + "%1$s\tif (!%12$s(%5$s)) { return; }\n\n"
                + "%1$s\t%3$s.putParcelable(\"%8$s\", %9$s);\n"
                + "%1$s\t%3$s.putInt(\"%4$s\", %5$s);\n"
                + "%1$s\t%3$s.putStringArray(\"%6$s\", %7$s);\n"
                + "%1$s}\n\n",

                tabs, // 1
                BUNDLE, // 2
                "outState", // 3
                STATE_CURRENT_REQUEST_CODE, // 4
                FIELD_CURRENT_REQUEST_CODE, // 5
                STATE_RATIONALE_PERMISSIONS, // 6
                FIELD_RATIONALE_PERMISSIONS, // 7
                STATE_PENDING_PERMISSION, // 8
                FIELD_PENDING_PERMISSION, // 9
                join(saveMethod.getModifiers(), " "), // 10
                saveMethod.getSimpleName(), // 11
                METHOD_CAN_HANDLE_REQUEST // 12
        ));
    }

    private void writeReplicatedCallbackMethods(List<GroupedAnnotatedMethodSet> callbackSets,
            Writer writer,
            String tabs) throws IOException {

        // Iterate through callback sets, replicating signatures of methods
        // annotated with any of the Ground Control annotations.
        // Keep track of what's written so methods don't get written twice,
        // if they happen to be specified in multiple callback sets.

        Set<ExecutableElement> writtenMethods = new HashSet<>();

        for (GroupedAnnotatedMethodSet callbackSet : callbackSets) {

            // Permission granted callback.
            if (null != callbackSet.permissionGrantedCallback
                    && !writtenMethods.contains(callbackSet.permissionGrantedCallback)) {

                writeReplicatedCallbackMethod(callbackSet.permissionGrantedCallback, writer, tabs);
                writtenMethods.add(callbackSet.permissionGrantedCallback);
            }

            // Permission denied callback.
            if (null != callbackSet.permissionDeniedCallback
                    && !writtenMethods.contains(callbackSet.permissionDeniedCallback)) {

                writeReplicatedCallbackMethod(callbackSet.permissionDeniedCallback, writer, tabs);
                writtenMethods.add(callbackSet.permissionDeniedCallback);
            }

            // Rationale callback.
            if (null != callbackSet.rationaleCallback
                    && !writtenMethods.contains(callbackSet.rationaleCallback)) {

                writeReplicatedCallbackMethod(callbackSet.rationaleCallback, writer, tabs);
                writtenMethods.add(callbackSet.rationaleCallback);
            }
        }
    }

    private void writeReplicatedCallbackMethod(ExecutableElement callback,
            Writer writer,
            String tabs) throws IOException {

        if (null == callback) {
            return;
        }

        Set<Modifier> modifiers = new HashSet<>(callback.getModifiers());
        modifiers.add(ABSTRACT);
        modifiers.remove(FINAL);

        writer.write(format("%1$s%2$s void %3$s(%4$s);\n\n",
                tabs, // 1
                join(modifiers, " "), // 2
                callback.getSimpleName(), // 3
                stringifyParameters(callback) // 4
        ));
    }

    private void writeRestartRationaleMethod(
            List<GroupedAnnotatedMethodSet> callbackSets,
            Element classElement,
            Writer writer,
            String tabs)
            throws IOException {

        // Method signature.
        writer.write(format("%1$sprotected void %2$s(final String[] %3$s, final int %4$s) {\n",
                tabs, METHOD_RESTART_RATIONALE, VAR_PERMISSIONS_ARRAY, VAR_REQUEST_CODE));

        // Activity instance.
        String varActivity = "activity";
        writer.write(format("%1$s\tfinal %2$s %3$s = %4$s;\n",
                tabs, ACTIVITY, varActivity,
                isActivityClass(classElement) ? "this" : METHOD_GET_ACTIVITY + "()"));

        // Rationale acknowledged listener.
        writer.write(format(
                "%1$s\t%2$s %3$s = new %2$s(%9$s, %10$s) {\n"
                + "%1$s\t\t@Override public void %4$s(%12$s %13$s) {\n"
                + "%1$s\t\t\t%5$s(%6$s.%7$s(%8$s, %9$s), %10$s);\n"
                + "%1$s\t\t\t%11$s = null;\n"
                + "%1$s\t\t}\n"
                + "%1$s\t};\n\n",

                tabs, // 1
                ON_RATIONALE_ACKNOWLEDGED_LISTENER, // 2
                VAR_RATIONALE_ACK_LISTENER, // 3
                METHOD_ON_RATIONALE_ACKNOWLEDGED, // 4
                METHOD_REQUEST_PERMISSIONS, // 5
                GROUND_CONTROL, // 6
                METHOD_FILTER_GRANTED_PERMISSIONS, // 7
                varActivity, // 8
                VAR_PERMISSIONS_ARRAY, // 9
                VAR_REQUEST_CODE, // 10
                FIELD_RATIONALE_PERMISSIONS, // 11
                getTopLevelType(classElement), // 12
                VAR_TARGET_INSTANCE // 13
        ));

        // Switch statement for every request code for which a rationale might be shown.
        StringBuilder builder = new StringBuilder();

        // Iterate through each callback set and call the appropriate rationale method,
        // if appropriate.
        String showRationaleMethodName;
        for (GroupedAnnotatedMethodSet callbackSet : callbackSets) {

            showRationaleMethodName = callbackSet.getShowRationaleMethodName();
            if (null == showRationaleMethodName) {
                continue;
            }

            // Check if Ground Control should handle restarts,
            // i.e. display the rationale on activity / fragment restart.
            if (!callbackSet.handleRestarts()) {
                continue;
            }

            builder.append(format(Locale.ENGLISH,
                    "%1$s\t\tcase %2$d:\n"
                    + "%1$s\t\t\t%3$s(%4$s); break;\n",

                    tabs, // 1
                    callbackSet.requestCode, // 2
                    showRationaleMethodName, // 3
                    VAR_RATIONALE_ACK_LISTENER // 4
            ));
        }

        String switchBody = builder.toString();
        if (!switchBody.isEmpty()) {
            writer.write(format("%1$s\tswitch (%2$s) {\n"
                    + "%3$s"
                    + "%1$s\t}\n",
                    tabs, // 1
                    VAR_REQUEST_CODE, // 2
                    switchBody // 3
            ));
        }

        // End method.
        writer.write(tabs + "}\n\n");
    }

    private void writeShowDefaultRationaleHelperMethods(
            List<GroupedAnnotatedMethodSet> callbackSets,
            Element classElement,
            Writer writer,
            String tabs)
            throws IOException {

        // Iterate through callback sets, creating helper methods for displaying
        // the default rationale dialog when a rationale resource ID is supplied,
        // assuming no custom rationale callback is present.

        String rationaleCallbackName;
        for (GroupedAnnotatedMethodSet callbackSet : callbackSets) {

            rationaleCallbackName = callbackSet.getShowRationaleMethodName();
            if (null != callbackSet.rationaleCallback || null == rationaleCallbackName) {
                // Custom rationale callback was specified, or
                // no rationale resource ID was specified in the NeedsPermission annotation.
                continue;
            }

            // Write a helper method for displaying this rationale in a simple dialog.
            writer.write(format(Locale.ENGLISH,
                    "%1$sprotected void %2$s(%3$s %4$s) {\n"
                    + "%1$s\t%5$s(new %6$s(%7$s.getString(%8$d), %7$s), %4$s);\n"
                    + "%1$s}\n\n",

                    tabs, // 1
                    rationaleCallbackName, // 2
                    ON_RATIONALE_ACKNOWLEDGED_LISTENER, // 3
                    VAR_RATIONALE_ACK_LISTENER, // 4
                    METHOD_SHOW_DEFAULT_RATIONALE_DIALOG, // 5
                    RATIONALE, // 6
                    isActivityClass(classElement) ? "this" : METHOD_GET_ACTIVITY + "()", // 7
                    callbackSet.permissionGrantedCallback.getAnnotation(NeedsPermission.class) // 8
                            .rationaleResourceId()
            ));
        }
    }

    private void writeShowDefaultRationaleDialog(Element classElement, Writer writer, String tabs)
            throws IOException {

        writer.write(format("%1$sprivate void %2$s(final %3$s %4$s,\n"
                + "%1$s\t\tfinal %5$s %6$s) {\n"

                + "%1$s\t%12$s = %8$s.%9$s(%7$s, %4$s,\n"
                + "%1$s\t\tnew %10$s() {\n"
                + "%1$s\t\t\t@Override public void onClick(%11$s dialog, int buttonId) {\n"
                + "%1$s\t\t\t\t%6$s.%13$s(%14$s);\n"
                + "%1$s\t\t\t}\n"
                + "%1$s\t\t});\n"
                + "%1$s\t%12$s.show();\n"

                + "%1$s}\n\n",

                tabs, // 1
                METHOD_SHOW_DEFAULT_RATIONALE_DIALOG, // 2
                RATIONALE, // 3
                "rationale", // 4
                ON_RATIONALE_ACKNOWLEDGED_LISTENER, // 5
                VAR_RATIONALE_ACK_LISTENER, // 6
                isActivityClass(classElement) ? "this" : METHOD_GET_ACTIVITY + "()", // 7
                GROUND_CONTROL, // 8
                METHOD_CREATE_RATIONALE_DIALOG, // 9
                DIALOG_ONCLICK_LISTENER, // 10
                DIALOG_INTERFACE, // 11
                FIELD_RATIONALE_DIALOG, // 12
                METHOD_ON_RATIONALE_ACKNOWLEDGED, // 13
                getIntermediaryClassName(classElement) + ".this" // 14
        ));
    }

    private void writeCanHandleRequest(List<GroupedAnnotatedMethodSet> callbackSets,
            Writer writer,
            String tabs) throws IOException {

        String varRequestCode = "requestCode";
        writer.write(format("%1$sprivate static boolean %2$s(int %3$s) {\n"
                + "%1$s\tswitch (%3$s) {\n"
                + "%4$s\n"
                + "%1$s\t}\n"
                + "%1$s\treturn false;\n"
                + "%1$s}\n\n",

                tabs, // 1
                METHOD_CAN_HANDLE_REQUEST, // 2
                varRequestCode, // 3
                join(callbackSets, "\n", // 4
                        set -> format(Locale.US, "%1$s\t\tcase %2$d: return %3$b;",
                                tabs, set.requestCode, null != set.permissionGrantedCallback))
        ));
    }

    private void writeAdviceFor(GroupedAnnotatedMethodSet callbackSet,
            Writer writer,
            String tabs) throws IOException {

        ExecutableElement grantedCallback = callbackSet.permissionGrantedCallback;
        if (null == grantedCallback) {
            return;
        }

        final String targetInstanceType = grantedCallback.getEnclosingElement().asType().toString();
        final String methodName = grantedCallback.getSimpleName().toString();
        final String parameterList = stringifyParameters(grantedCallback);
        final String permissionCallbackClass = getCallbackClassNameFor(grantedCallback);

        final List<? extends VariableElement> paramElements
                = ((ExecutableElement) grantedCallback).getParameters();
        final String methodArgs = join(paramElements, ", ",
                elem -> elem.getSimpleName().toString());

        // Join point definition.
        String joinPoint = format("call(void %1$s.%2$s(%3$s))",
                targetInstanceType, // 1
                methodName, // 2
                join(paramElements, ", ", elem -> elem.asType().toString()) // 3
        );

        // If the last parameter is a varargs (e.g. String...), it will need to be
        // explicitly specified as such in the join point definition.
        final VariableElement lastParam = paramElements.isEmpty()
                ? null
                : paramElements.get(paramElements.size() - 1);
        if (null != lastParam && ARRAY == lastParam.asType().getKind()) {
            joinPoint = format(
                    "(%1$s || call(void %2$s.%3$s(%4$s, %5$s...)))",
                    joinPoint, // 1
                    targetInstanceType, // 2
                    methodName, // 3
                    join(paramElements.subList(0, paramElements.size() - 1), // 4
                            ", ",
                            param -> param.asType().toString()),
                    ((ArrayType) lastParam.asType()).getComponentType() // 5
            );
        }

        final Set<String> permissionsSet = getPermissionsSet(grantedCallback);

        // Advice around calls to the annotated method.
        writer.write(format("%1$svoid around(final %2$s %3$s%6$s%5$s):\n"
                + "%1$s\t%4$s && args(%7$s) && target(%3$s) "
                + "&& !within(%9$s) {\n\n"

                // Permissions array as a local variable.
                + "%1$s\tfinal String[] %11$s = new String[] {%12$s};\n"

                // If the permissions were already granted,
                // proceed with the method execution.
                + "%1$s\tif (%13$s.hasPermissions(%3$s%10$s, %11$s)) {\n"
                + "%1$s\t\tproceed(%3$s%6$s%7$s);\n"
                + "%1$s\t\treturn;\n"
                + "%1$s\t}\n\n"

                // Set the pending permission callback handler.
                + "%1$s\t%3$s.%8$s = new %9$s(%3$s%6$s%7$s);\n"

                // Set request code.
                + "%1$s\t%3$s.%14$s = %15$d;\n\n"

                // Handle showing of rationale, if applicable.
                + generateRationaleCode(callbackSet, tabs + "\t")

                + "%1$s}\n\n",

                tabs, // 1
                targetInstanceType, // 2
                VAR_TARGET_INSTANCE, // 3
                joinPoint, // 4
                parameterList, // 5
                paramElements.isEmpty() ? "" : ", ", // 6
                methodArgs, // 7
                FIELD_PENDING_PERMISSION, // 8
                permissionCallbackClass, // 9
                isActivityClass(grantedCallback.getEnclosingElement()) // 10
                        ? ""
                        : "." + METHOD_GET_ACTIVITY + "()",
                VAR_PERMISSIONS_ARRAY, // 11
                join(permissionsSet, ", ", permission -> format("\"%s\"", permission)), // 12
                GROUND_CONTROL, // 13
                FIELD_CURRENT_REQUEST_CODE, // 14
                callbackSet.requestCode // 15
        ));

        // Permission callback handler implementation for this method.
        writePermissionCallback(callbackSet, writer, tabs);
    }

    private void writePermissionCallback(GroupedAnnotatedMethodSet callbackSet,
            Writer writer,
            String tabs)
            throws IOException {

        final ExecutableElement grantedCallback = callbackSet.permissionGrantedCallback;
        final String callbackClassName = getCallbackClassNameFor(grantedCallback);
        final Element classElement = grantedCallback.getEnclosingElement();
        final String intermediaryClassName = getIntermediaryClassName(classElement);

        // Class declaration.
        writer.write(format("%1$sstatic class %2$s implements %3$s {\n\n",
                tabs, callbackClassName, INTERFACE_PERMISSION_CALLBACK));

        // Reference back to class instance as the intermediary type.
        writer.write(format("%1$s\tprivate %2$s %3$s;\n",
                tabs, intermediaryClassName, VAR_TARGET_INSTANCE));

        // Cached parameters as fields.
        final List<? extends VariableElement> parameters = grantedCallback.getParameters();
        for (VariableElement param : parameters) {
            writer.write(format("%1$s\tprivate %2$s %3$s;\n",
                    tabs, param.asType(), param.getSimpleName()));
        }

        final String parametersList = join(parameters, ", ",
                param -> format("%s %s", param.asType(), param.getSimpleName()));

        // Constructor declaration.
        writer.write(format("\n%1$s\tpublic %2$s(%3$s %4$s%6$s%5$s) {\n",
                tabs, // 1
                callbackClassName, // 2
                intermediaryClassName, // 3
                VAR_TARGET_INSTANCE, // 4
                parametersList, // 5
                parameters.isEmpty() ? "" : ", " // 6
        ));

        // Initialize fields.
        writer.write(format("%1$s\t\tthis.%2$s = %2$s;\n", tabs, VAR_TARGET_INSTANCE));
        for (VariableElement param : parameters) {
            writer.write(format("%1$s\t\tthis.%2$s = %2$s;\n", tabs, param.getSimpleName()));
        }

        // End constructor.
        writer.write(format("%1$s\t}\n\n", tabs));

        // Implementation of getRequestCode.
        writer.write(format(Locale.ENGLISH,
                "%1$s\t@Override public int %2$s() { return %3$d; }\n\n",
                tabs, METHOD_GET_REQUEST_CODE, callbackSet.requestCode));

        // Implementation of grant.
        writer.write(format("%1$s\t@Override public void %2$s() { %3$s.%4$s(%5$s); }\n\n",
                tabs, METHOD_GRANT,
                VAR_TARGET_INSTANCE,
                grantedCallback.getSimpleName(),
                join(parameters, ", ", param -> param.getSimpleName().toString())));

        // Implementation of deny.
        String callbackInvocation = null == callbackSet.permissionDeniedCallback
                ? ""
                : format("%1$s.%2$s();",
                        VAR_TARGET_INSTANCE,
                        callbackSet.permissionDeniedCallback.getSimpleName());

        writer.write(format("%1$s\t@Override public void %2$s() { %3$s }\n\n",
                tabs, METHOD_DENY, callbackInvocation));

        // Setter for target instance (class containing annotated methods).
        writer.write(format(
                "%1$s\t@Override public void %4$s(%2$s %3$s) {\n"
                + "%1$s\t\tthis.%3$s = %3$s;\n"
                + "%1$s\t}\n\n",

                tabs, // 1
                intermediaryClassName, // 2
                VAR_TARGET_INSTANCE, // 3
                METHOD_SET_TARGET_INSTANCE // 4
        ));

        // Parcelable methods and Creator instance.
        writeParcelableImplementation(grantedCallback, callbackClassName, writer, tabs + "\t");

        // End class.
        writer.write(format("%1$s}\n\n", tabs));
    }

    private void writeParcelableImplementation(ExecutableElement grantedCallback,
            String permissionCallbackClassName,
            Writer writer,
            String tabs) throws IOException {

        boolean usageWarningsOn = grantedCallback.getAnnotation(NeedsPermission.class)
                .usageWarnings();

        // Parcelable method, describeContents.
        writer.write(tabs + "@Override public int describeContents() { return 0; }\n\n");

        // Parcelable method, writeToParcel.
        final String varDestination = "dest";
        writer.write(format("%1$s@Override public void writeToParcel(%2$s %3$s, int flags) {\n",
                tabs, // 1
                PARCEL, // 2
                varDestination // 3
        ));

        // Iterate through each field and write to parcel.
        // For each field, determine the appropriate Parcel method to use.
        final List<? extends VariableElement> parameters = grantedCallback.getParameters();
        String methodSuffix;
        TypeMirror paramType;

        for (VariableElement param : parameters) {

            paramType = param.asType();
            methodSuffix = determineParcelMethodSuffix(paramType);

            // Unsupported type. Display warning (if appropriate) and skip.
            if (null == methodSuffix) {
                if (usageWarningsOn) {
                    messager.printMessage(WARNING,
                            format("Parameter '%1$s %2$s' on method %3$s should be a "
                                    + "primitive type, String, Parcelable, or an array of "
                                    + "those types to ensure this callback can be invoked with "
                                    + "the appropriate arguments after a configuration change. "
                                    + "If handling configuration changes manually, this warning "
                                    + "can be ignored.",
                                    paramType, // 1
                                    param.getSimpleName(), // 2
                                    grantedCallback.getSimpleName()), // 3
                            grantedCallback);
                }
                continue;
            }

            // Convert boolean values to bytes, since there's no writeBoolean method on Parcel.
            if (BOOLEAN == paramType.getKind()) {
                writer.write(format(
                        "%1$s\t%2$s.writeByte((byte) (%3$s ? 1 : 0));\n",
                        tabs, // 1
                        varDestination, // 2
                        param.getSimpleName() // 3
                ));
                continue;
            }

            // Write value to parcel.
            writer.write(format(
                    "%1$s\t%2$s.write%3$s(%4$s%5$s);\n",
                    tabs, // 1
                    varDestination, // 2
                    methodSuffix, // 3
                    param.getSimpleName(), // 4
                    methodSuffix.contains("Parcelable") ? ", 0" : "" // 5
            ));
        }

        // End writeToParcel.
        writer.write(tabs + "}\n\n");

        // Creator instance.

        // First, read all fields from the source parcel.
        String varSource = "source";
        List<String> constructorArgs = new ArrayList<>();

        for (VariableElement param : parameters) {

            paramType = param.asType();
            methodSuffix = determineParcelMethodSuffix(paramType);

            // Unsupported type. Use a null argument.
            if (null == methodSuffix) {
                constructorArgs.add("null");
                continue;
            }

            // Convert byte values into boolean, since there's no readBoolean method on Parcel.
            if (BOOLEAN == paramType.getKind()) {
                constructorArgs.add(format("1 == %1$s.readByte()", varSource));
                continue;
            }

            // Read value from parcel.
            constructorArgs.add(format("%5$s%1$s.%2$s%3$s(%4$s)",
                    varSource, // 1
                    methodSuffix.contains("Array") && !methodSuffix.contains("Parcelable") // 2
                            ? "create"
                            : "read",
                    methodSuffix, // 3
                    methodSuffix.contains("Parcelable") // 4
                            ? "null"
                            : "",
                    methodSuffix.contains("ParcelableArray")
                            ? "(" + paramType + ") "
                            : ""
            ));
        }

        // Write CREATOR instance with implemented methods.
        writer.write(format(
                "%1$spublic static final %2$s<%3$s> CREATOR = new %2$s<%3$s>() {\n\n"

                // Creator method, createFromParcel.
                + "%1$s\t@Override public %3$s createFromParcel(%4$s %5$s) {\n"
                + "%1$s\t\treturn new %3$s(null%6$s);\n"
                + "%1$s\t}\n\n"

                // Creator method, newArray.
                + "%1$s\t@Override public %3$s[] newArray(int size) {\n"
                + "%1$s\t\treturn new %3$s[size];\n"
                + "%1$s\t}\n"

                + "%1$s};\n\n",

                tabs, // 1
                PARCELABLE_CREATOR, // 2
                permissionCallbackClassName, // 3
                PARCEL, // 4
                varSource, // 5
                constructorArgs.isEmpty() ? "" : ", " + join(constructorArgs, ", ") // 6
        ));
    }

    /**
     * Determine the appropriate {@code Parcel} method to use given the specified type.
     * E.g. if the specified type is {@code String}, this method will return {@code "String"},
     * which can then be used to construct the {@code Parcel} method to invoke, e.g.
     * {@code Parcel.writeString(String)} or {@code Parcel.readString()}.
     *
     * @param type the type for which the appropriate {@code Parcel} method suffix should be
     * determined.
     *
     * @return the appropriate {@code Parcel} method suffix to use for the specified type,
     * or {@code null} if none exists or isn't supported.
     */
    private String determineParcelMethodSuffix(TypeMirror type) {

        final TypeMirror stringType = elements.getTypeElement(STRING).asType();
        final TypeMirror parcelableType = elements.getTypeElement(PARCELABLE).asType();

        if (type.getKind().isPrimitive()) {
            return capitalize(type.getKind().name());
        }

        if (types.isSameType(type, stringType)) {
            return "String";
        }

        if (types.isAssignable(type, parcelableType)) {
            return "Parcelable";
        }

        if (ARRAY == type.getKind()) {
            TypeMirror arrayType = ((ArrayType) type).getComponentType();

            if (arrayType.getKind().isPrimitive()) {
                return capitalize(arrayType.getKind().name()) + "Array";
            }

            if (types.isSameType(arrayType, stringType)) {
                return "StringArray";
            }

            if (types.isAssignable(arrayType, parcelableType)) {
                return "ParcelableArray";
            }
        }

        // Boxed primitive types.
        if (types.isSameType(type, elements.getTypeElement(INTEGER_TYPE).asType())) {
            return "Int";
        }
        if (types.isSameType(type, elements.getTypeElement(BOOLEAN_TYPE).asType())) {
            return "Boolean";
        }
        if (types.isSameType(type, elements.getTypeElement(LONG_TYPE).asType())) {
            return "Long";
        }
        if (types.isSameType(type, elements.getTypeElement(DOUBLE_TYPE).asType())) {
            return "Double";
        }
        if (types.isSameType(type, elements.getTypeElement(BYTE_TYPE).asType())) {
            return "Byte";
        }
        if (types.isSameType(type, elements.getTypeElement(FLOAT_TYPE).asType())) {
            return "Float";
        }
        if (types.isSameType(type, elements.getTypeElement(SHORT_TYPE).asType())) {
            return "Short";
        }

        return null;
    }

    private String generateRationaleCode(GroupedAnnotatedMethodSet callbackSet, String tabs) {

        ExecutableElement grantedCallback = callbackSet.permissionGrantedCallback;
        final Element classElement = grantedCallback.getEnclosingElement();

        final String activity = VAR_TARGET_INSTANCE
                + (isActivityClass(classElement)
                ? ""
                : "." + METHOD_GET_ACTIVITY + "()");

        String requestPermissionsLine = format(Locale.ENGLISH,
                "%1$s.%2$s(%3$s.%4$s(%5$s, %6$s), %7$d);\n",

                VAR_TARGET_INSTANCE, // 1
                METHOD_REQUEST_PERMISSIONS, // 2
                GROUND_CONTROL, // 3
                METHOD_FILTER_GRANTED_PERMISSIONS, // 4
                activity, // 5
                VAR_PERMISSIONS_ARRAY, // 6
                callbackSet.requestCode // 7
        );

        final String rationaleMethodName = callbackSet.getShowRationaleMethodName();
        if (null == rationaleMethodName) {
            // No rationale specified, so just go straight to requesting the permissions.
            return tabs + requestPermissionsLine;
        }

        return format(
                // If rationale shouldn't be shown, proceed with requesting permissions.
                "%1$sif (!%2$s.shouldShowRationale(%3$s, %4$s)) {\n"
                + "%1$s\t%5$s"
                + "%1$s\treturn;\n"
                + "%1$s}\n\n"

                // Initialize rationale acknowledgement listener.
                + "%1$s%6$s %7$s = new %6$s(%4$s, %12$d) {\n"
                + "%1$s\t@Override public void %8$s(%13$s %9$s) {\n"
                + "%1$s\t\t%5$s"
                + "%1$s\t\t((%14$s) %9$s).%11$s = null;\n"
                + "%1$s\t}\n"
                + "%1$s};\n"

                // Save permissions for rationale.
                + "%1$s%9$s.%11$s = %4$s;\n"

                // Show rationale.
                + "%1$s%9$s.%10$s(%7$s);\n",

                tabs, // 1
                GROUND_CONTROL, // 2
                activity, // 3
                VAR_PERMISSIONS_ARRAY, // 4
                requestPermissionsLine, // 5
                ON_RATIONALE_ACKNOWLEDGED_LISTENER, // 6
                VAR_RATIONALE_ACK_LISTENER, // 7
                METHOD_ON_RATIONALE_ACKNOWLEDGED, // 8
                VAR_TARGET_INSTANCE, // 9
                rationaleMethodName, // 10
                FIELD_RATIONALE_PERMISSIONS, // 11
                callbackSet.requestCode, // 12
                getTopLevelType(classElement), // 13
                classElement.asType() // 14
        );
    }

    private String getIntermediaryClassName(Element classElement) {
        Element parentClass = getParentClass(classElement, types);
        return format("GroundControlled%s",
                null == parentClass ? "" : parentClass.getSimpleName());
    }

    private String getCallbackClassNameFor(Element methodElement) {
        final String methodName = methodElement.getSimpleName().toString();

        return format("%s%s%s",
                Character.toUpperCase(methodName.charAt(0)),
                methodName.substring(1),
                INTERFACE_PERMISSION_CALLBACK);
    }

    private HashSet<String> getPermissionsSet(ExecutableElement method) {
        NeedsPermission permissionAnnotation = method.getAnnotation(NeedsPermission.class);
        return new HashSet<>(Arrays.asList(permissionAnnotation.value()));
    }

    private TypeMirror getTopLevelType(Element classElement) {

        final TypeMirror classType = classElement.asType();

        TypeElement[] typeElements = new TypeElement[] {
                elements.getTypeElement(ACTIVITY),
                elements.getTypeElement(FRAGMENT),
                elements.getTypeElement(SUPPORT_FRAGMENT)
        };

        for (TypeElement typeElement : typeElements) {
            if (null != typeElement && types.isAssignable(classType, typeElement.asType())) {
                return typeElement.asType();
            }
        }

        return null;
    }

    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    private boolean isActivityClass(Element classElement) {
        TypeMirror activityType = elements.getTypeElement(ACTIVITY).asType();
        return types.isAssignable(classElement.asType(), activityType);
    }

    private boolean isValidContainingClass(Element classElement) {

        boolean isValid = true;

        // The class must derive from either Activity or Fragment.
        if (null == getTopLevelType(classElement)) {
            printClassValidationError(classElement, "must inherit from Activity or Fragment");
            isValid = false;
        }

        final PrimitiveType INT_TYPE = types.getPrimitiveType(INT);
        final NoType VOID_TYPE = types.getNoType(VOID);
        final DeclaredType STRING_TYPE = types.getDeclaredType(elements.getTypeElement(STRING));
        final ArrayType STRING_ARRAY_TYPE = types.getArrayType(STRING_TYPE);

        // Verify that the class has the requestPermissions method.
        ExecutableElement requestMethod = findMethod(METHOD_REQUEST_PERMISSIONS,
                VOID_TYPE,
                classElement,
                elements,
                types,
                STRING_ARRAY_TYPE,
                INT_TYPE);

        if (null == requestMethod) {
            printClassValidationError(classElement,
                    format("must have or inherit the method %1$s (see Activity or Fragment API "
                            + "documentation)",
                    METHOD_REQUEST_PERMISSIONS));
            isValid = false;
        }

        // Verify that the class has the onRequestPermissionsResult method.
        ExecutableElement resultMethod = findMethod(METHOD_ON_REQUEST_PERMISSIONS_RESULT,
                VOID_TYPE,
                classElement,
                elements,
                types,
                INT_TYPE,
                STRING_ARRAY_TYPE,
                types.getArrayType(INT_TYPE));

        if (null == resultMethod || resultMethod.getModifiers().contains(PRIVATE)) {
            printClassValidationError(classElement,
                    format("must have or inherit the method %1$s which is called by the Android "
                            + "framework",
                    METHOD_ON_REQUEST_PERMISSIONS_RESULT));
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidAnnotatedMethod(Element methodElement,
            String annotationName,
            boolean showUsageWarnings) {

        Set<Modifier> modifiers = methodElement.getModifiers();

        // Verify scope is not private.
        if (modifiers.contains(PRIVATE)) {
            printMethodValidationError(methodElement, annotationName, "cannot be private");
            return false;
        }

        // Verify method is not static.
        if (modifiers.contains(STATIC)) {
            printMethodValidationError(methodElement, annotationName, "cannot be static");
            return false;
        }

        // Verify return type is void.
        if (VOID != ((ExecutableElement) methodElement).getReturnType().getKind()) {
            printMethodValidationError(methodElement, annotationName, "must have void return type");
            return false;
        }

        // Display warning if annotating an inherited method, but still return as valid.
        printInheritedMethodWarning(methodElement, annotationName, showUsageWarnings);

        return true;
    }

    private boolean isValidOnPermissionDeniedMethod(Element methodElement) {

        if (!isValidAnnotatedMethod(methodElement, OnPermissionDenied.class.getSimpleName(),
                methodElement.getAnnotation(OnPermissionDenied.class).usageWarnings())) {
            return false;
        }

        if (((ExecutableElement) methodElement).getParameters().isEmpty()) {
            return true;
        }

        printMethodValidationError(methodElement,
                OnPermissionDenied.class.getSimpleName(),
                "must not contain any parameters");
        return false;
    }

    private boolean isValidOnShowRationaleMethod(Element methodElement) {

        if (!isValidAnnotatedMethod(methodElement, OnShowRationale.class.getSimpleName(),
                methodElement.getAnnotation(OnShowRationale.class).usageWarnings())) {
            return false;
        }

        final List<? extends VariableElement> params
                = ((ExecutableElement) methodElement).getParameters();
        final DeclaredType onRationaleAckListener = types.getDeclaredType(
                elements.getTypeElement(ON_RATIONALE_ACKNOWLEDGED_LISTENER));

        if (1 == params.size()
                && types.isSameType(onRationaleAckListener, params.get(0).asType())) {

            return true;
        }

        printMethodValidationError(methodElement,
                OnShowRationale.class.getSimpleName(),
                "must have exactly 1 parameter of type " + ON_RATIONALE_ACKNOWLEDGED_LISTENER);
        return false;
    }

    private void printClassValidationError(Element classElement, String errorFragment) {

        messager.printMessage(ERROR, format(
                "Class %1$s containing methods annotated with @%2$s %3$s",
                classElement.asType(), NeedsPermission.class.getSimpleName(), errorFragment),
                classElement);
    }

    private void printMethodValidationError(Element methodElement,
            String annotationName,
            String errorFragment) {

        messager.printMessage(ERROR,
                format("Callback methods%1$s %2$s: %3$s.%4$s",
                        null == annotationName ? "" : " annotated with @" + annotationName,
                        errorFragment,
                        methodElement.getEnclosingElement().asType(),
                        methodElement.getSimpleName().toString()),
                methodElement);
    }

    /**
     * Display warnings if there are any annotated callbacks that don't match
     * permission requirement methods.
     *
     * @param callbackSets list of callback sets to check for unmatched annotated callbacks.
     */
    private void printUnmatchedAnnotationCallbackWarnings(
            List<GroupedAnnotatedMethodSet> callbackSets) {

        final String warning = "Permissions from @%1$s on method %2$s do not match permissions "
                + "specified in any " + NeedsPermission.class.getSimpleName() + " annotation";

        for (GroupedAnnotatedMethodSet callbackSet : callbackSets) {

            if (null != callbackSet.permissionGrantedCallback) {
                continue;
            }

            if (null != callbackSet.permissionDeniedCallback
                    && callbackSet.permissionDeniedCallback
                            .getAnnotation(OnPermissionDenied.class)
                            .usageWarnings()
            ) {

                messager.printMessage(WARNING,
                        format(warning,
                                OnPermissionDenied.class.getSimpleName(),
                                callbackSet.permissionDeniedCallback.getSimpleName()),
                        callbackSet.permissionDeniedCallback
                );
            }

            if (null != callbackSet.rationaleCallback
                    && callbackSet.rationaleCallback
                            .getAnnotation(OnShowRationale.class)
                            .usageWarnings()
            ) {

                messager.printMessage(WARNING,
                        format(warning,
                                OnShowRationale.class.getSimpleName(),
                                callbackSet.rationaleCallback.getSimpleName()),
                        callbackSet.rationaleCallback
                );
            }
        }
    }

    /**
     * Display a warning about annotating an inherited method, if the specified method
     * overrides a parent implementation. Annotating an overridden method may cause
     * unexpected behavior, especially if the annotated method is an Activity or Fragment
     * lifecycle method, because Ground Control may change the flow of execution to prompt
     * the user for the appropriate permissions.
     *
     * @param methodElement method about which to display a warning if the method overrides
     * a parent implementation.
     * @param annotationName the name of the annotation being used on the method.
     * @param showUsageWarnings whether to display usage warnings.
     */
    private void printInheritedMethodWarning(Element methodElement,
            String annotationName,
            boolean showUsageWarnings) {

        // If usage warnings are turned off, this method does nothing.
        if (!showUsageWarnings) {
            return;
        }

        ExecutableElement method = (ExecutableElement) methodElement;

        // Get parameter types as an array.
        final List<? extends VariableElement> params = method.getParameters();
        TypeMirror[] paramTypes = new TypeMirror[params.size()];
        for (int index = 0; index < paramTypes.length; ++index) {
            paramTypes[index] = params.get(index).asType();
        }

        // Check if there are any matching methods in parent class or interfaces.
        // If there are, raise the warning.

        final List<? extends TypeMirror> parentTypes = types.directSupertypes(
                methodElement.getEnclosingElement().asType());
        ExecutableElement overriddenMethod;
        Set<Modifier> modifiers;

        for (TypeMirror parentType : parentTypes) {

            overriddenMethod = findMethod(method.getSimpleName().toString(), method.getReturnType(),
                    types.asElement(parentType), elements, types, paramTypes);
            if (null == overriddenMethod) {
                continue;
            }

            // Methods can't override parent implementations if the parent methods are
            // private or static, so there's no need to display a warning for these cases.
            modifiers = overriddenMethod.getModifiers();
            if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
                continue;
            }

            messager.printMessage(WARNING,
                    format("Annotating methods that override a parent implementation with @%1$s "
                            + "may cause unexpected behavior, especially if those methods are "
                            + "Activity or Fragment life cycle methods", annotationName),
                    methodElement);
            break;
        }
    }

    private List<GroupedAnnotatedMethodSet> findAllAnnotatedCallbacks(Element classElement) {

        Map<Set<String>, List<GroupedAnnotatedMethodSet>> methodSetsMap = new HashMap<>();
        NeedsPermission needsPermissionAnnotation;
        OnPermissionDenied onPermissionDeniedAnnotation;
        OnShowRationale onShowRationaleAnnotation;
        Set<String> permissions;
        List<GroupedAnnotatedMethodSet> methodSets;
        GroupedAnnotatedMethodSet methodSet;

        for (Element element : classElement.getEnclosedElements()) {
            if (METHOD != element.getKind()) {
                continue;
            }

            // Show error if method is annotated by more than one Ground Control annotation.
            needsPermissionAnnotation = element.getAnnotation(NeedsPermission.class);
            onPermissionDeniedAnnotation = element.getAnnotation(OnPermissionDenied.class);
            onShowRationaleAnnotation = element.getAnnotation(OnShowRationale.class);

            int numAnnotations = (null == needsPermissionAnnotation ? 0 : 1)
                    + (null == onPermissionDeniedAnnotation ? 0 : 1)
                    + (null == onShowRationaleAnnotation ? 0 : 1);

            if (1 < numAnnotations) {
                printMethodValidationError(element, null, "can only be annotated by one of "
                        + "NeedsPermission, OnPermissionDenied, or OnShowRationale");
                continue;
            }

            // Validate and process NeedsPermission annotation.
            if (null != needsPermissionAnnotation) {
                if (!isValidAnnotatedMethod(element, NeedsPermission.class.getSimpleName(),
                        element.getAnnotation(NeedsPermission.class).usageWarnings())) {
                    continue;
                }

                permissions = new HashSet<>(Arrays.asList(needsPermissionAnnotation.value()));
                methodSets = methodSetsMap.get(permissions);
                if (null == methodSets) {
                    methodSet = new GroupedAnnotatedMethodSet();
                    methodSets = new ArrayList<>();
                    methodSets.add(methodSet);
                    methodSetsMap.put(permissions, methodSets);
                } else {
                    methodSet = methodSets.get(0);
                    if (null != methodSet.permissionGrantedCallback) {
                        methodSet = methodSet.clone();
                        methodSets.add(methodSet);
                    }
                }

                methodSet.permissions = needsPermissionAnnotation.value();
                methodSet.requestCode = ++nextRequestCode;
                methodSet.permissionGrantedCallback = (ExecutableElement) element;

                checkRationaleConflict(methodSet);
                continue;
            }

            // Validate and process OnPermissionDenied annotation.
            if (null != onPermissionDeniedAnnotation) {
                if (!isValidOnPermissionDeniedMethod(element)) {
                    continue;
                }

                permissions = new HashSet<>(Arrays.asList(onPermissionDeniedAnnotation.value()));
                methodSets = methodSetsMap.get(permissions);
                if (null == methodSets) {
                    methodSet = new GroupedAnnotatedMethodSet();
                    methodSet.permissions = onPermissionDeniedAnnotation.value();
                    methodSet.requestCode = ++nextRequestCode;
                    methodSets = new ArrayList<>();
                    methodSets.add(methodSet);
                    methodSetsMap.put(permissions, methodSets);
                }

                // Method sets already exist.
                // Iterate through each and set the permission denied callback, if non-null.
                for (GroupedAnnotatedMethodSet annotatedMethodSet : methodSets) {
                    if (null == annotatedMethodSet.permissionDeniedCallback) {
                        annotatedMethodSet.permissionDeniedCallback = (ExecutableElement) element;
                        continue;
                    }
                    messager.printMessage(ERROR,
                            "Cannot have multiple OnPermissionDenied annotations "
                                    + "using the same permissions.",
                            element);
                    break;
                }
                continue;
            }

            // Validate and process OnShowRationale annotation.
            if (null != onShowRationaleAnnotation) {
                if (!isValidOnShowRationaleMethod(element)) {
                    continue;
                }

                permissions = new HashSet<>(Arrays.asList(onShowRationaleAnnotation.value()));
                methodSets = methodSetsMap.get(permissions);
                if (null == methodSets) {
                    methodSet = new GroupedAnnotatedMethodSet();
                    methodSet.permissions = onShowRationaleAnnotation.value();
                    methodSet.requestCode = ++nextRequestCode;
                    methodSets = new ArrayList<>();
                    methodSets.add(methodSet);
                    methodSetsMap.put(permissions, methodSets);
                }

                // Method sets already exist.
                // Iterate through each and set the rationale callback, if non-null.
                for (GroupedAnnotatedMethodSet annotatedMethodSet : methodSets) {
                    if (null == annotatedMethodSet.rationaleCallback) {
                        annotatedMethodSet.rationaleCallback = (ExecutableElement) element;
                        checkRationaleConflict(annotatedMethodSet);
                        continue;
                    }
                    messager.printMessage(ERROR,
                            "Cannot have multiple OnShowRationale annotations "
                                    + "using the same permissions.",
                            element);
                    break;
                }
                // continue;
            }
        }

        // Return all method sets.
        methodSets = new ArrayList<>();
        for (List<GroupedAnnotatedMethodSet> sets : methodSetsMap.values()) {
            methodSets.addAll(sets);
        }
        return methodSets;
    }

    private void checkRationaleConflict(GroupedAnnotatedMethodSet methodSet) {

        // Display error for callback sets that indicate use of both
        // default and custom rationales.
        if (!methodSet.usesDefaultRationale() || null == methodSet.rationaleCallback) {
            return;
        }

        messager.printMessage(ERROR,
                "Custom rationale implementation conflicts with use of default rationale "
                        + "specified in NeedsPermission annotating method "
                        + methodSet.permissionGrantedCallback.getSimpleName(),
                methodSet.rationaleCallback);
    }

    /**
     * Data container for grouping methods annotated with the same set of permissions.
     */
    private static class GroupedAnnotatedMethodSet implements Cloneable {

        public String[] permissions;
        public ExecutableElement permissionGrantedCallback;
        public ExecutableElement permissionDeniedCallback;
        public ExecutableElement rationaleCallback;
        public int requestCode;

        public String getShowRationaleMethodName() {

            if (null != rationaleCallback) {
                return rationaleCallback.getSimpleName().toString();
            }

            return !usesDefaultRationale()
                    ? null
                    : "__groundControlShowRationale_" + permissionGrantedCallback.getSimpleName();
        }

        public boolean handleRestarts() {

            if (usesDefaultRationale()) {
                return true;
            }

            return null != rationaleCallback
                    && rationaleCallback.getAnnotation(OnShowRationale.class).handleRestarts();
        }

        @Override
        public GroupedAnnotatedMethodSet clone() {
            GroupedAnnotatedMethodSet clone = new GroupedAnnotatedMethodSet();
            clone.permissions = Arrays.copyOf(permissions, permissions.length);
            clone.permissionGrantedCallback = permissionGrantedCallback;
            clone.permissionDeniedCallback = permissionDeniedCallback;
            clone.rationaleCallback = rationaleCallback;
            clone.requestCode = requestCode;
            return clone;
        }

        private boolean usesDefaultRationale() {

            if (null == permissionGrantedCallback) {
                return false;
            }

            return 0 != permissionGrantedCallback.getAnnotation(NeedsPermission.class)
                    .rationaleResourceId();
        }
    }
}
