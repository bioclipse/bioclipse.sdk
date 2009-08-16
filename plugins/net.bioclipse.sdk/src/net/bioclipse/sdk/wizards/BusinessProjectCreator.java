/* Copyright (c) 2008-2009  Ola Spjuth <olas@users.sf.net>
 *                    2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.sdk.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.bioclipse.sdk.templating.Templater;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.pde.internal.core.ClasspathComputer;
import org.eclipse.pde.internal.core.ExecutionEnvironmentAnalyzer;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class BusinessProjectCreator {

    private final static String TEMPLATE_FOLDER_FOO_MANAGER =
        "templates/net.bioclipse.foo.business/";

    private WizardNewProjectCreationPage fFirstPage;
    private ManagerDataInputPage dataInputPage;

    public BusinessProjectCreator(
        WizardNewProjectCreationPage fFirstPage,
        ManagerDataInputPage dataInputPage) {
        this.fFirstPage = fFirstPage;
        this.dataInputPage = dataInputPage;
    }

    protected void createProject(IProgressMonitor monitor) {
        monitor.beginTask("Creating the business project", 50);
        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            monitor.subTask("Creating directories");
            IProject project = root.getProject(fFirstPage.getProjectName());
            addNaturesAndBuilders(monitor, project);
            IPath projectPath = createFolders(monitor, root, project);
            createFiles(monitor, root, projectPath);
        } catch(CoreException x) {
            x.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            monitor.done();
        }
    }

    private void createFiles(IProgressMonitor monitor, IWorkspaceRoot root,
            IPath projectPath) throws CoreException, IOException {
        monitor.subTask("Creating files");

        String managerName = this.dataInputPage.getManager();
        String packageName = this.dataInputPage.getPackage();
        String managerNamespace = this.dataInputPage.getNamespace();
        String bundleName = packageName;
        String pluginName = packageName;

        // META-INF/MANIFEST.MF
        IPath path = projectPath.append("META-INF").
            append("MANIFEST.MF");
        IFile file = root.getFile(path);
        Templater context = new Templater(
            this.getClass().getClassLoader().getResourceAsStream(
                    TEMPLATE_FOLDER_FOO_MANAGER +
                    "META-INF/MANIFEST.MF"
            )
        );
        String fileContent = context.generate(
            "packageName", packageName,
            "bundleName", bundleName,
            "pluginName", pluginName
        );
        createFile(monitor, file, fileContent);

        // spring/context.xml
        path = projectPath.append("META-INF").append("spring").
            append("context.xml");
        file = root.getFile(path);
        context = new Templater(
            this.getClass().getClassLoader().getResourceAsStream(
                TEMPLATE_FOLDER_FOO_MANAGER +
                "META-INF/spring/context.xml"
            )
        );
        fileContent = context.generate(
            "managerName", managerName,
            "packageName", packageName
        );
        createFile(monitor, file, fileContent);

        // plugin.xml
        path = projectPath.append("plugin.xml");
        file = root.getFile(path);
        context = new Templater(
            this.getClass().getClassLoader().getResourceAsStream(
                TEMPLATE_FOLDER_FOO_MANAGER + "plugin.xml"
            )
        );
        fileContent = context.generate(
            "managerName", managerName,
            "packageName", packageName
        );
        createFile(monitor, file, fileContent);

        // build.properties
        path = projectPath.append("build.properties");
        file = root.getFile(path);
        context = new Templater(
            this.getClass().getClassLoader().getResourceAsStream(
                TEMPLATE_FOLDER_FOO_MANAGER +"build.properties"
            )
        );
        fileContent = context.generate(); // nothing to change
        createFile(monitor, file, fileContent);

        // Activator.java
        IPath javaPkgPath = projectPath.append("src");
        for (String part : packageName.split("\\."))
            javaPkgPath = javaPkgPath.append(part);
        path = javaPkgPath.append("Activator.java");
        file = root.getFile(path);
        context = new Templater(
            this.getClass().getClassLoader().getResourceAsStream(
                TEMPLATE_FOLDER_FOO_MANAGER + "src/Activator.java"
            )
        );
        fileContent = context.generate(
            "managerName", managerName,
            "packageName", packageName
        );
        createFile(monitor, file, fileContent);

        // create the Java source files
        String[] sourceFiles = {
            "IJavaManager.java",
            "IJavaScriptManager.java",
            "IManager.java",
            "Manager.java",
            "ManagerFactory.java"
        };
        IPath businessPath = javaPkgPath.append("business");
        for (String sourceFile : sourceFiles) {
            String targetFile = sourceFile.replace(
                "Manager", managerName
            );
            file = root.getFile(path);
            context = new Templater(
                this.getClass().getClassLoader().getResourceAsStream(
                    TEMPLATE_FOLDER_FOO_MANAGER + "src/" + sourceFile
                )
            );
            fileContent = context.generate(
                "managerName", managerName,
                "packageName", packageName,
                "managerNamespace", managerNamespace
            );
            path = businessPath.append(targetFile);
            file = root.getFile(path);
            createFile(monitor, file, fileContent);
        }
    }

    private void createFile(IProgressMonitor monitor, IFile qsarFile, String foo)
            throws CoreException, IOException {
        ByteArrayInputStream bos = new ByteArrayInputStream(foo.getBytes());
        qsarFile.create(bos, true, new SubProgressMonitor(monitor,10));
        bos.close();
    }

    private IPath createFolders(IProgressMonitor monitor, IWorkspaceRoot root,
            IProject project) {
        String[] defaultFolders = {
            "src",
            "META-INF/spring"
        };
        IProgressMonitor subMonitor = new SubProgressMonitor(
            monitor, defaultFolders.length
        );
        IPath projectPath = project.getFullPath();
        for (String folder : defaultFolders) {
            IPath path = projectPath.append(folder);
            IFolder molFolder = root.getFolder(path);
            createFolderHelper(molFolder, monitor);
            subMonitor.worked(1);
        }
        String packageName = this.dataInputPage.getPackage();
        IPath path = projectPath.append("src");
        for (String part : packageName.split("\\."))
            path = path.append(part);
        createFolderHelper(root.getFolder(path), monitor);
        IPath bussinesPath = path.append("business");
        createFolderHelper(root.getFolder(bussinesPath), monitor);
        return projectPath;
    }

    private void addNaturesAndBuilders(IProgressMonitor monitor,
            IProject project) throws CoreException {
        IProjectDescription description = ResourcesPlugin.getWorkspace().
            newProjectDescription(project.getName());
        if(!Platform.getLocation().equals(fFirstPage.getLocationPath()))
            description.setLocation(fFirstPage.getLocationPath());
        description.setNatureIds(new String[] {
            "org.eclipse.pde.PluginNature",
            "org.eclipse.jdt.core.javanature"
        });
        ICommand javaBuilder = description.newCommand();
        javaBuilder.setBuilderName("org.eclipse.jdt.core.javabuilder");
        ICommand mfBuilder = description.newCommand();
        mfBuilder.setBuilderName("org.eclipse.pde.ManifestBuilder");
        description.setBuildSpec(new ICommand[] {
            javaBuilder,
            mfBuilder
        });
        project.create(description, monitor);
        project.open(monitor);
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry[] entries = getClassPathEntries(javaProject);
        javaProject.setRawClasspath(entries, null);
        monitor.worked(10);
    }

    private IClasspathEntry[] getClassPathEntries(IJavaProject project) {
        IClasspathEntry[] entries = new IClasspathEntry[3];
        String executionEnvironment = null;
        ClasspathComputer.setComplianceOptions(
            project,
            ExecutionEnvironmentAnalyzer.getCompliance(executionEnvironment)
        );
        entries[0] = ClasspathComputer.createJREEntry(executionEnvironment);
        entries[1] = ClasspathComputer.createContainerEntry();
        IPath path = project.getProject().getFullPath().append("src/");
        entries[2] = JavaCore.newSourceEntry(path);

        return entries;
    }

    private void createFolderHelper (IFolder folder, IProgressMonitor monitor) {
        try {
            if(!folder.exists()) {
                IContainer parent = folder.getParent();

                if (parent instanceof IFolder && (!((IFolder)parent).exists()))
                    createFolderHelper((IFolder)parent, monitor);

                folder.create(false,true,monitor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}