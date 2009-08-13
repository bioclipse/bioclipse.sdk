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
import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * New Project Wizard that sets up a Bioclipse plugin with a Bioclipse manager.
 */
public class ManagerPluginNewWizard extends Wizard implements INewWizard {

    private WizardNewProjectCreationPage fFirstPage;

    private IWorkbench workbench;
    private IStructuredSelection selection;
    
    private final static String TITLE = "New Bioclipe Manager Plugin Project";

    public ManagerPluginNewWizard() {
        super();
        setWindowTitle(TITLE);
    }

    public void addPages() {
        super.addPages();
        fFirstPage = new WizardNewProjectCreationPage(TITLE);
        fFirstPage.setTitle(TITLE);
        fFirstPage.setDescription("Create a " + TITLE);
        addPage(fFirstPage);
    }

    @Override
    public boolean performFinish() {
        try {
            WorkspaceModifyOperation op =
                new WorkspaceModifyOperation() {

                @Override
                protected void execute(IProgressMonitor monitor)
                throws CoreException, InvocationTargetException,
                InterruptedException {
                    createProject(monitor != null ?
                            monitor : new NullProgressMonitor());

                }
            };
            getContainer().run(false,true,op);
        } catch(InvocationTargetException x) {
            x.printStackTrace();
            return false;
        } catch(InterruptedException x) {
            return false;
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
        setWindowTitle(TITLE);
    }

    protected void createProject(IProgressMonitor monitor) {
        monitor.beginTask("Creating the " + TITLE, 50);
        try {
            //Get WS root
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            monitor.subTask("Creating directories");

            //Create the project
            IProject project = root.getProject(fFirstPage.getProjectName());

            //Add natures and builders
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
            monitor.worked(10);

            //Open project
            project.open(monitor);

            //Create folders
            IPath projectPath = project.getFullPath(),
            molPath = projectPath.append("src");
            IFolder molFolder = root.getFolder(molPath);
            createFolderHelper(molFolder, monitor);
            monitor.worked(10);

            //Create files (qsar.xml)
            monitor.subTask("Creating files");
            IPath qsarPath = projectPath.append("plugin.xml");
            IFile qsarFile = root.getFile(qsarPath);
            ByteArrayInputStream bos = new ByteArrayInputStream("".getBytes());
            qsarFile.create(bos, true, new SubProgressMonitor(monitor,10));
            bos.close();
        } catch(CoreException x) {
            x.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            monitor.done();
        }
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