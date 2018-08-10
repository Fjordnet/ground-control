Ground Control
==============

## Overview

Ground Control is a library that can be used to simplify the implementation of [runtime permission handling](https://developer.android.com/training/permissions/requesting.html) introduced in Android 6.0 Marshmallow. It uses annotations to generate the boilerplate code that prompts the user for permission and shows rationales when appropriate. It also uses [aspect-oriented programming](https://en.wikipedia.org/wiki/Aspect-oriented_programming) to weave the generated code into the execution flow.

Ground Control annotations are only supported on methods within activities and fragments. See [limitations](#limitations).

The current implementation of Ground Control does not support Kotlin and is unaware of the existence of [architecture components](https://developer.android.com/topic/libraries/architecture/index.html).

## Usage

### NeedsPermission

To use Ground Control, annotate the methods within an activity or fragment requiring [dangerous permissions](https://developer.android.com/guide/topics/permissions/requesting.html#normal-dangerous) with `@NeedsPermission`, supplying it with the permissions needed by the method. Note that permissions must still be added to the Android manifest as usual.

Optionally specify attribute `rationaleResourceId` to display a simple rationale dialog with the specified text, when displaying a rationale is appropriate as queried by [`shouldShowRequestPermissionRationale(String)`](https://developer.android.com/reference/android/app/Activity.html#shouldShowRequestPermissionRationale%28java.lang.String%29).

    import static android.Manifest.permission.*;

    @NeedsPermission(CAMERA)
    protected void captureMedia(boolean video) {
        // Code that opens the camera.
    }
    
    @NeedsPermission(value = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
        rationaleResourceId = R.string.rationale_write_storage)
    protected void writeMedia() {
        // Code that writes to external storage.
    }

Invoke these methods throughout your code as you would any other method. Ground Control will automatically prompt the user to grant the permissions specified by the annotation, if the permissions have not already been granted. Execution flow will resume with this method once permission has been granted.

### OnShowRationale

To display a custom rationale, annotate a separate method with `@OnShowRationale`. This method will automatically be invoked by Ground Control as indicated by [`shouldShowRequestPermissionRationale(String)`](https://developer.android.com/reference/android/app/Activity.html#shouldShowRequestPermissionRationale%28java.lang.String%29).

The method must have a single parameter of type `OnRationaleAcknowledgedListener`, and in its implementation, you will need to call one of the listener's `onRationaleAcknowledged` methods to pass control back to Ground Control to display the permission prompt. The `onRationaleAcknowledged` method accepts an instance of `Activity` or `Fragment`. Use the appropriate version based on the type of the class containing these annotated methods.

    @OnShowRationale(CAMERA)
    protected void showCameraRationale(final OnRationaleAcknowledgedListener listener) {

        rationaleDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.id.rationale_title_camera)
                .setMessage(R.id.rationale_message_camera)
                .setPositiveButton(R.id.rationale_button_camera,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                listener.onRationaleAcknowledged(MainActivity.this);
                            }
                        })
                .create();

        rationaleDialog.show();
    }

When displaying a rationale that requires user interaction to dismiss, there is the possibility that the device configuration changes, causing the activity or fragment to be destroyed and recreated. In those cases, there are a couple options.

1. Tell Ground Control to re-invoke the method annotated by `OnShowRationale` after an activity or fragment restart by specifying the annotation attribute `handleRestarts = true`.

        @OnShowRationale(value = WRITE_CONTACTS, handleRestarts = true)
        protected void showUpdateContactsRationale(final OnRationaleAcknowledgedListener listener) {
    
            inlineRationale.setText(R.string.request_write_contacts_explanation);
            ackRationaleButton.setOnClickListener(view -> {
                inlineRationale.setVisibility(GONE);
                ackRationaleButton.setVisibility(GONE);
                listener.onRationaleAcknowledged(this);
            });
    
            inlineRationale.setVisibility(VISIBLE);
            ackRationaleButton.setVisibility(VISIBLE);
        }

2. Cache the `OnRationaleAcknowledgedListener` instance as a field, save it in `onSaveInstanceState` as a `Parcelable`, and recreate it when the activity or fragment comes back.

        @OnShowRationale({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
        protected void showLocationRationale(OnRationaleAcknowledgedListener listener) {
            rationaleListener = listener;
            RationaleDialogFragment rationale = RationaleDialogFragment.newInstance(
                    R.string.request_location_title, R.string.request_location_explanation);
            rationale.show(getSupportFragmentManager(), "rationale");
        }
    
        @Override
        public void onRationaleDismissed() {
            if (null == rationaleListener) {
                return;
            }
            rationaleListener.onRationaleAcknowledged(this);
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            if (null != savedInstanceState) {
                rationaleListener = savedInstanceState.getParcelable(STATE_RATIONALE_LISTENER);
            }
        }
        
        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(STATE_RATIONALE_LISTENER, rationaleListener);
        }


### OnPermissionDenied

To get a callback when a permission is denied, create a method annotated with `@OnPermissionDenied`.

    @OnPermissionDenied(CAMERA)
    protected void onCameraDenied() {
        setResult(RESULT_CANCELED);
        finish();
    }

### GroundControl utilities

There are several static utility methods exposed through the `GroundControl` helper class.

* To determine if permissions are already granted to the app, use `hasPermissions`.
* To determine if a rationale should be displayed for permissions, use `shouldShowRationale`.
* To get an `Intent` to launch the app settings, use `createAppSettingsIntent`.
* To simply launch the app settings, use `showAppSettings`.

## Download

Gradle

Add the following entries to your buildscript. This may be in the top level build.gradle file.

    buildscript {
        repositories {
            mavenCentral()
        }
        dependencies {
            classpath 'com.fjordnet.groundcontrol:gradle-plugin:1.0.2'
        }
    }

In your app's build.gradle file, apply the plugin. The plugin pulls in additional dependencies automatically. It must be applied after the Android plugin.

    apply plugin: 'com.android.application'
    apply plugin: 'com.fjordnet.groundcontrol'

You'll also need the Android `support-compat` library, version 24.1.0 or greater, if it's not already present in your app dependencies block or pulled transitively through another dependency.

    dependencies {
        compile 'com.android.support:support-compat:24.1.0'
    }

<a id="limitations"></a>
## Limitations

There are several limitations to be mindful of when using Ground Control. These limitations will be detected during compilation, and appropriate warnings or errors will be surfaced. Warnings can be suppressed for any of the annotation types by including `usageWarnings = false` attribute.

#### Errors

* Class using Ground Control must inherit from `Activity` or `Fragment` (support `Fragment` is OK).
* Class must have or inherit [requestPermissions(String\[\], int)](https://developer.android.com/reference/android/app/Activity.html#requestPermissions%28java.lang.String%5B%5D%2C%20int%29).
* Class must have or inherit [onRequestPermissionsResult(int, String\[\], int\[\])](https://developer.android.com/reference/android/app/Activity.html#onRequestPermissionsResult%28int%2C%20java.lang.String%5B%5D%2C%20int%5B%5D%29)
* Method must not be private (all annotations).
* Method must not be static (all annotations).
* Method must have `void` return type (all annotations).
* Method annotated with `@OnPermissionDenied` must have an empty parameter list.
* Method annotated with `@OnShowRationale` must have one parameter of type `OnRationaleAcknowledgedListener`.
* Method can have at most one Ground Control annotation.
* Class can have at most one method annotated with `@OnPermissionDenied` with a particular set of permissions (multiple can exist so long as the permissions are different).
* Class can have at most one method annotated with `@OnShowRationale` with a particular set of permissions (multiple can exist so long as the permissions are different).

#### Warnings

* Method is inherited from a parent class or interface (all annotations).
* Method annotated with `@NeedsPermission` does not have parameters of primitive types, `String`, `Parcelable`, or arrays of the above types. (This warning can be safely ignored if activity or fragment restarts, e.g. configuration changes, will be avoided.)
* Method annotated with `@OnPermissionDenied` or `@OnShowRationale` does not have a matching method annotated with `@NeedsPermission` with the same set of permissions.

#### Pitfalls

Be wary of the following scenarios. These cases will not be detected during compilation and may cause undesired behavior.

* Calling methods annotated with `@NeedsPermission` from any activity or fragment life cycle methods (e.g. `onActivityCreated`, `onStart`, etc.) will likely cause issues during activity or fragment restarts (e.g. configuration changes).
* Overriding `onRequestPermissionsResult` and not calling the parent implementation may prevent Ground Control from validating the results and appropriately handing execution flow back to your app.

## Library developers

### Environment

This library was developed using Android Studio 2.3.3 and Java JDK 1.8.0_74.

### Local installation

Installing a build of the library locally allows you to test the library in the context of an app you're building in parallel.
This does not replace the need for a sample app module in the library code repo. It just allows for deeper testing and validation in a real development context.

To install a build of the library, issue the following command in terminal from the root project's directory:

    ./gradlew install

This will produce the appropriate binaries and copy it into your local maven repository (~/.m2/repository).

In your project, include your dependency as you normally would. For example:

    dependencies {
        classpath 'com.fjordnet.groundcontrol:gradle-plugin:1.0.2-SNAPSHOT'
    }

You will need to add the local maven repository for Gradle to be able to find it.

    repositories {
        mavenLocal()
    }

## License

    Copyright 2017-2018 Fjord

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
