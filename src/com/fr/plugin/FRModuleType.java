package com.fr.plugin;

import com.fr.plugin.ui.FRIcons;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import javax.swing.*;

/**
 * Created by vito on 16/5/15.
 */
public class FRModuleType extends JavaModuleType {
    public static final String ID = "FR_JAVA_MODULE";
    private static final String MODULE_NAME = "FineReport";

    public FRModuleType() {
        super(ID);
    }

    public static FRModuleType getInstance() {
        return (FRModuleType)ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public FRModuleBuilder createModuleBuilder() {
        return new FRModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "FineReport dev";
    }

    @Override
    public Icon getBigIcon() {
        return FRIcons.Fr;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return FRIcons.Fr;
    }

    @NotNull
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, moduleBuilder, new Condition<SdkTypeId>() {
            @Override
            public boolean value(SdkTypeId sdkType) {
                return moduleBuilder.isSuitableSdkType(sdkType);
            }
        });
    }

    @Override
    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return isValidJavaSdk(module);
    }

    public static boolean isValidJavaSdk(@NotNull Module module) {
        if (ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.SOURCES).isEmpty()) return true;
        return JavaPsiFacade.getInstance(module.getProject()).findClass(CommonClassNames.JAVA_LANG_OBJECT,
                module.getModuleWithLibrariesScope()) != null;
    }



}