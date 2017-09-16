package com.fr.plugin;

import com.fr.plugin.ui.FRIcons;
import com.fr.plugin.ui.Setting;
import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by vito on 16/5/15.
 */
public class FRFramework extends FrameworkTypeEx {
    private static final String FRAMEWORK_ID = "FR_Plugin";
    private Setting setting;

    protected FRFramework() {
        super(FRAMEWORK_ID);
        setting = new Setting();
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "FineReport Plugin Framework";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return FRIcons.Fr;
    }

    @NotNull
    @Override
    public FrameworkSupportInModuleProvider createProvider() {
        return new FrameworkSupportInModuleProvider() {
            @NotNull
            @Override
            public FrameworkTypeEx getFrameworkType() {
                return FRFramework.this;
            }

            @NotNull
            @Override
            public FrameworkSupportInModuleConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
                return new FrameworkSupportInModuleConfigurable() {
                    @Nullable
                    @Override
                    public JComponent createComponent() {
                        return setting.settingPane;
                    }

                    @Override
                    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {
                        //do what you want here: setup a library, generate a specific file, etc
                        module.setOption(FRFrameworkInit.FUNCTION_RECORD_CHECK, String.valueOf(setting.needFunctionRecord()));
                        module.setOption(FRFrameworkInit.FR_VERSION, String.valueOf(setting.getVersion()));
                        new FRFrameworkInit().init(module, model, provider);
                    }
                };
            }

            @Override
            public boolean isEnabledForModuleType(@NotNull ModuleType moduleType) {
                return JavaModuleType.getModuleType().equals(moduleType);
            }
        };
    }
}
