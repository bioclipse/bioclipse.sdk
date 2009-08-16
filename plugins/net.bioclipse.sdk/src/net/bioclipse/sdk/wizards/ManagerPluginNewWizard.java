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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * New Project Wizard that sets up a Bioclipse plugin with a Bioclipse manager.
 */
public class ManagerPluginNewWizard extends Wizard implements INewWizard {

    private final static String TEMPLATE_FOLDER_FOO_MANAGER =
        "templates/net.bioclipse.foo.business/";

    private WizardNewProjectCreationPage fFirstPage;
    private ManagerDataInputPage dataInputPage;

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
        addPage(dataInputPage= new ManagerDataInputPage());
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
    	IWizardPage pg=  super.getNextPage(page);
    	if(pg instanceof ManagerDataInputPage) {
    		if(fFirstPage.getProjectName()!=null)
    			((ManagerDataInputPage)pg).setPluginName(fFirstPage.getProjectName());
    	}
    	return pg;
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
                    BusinessProjectCreator creator =
                        new BusinessProjectCreator(fFirstPage, dataInputPage);
                    creator.createProject(monitor != null ?
                            monitor : new NullProgressMonitor());
                    TestProjectCreator testCreator =
                        new TestProjectCreator(fFirstPage, dataInputPage);
                    testCreator.createProject(monitor != null ?
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

}