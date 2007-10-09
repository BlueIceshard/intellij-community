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

package com.intellij.execution.junit;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.testframework.TestSearchScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;

public class AllInPackageConfigurationProducer extends JUnitConfigurationProducer {
  private PsiPackage myPackage = null;

  protected RunnerAndConfigurationSettingsImpl createConfigurationByElement(final Location location, final ConfigurationContext context) {
    final Project project = location.getProject();
    final PsiElement element = location.getPsiElement();
    myPackage = checkPackage(element);
    if (myPackage == null) return null;
    RunnerAndConfigurationSettingsImpl settings = cloneTemplateConfiguration(project, context);
    final JUnitConfiguration configuration = (JUnitConfiguration)settings.getConfiguration();
    final JUnitConfiguration.Data data = configuration.getPersistentData();
    data.PACKAGE_NAME = myPackage.getQualifiedName();
    data.TEST_OBJECT = JUnitConfiguration.TEST_PACKAGE;
    if (data.getScope() != TestSearchScope.WHOLE_PROJECT) {
      final Module predefinedModule = configuration.getConfigurationModule().getModule();
      if (predefinedModule == null) {
        Module module = null;
        if (element instanceof PsiDirectory) {
          module = VfsUtil.getModuleForFile(project, ((PsiDirectory)element).getVirtualFile());
        }
        if (module != null) {
          configuration.setModule(module);
        }
        else {
          data.setScope(TestSearchScope.WHOLE_PROJECT);
        }
      }
    } else {
      final RunnerAndConfigurationSettingsImpl template =
        ((RunManagerImpl)context.getRunManager()).getConfigurationTemplate(getConfigurationFactory());
      final Module selectedModule = context.getModule();
      if (selectedModule != null) {
        final ModuleBasedConfiguration templateConfiguration = (ModuleBasedConfiguration)template.getConfiguration();
        if (templateConfiguration.getConfigurationModule().getModule() == null) {
          configuration.setModule(selectedModule);
          data.setScope(TestSearchScope.SINGLE_MODULE);
        }
      }
    }
    configuration.setGeneratedName();
    return settings;
  }

  public PsiElement getSourceElement() {
    return myPackage;
  }
}
