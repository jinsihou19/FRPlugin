package com.fr.plugin;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by vito on 2017/8/19.
 * ��ʼ��������
 */
public class FRFrameworkInit {

    private static final String PACKAGE_NAME = "com.fr.plugin.";
    private static final String PACKAGE_NAME_KEY = "PACKAGE";
    private static final String FR_DATE = "FR_DATE";
    private static final String PLUGIN_XML = "plugin.xml";
    private static final String BUILD_XML = "build.xml";
    private static final String LIB_KEEP = ".keep";
    private static final Logger LOG = Logger.getInstance("#com.fr.plugin.FRFrameworkInit");

    public void init(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    createProjectFile(module, model);
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        });
    }

    private void createProjectFile(@NotNull Module module, @NotNull ModifiableRootModel model) throws Exception {
        VirtualFile[] contentRoots = model.getContentRoots();
        VirtualFile[] srcRoots = model.getSourceRoots();
        if (contentRoots.length == 0 || srcRoots.length == 0) {
            return;
        }
        VirtualFile contentRoot = contentRoots[0];
        VirtualFile srcRoot = srcRoots[0];

        PsiDirectory psiRootDirectory = new PsiJavaDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()), contentRoot);
        createFile(module, PLUGIN_XML, psiRootDirectory, "plugin.xml");
        createFile(module, BUILD_XML, psiRootDirectory, "build.xml");

        //lib
        VirtualFile libDir = contentRoot.createChildDirectory(this, "lib");
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

    private static void createFile(@NotNull Module module, @NotNull String fileName, @NotNull PsiDirectory directory, @NotNull final String templateName) {
        Properties props = new Properties(FileTemplateManager.getDefaultInstance().getDefaultProperties());
        props.setProperty(FileTemplate.ATTRIBUTE_NAME, module.getName());
        props.setProperty(FileTemplate.ATTRIBUTE_CLASS_NAME, makeClassName(module.getName()));
        props.setProperty(PACKAGE_NAME_KEY, PACKAGE_NAME + fileName);
        props.setProperty(FR_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        try {
            FileTemplateUtil.createFromTemplate(template, fileName, props, directory);
        } catch (IncorrectOperationException ignore) {
            // ignore
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    private static String makeClassName(String oriName) {
        return oriName.substring(0, 1).toUpperCase() + oriName.substring(1);
    }
}
