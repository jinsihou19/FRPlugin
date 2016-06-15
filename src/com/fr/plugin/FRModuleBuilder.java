package com.fr.plugin;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.file.impl.FileManager;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by vito on 16/5/15.
 */
public class FRModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {

    private static final String PACKAGE_NAME = "com.fr.plugin.";
    private static final String CLASS_NAME = "CLASS_NAME";
    private static final String PACKAGE_NAME_KEY = "PACKAGE";
    private static final String FR_DATE = "FR_DATE";
    private static final String PLUGIN_XML = "plugin.xml";
    private static final String BUILD_XML = "build.xml";
    private static final String LIB_KEEP = ".keep";


    public FRModuleBuilder() {
        addListener(this);
    }


    @Override
    public ModuleType getModuleType() {
        return FRModuleType.getInstance();
    }

    @Override
    public void moduleCreated(@NotNull Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if (roots.length == 1) {
            VirtualFile srcRoot = roots[0];//src
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        createProjectFile(module, srcRoot);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void createProjectFile(Module module, VirtualFile srcRoot) throws Exception {
        //project root
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getContentRoots();
        PsiDirectory psiRootDirectory = new PsiJavaDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()), roots[0]);
        createFile(module, PLUGIN_XML, psiRootDirectory, "plugin.xml");
        createFile(module, BUILD_XML, psiRootDirectory, "build.xml");

        //lib
        VirtualFile libDir = roots[0].createChildDirectory(this, "lib");
        PsiDirectory psiLibDirectory = new PsiJavaDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()), libDir);
        createFile(module, LIB_KEEP, psiLibDirectory, ".keep");
        //com.fr.plugin.${NAME}
        VirtualFile pluginDir = srcRoot.createChildDirectory(this, "com")
                .createChildDirectory(this, "fr")
                .createChildDirectory(this, "plugin")
                .createChildDirectory(this, module.getName());
        PsiDirectory psiDirectory = new PsiJavaDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()), pluginDir);
        if (("true").equals(module.getOptionValue("CHECK"))) {
            createFile(module, module.getName(), psiDirectory, "Main.java");
        } else {
            createFile(module, module.getName(), psiDirectory, "MainNoFuncRe.java");
        }

        //com.fr.plugin.${NAME}.resource.locale
        VirtualFile local = pluginDir.createChildDirectory(this, "locale");
        PsiDirectory localPsiDirectory = new PsiJavaDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()), local);
        createFile(module, module.getName(), localPsiDirectory, "locale.properties");
        createFile(module, module.getName() + "_en_US", localPsiDirectory, "locale.properties");
        createFile(module, module.getName() + "_zh_CN", localPsiDirectory, "locale.properties");
        createFile(module, module.getName() + "_zh_TW", localPsiDirectory, "locale.properties");
        createFile(module, module.getName() + "." + "locale", localPsiDirectory, "LocaleFinder.java");
    }

    private static void createFile(Module module, String fileName, @NotNull PsiDirectory directory, final String templateName) {
        Properties props = new Properties(FileTemplateManager.getInstance().getDefaultProperties(directory.getProject()));
        props.setProperty(FileTemplate.ATTRIBUTE_NAME, module.getName());
        props.setProperty(CLASS_NAME, makeClassName(module.getName()));
        props.setProperty(PACKAGE_NAME_KEY, PACKAGE_NAME + fileName);
        props.setProperty(FR_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        final FileTemplate template = FileTemplateManager.getInstance().getInternalTemplate(templateName);

        try {
            FileTemplateUtil.createFromTemplate(template, fileName, props, directory, FRModuleBuilder.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeClassName(String oriName) {
        return oriName.substring(0, 1).toUpperCase() +
                oriName.substring(1);
    }

}