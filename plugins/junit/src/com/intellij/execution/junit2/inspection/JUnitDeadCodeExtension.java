/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * User: anna
 * Date: 28-May-2007
 */
package com.intellij.execution.junit2.inspection;

import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.deadCode.DeadCodeExtension;
import com.intellij.codeInspection.reference.RefClass;
import com.intellij.codeInspection.reference.RefElement;
import com.intellij.codeInspection.reference.RefMethod;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierListOwner;
import org.jdom.Element;

public class JUnitDeadCodeExtension implements DeadCodeExtension {
  public boolean ADD_JUNIT_TO_ENTRIES = true;

  public String getDisplayName() {
    return InspectionsBundle.message("inspection.dead.code.option2");
  }

  public boolean isEntryPoint(RefElement refElement) {
    if (ADD_JUNIT_TO_ENTRIES) {
      if (refElement instanceof RefClass) {
        final RefClass aClass = (RefClass)refElement;
        if (JUnitUtil.isTestClass(aClass.getElement())) {
          return true;
        }
      }
      else if (refElement instanceof RefMethod) {
        final RefMethod refMethod = (RefMethod)refElement;
        final PsiModifierListOwner psiMethod = refMethod.getElement();
        if (psiMethod instanceof PsiMethod) {
          final String name = ((PsiMethod)psiMethod).getName();
          final PsiClass psiClass = refMethod.getOwnerClass().getElement();
          if (JUnitUtil.isTestClass(psiClass) && psiMethod.hasModifierProperty(PsiModifier.PUBLIC) &&
              !psiMethod.hasModifierProperty(PsiModifier.ABSTRACT) && name.startsWith("test")
              || "suite".equals(name) || "setUp".equals(name) ||  "tearDown".equals(name)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isSelected() {
    return ADD_JUNIT_TO_ENTRIES;
  }

  public void setSelected(boolean selected) {
    ADD_JUNIT_TO_ENTRIES = selected;
  }

  public void readExternal(Element element) throws InvalidDataException {
    DefaultJDOMExternalizer.readExternal(this, element);
  }

  public void writeExternal(Element element) throws WriteExternalException {
    DefaultJDOMExternalizer.writeExternal(this, element);
  }
}