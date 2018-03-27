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

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static java.lang.String.format;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.type.TypeKind.DECLARED;

/**
 * Utility methods for annotation processing.
 */
public abstract class ProcessorUtils {

    protected ProcessorUtils() {
    }

    /**
     * Get the parent class of the specified class element.
     *
     * @param classElement the class element whose parent element is to be retrieved.
     * @param typeUtils {@link Types} instance from
     * {@link ProcessingEnvironment}.
     *
     * @return the parent class of the specified class element.
     */
    public static Element getParentClass(Element classElement,
            Types typeUtils) {

        return null == classElement || DECLARED != classElement.asType().getKind()
                ? null
                : typeUtils.asElement(((TypeElement) classElement).getSuperclass());
    }

    /**
     * Retrieve the {@link ExecutableElement} which matches the specified method signature,
     * including name, return type, and parameter types. This implementation searches through
     * declared methods as well as inherited methods from parent classes.
     *
     * @param methodName the name of the method to find.
     * @param returnType the return type of the method.
     * @param classElement the element representing the class which is expected to contain the
     * target method.
     * @param elementUtils {@link Elements} instance from {@link ProcessingEnvironment}.
     * @param typeUtils {@link Types} instance from {@link ProcessingEnvironment}.
     * @param paramTypes array of parameter types that the method accepts.
     *
     * @return the {@link ExecutableElement} which matches the specified method signature,
     * or {@code null} if it could not be found.
     */
    public static ExecutableElement findMethod(
            String methodName,
            TypeMirror returnType,
            Element classElement,
            Elements elementUtils,
            Types typeUtils,
            TypeMirror... paramTypes) {

        final List<? extends Element> members = elementUtils.getAllMembers(
                (TypeElement) typeUtils.asElement(classElement.asType()));
        ExecutableElement method;
        List<? extends VariableElement> params;

        for (Element member : members) {

            if (METHOD != member.getKind()) {
                continue;
            }
            method = (ExecutableElement) member;
            params = method.getParameters();

            // Check method name, return type, and number of parameters.
            if (!methodName.equals(member.getSimpleName().toString())
                    || !typeUtils.isSameType(returnType, method.getReturnType())
                    || paramTypes.length != params.size()) {

                continue;
            }

            // Check parameter types.
            boolean paramsCheckOut = true;
            for (int index = 0; index < params.size(); ++index) {
                if (!typeUtils.isSameType(paramTypes[index], params.get(index).asType())) {
                    paramsCheckOut = false;
                    break;
                }
            }

            if (paramsCheckOut) {
                return method;
            }
        }

        return null;
    }

    /**
     * Build a string representing all items provided by the specified {@link Iterable},
     * separated by the specified delimiter.
     *
     * @param iterable the {@link Iterable} containing the items from which to build
     * the joined string.
     * @param delimiter the sequence of characters to separate each item in the {@link Iterable}.
     * @return a string representing all items provided by the {@link Iterable},
     * separated by the specified delimiter.
     * @param <ItemType> the type of the items in the {@link Iterable}.
     *
     * @return a string representing all items provided by the {@link Iterable},
     * separated by the specified delimiter.
     */
    public static <ItemType> String join(Iterable<ItemType> iterable,
            String delimiter) {

        return join(iterable, delimiter, Object::toString);
    }

    /**
     * Build a string representing all items provided by the specified {@link Iterable},
     * separated by the specified delimiter.
     *
     * @param iterable the {@link Iterable} containing the items from which to build
     * the joined string.
     * @param delimiter the sequence of characters to separate each item in the {@link Iterable}.
     * @param serializer optional function for converting items into a string representation.
     * @param <ItemType> the type of the items in the {@link Iterable}.
     *
     * @return a string representing all items provided by the {@link Iterable},
     * separated by the specified delimiter.
     */
    public static <ItemType> String join(Iterable<ItemType> iterable,
            String delimiter,
            ItemSerializer<ItemType> serializer) {

        if (null == iterable) {
            return "";
        }

        if (null == delimiter) {
            delimiter = "";
        }

        StringBuilder builder = new StringBuilder();
        for (ItemType item : iterable) {
            builder.append(delimiter).append(serializer.serialize(item));
        }

        return delimiter.length() > builder.length()
                ? builder.toString()
                : builder.substring(delimiter.length());
    }

    public interface ItemSerializer<ItemType> {
        String serialize(ItemType item);

    }

    /**
     * Obtain the string representation of the specified method's parameter list.
     *
     * @param methodElement the method whose parameters are to be retrieved as a string.
     *
     * @return the string representation of the specified method's parameter list.
     */
    public static String stringifyParameters(ExecutableElement methodElement) {

        return join(methodElement.getParameters(), ", ",
                parameter -> format("%s %s", parameter.asType(), parameter.getSimpleName()));
    }
}
