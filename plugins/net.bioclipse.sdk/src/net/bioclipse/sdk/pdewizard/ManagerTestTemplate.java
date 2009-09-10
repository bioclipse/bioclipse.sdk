/* Copyright (c) 2009  Arvid Berg <arvid.berg@farmbio.uu.se>
 *               2009  Egon Willighagen <egonw@user.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.sdk.pdewizard;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import net.bioclipse.sdk.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.core.bundle.BundlePluginBase;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.ui.IHelpContextIds;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

public class ManagerTestTemplate extends OptionTemplateSection {

	private static final String KEY_MANAGER_NAME = "managerName";
    private static final String KEY_MANAGER_PACKAGE = "managerPackage";
    private static final String KEY_COPYRIGHTOWNER = "copyrightOwner";
    private static final String KEY_COPYRIGHTOWNER_EMAIL = "copyrightOwnerEmail";
    private static final String KEY_COPYRIGHTYEAR = "copyrightYear";

	public ManagerTestTemplate() {
		super();
		setPageCount(1);
		createOptions();
	}
	@Override
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0,IHelpContextIds.TEMPLATE_INTRO);
		page.setTitle("Bioclipse Manager Test Template");
		page.setDescription("Create a Bioclipse manager test plugin");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_MANAGER_NAME, "Manager to be Tested", null, 0);
        addOption(KEY_MANAGER_PACKAGE, "Manager's Plugin Root Package", null, 0);
        addOption(KEY_COPYRIGHTOWNER, "Author", null,0);
        addOption(KEY_COPYRIGHTOWNER_EMAIL, "Author's Email", null,0);
        addOption(KEY_COPYRIGHTYEAR, "Copyright Year", null,0);
	}

	@Override
	protected void initializeFields(IFieldData data) {
		String packageName = getFormattedPackageName(data.getId());
		initFields(packageName);
	}
	public void initializeFields(IPluginModelBase model) {
		String packageName = getFormattedPackageName(model.getPluginBase().getId());
		initFields(packageName);
	}

	private void initFields(String packageName) {
			if(packageName == null || packageName.length()<=0) return;

			String managerPackage = "net.bioclipse.some";
            String mName = "SomeManager";
			if (packageName.endsWith(".tests") ||
			    packageName.endsWith(".test")) {
			    int lastDotIndex = packageName.lastIndexOf('.');
			    managerPackage = packageName.substring(0, lastDotIndex);
			    int nextLastDotIndex = managerPackage.lastIndexOf('.');
			    mName = packageName.substring(nextLastDotIndex+1,lastDotIndex);
	            char ch = mName.charAt(0);
	            StringBuilder n = new StringBuilder( mName.substring(1));
	            n.insert(0, Character.toUpperCase(ch)).append("Manager");
	            mName = n.toString();
			}
			
			initializeOption(KEY_MANAGER_NAME, mName);
            initializeOption(KEY_MANAGER_PACKAGE, managerPackage);
			initializeOption(KEY_PACKAGE_NAME, mName);

            DateFormat df = new SimpleDateFormat("yyyy");
            Date today = new Date();
            String year = "" + df.format(today);
            initializeOption(KEY_COPYRIGHTYEAR, year);

            initializeOption(KEY_COPYRIGHTOWNER, "Your Name");

            initializeOption(KEY_COPYRIGHTOWNER_EMAIL, "you@example.com");
	}
	@Override
	protected URL getInstallURL() {
		return Activator.getDefault().getBundle().getEntry("/");
	}

	@Override
	protected String getTemplateDirectory() {
		return super.getTemplateDirectory();
	}

	@Override
	public boolean isDependentOnParentWizard() {
		return true;
	}
	@Override
	public String getSectionId() {
		return "managertesttemplate";
	}

	@Override
	protected ResourceBundle getPluginResourceBundle() {
		return Platform.getResourceBundle(Activator.getDefault().getBundle());
	}

	@Override
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();

		String packageName = getFormattedPackageName(plugin.getId())+".business";
		IPluginExtension extension = createExtension(
					"net.bioclipse.scripting.contribution", true);

		IPluginElement element = factory.createElement(extension);
		element.setName("scriptContribution");
		element.setAttribute("id", packageName
								  + "."
								  + getStringOption(KEY_MANAGER_NAME)
								  );
		element.setAttribute("service", packageName
									   + "."
				  					   + getStringOption(KEY_MANAGER_NAME)
				  					   + "Factory");
		extension.add(element);
		plugin.add(extension);

	}


	public String[] getNewFiles() {
		return new String[0];
	}

	public String getUsedExtensionPoint() {
		return "net.bioclipse.core.net.bioclipse.core.scriptingContribution";
	}

	public IPluginReference[] getDependencies(String schemaVersion) {
		return createDependencies(
				 "org.eclipse.ui",
				 "org.eclipse.core.runtime",
                 "org.eclipse.core.resources",
				 "net.bioclipse.core",
                 "net.bioclipse.core.tests",
				 "net.bioclipse.scripting",
				 "org.springframework.bundle.spring.aop",
				 "net.sf.cglib",
				 "org.springframework.osgi.aopalliance.osgi",
                 "org.junit4",
                 (String)getValue(KEY_MANAGER_PACKAGE)
		);
	}

	private IPluginReference[] createDependencies(String... plugins) {
		List<IPluginReference> deps = new ArrayList<IPluginReference>();
		for(String dep:plugins) {
			deps.add(new PluginReference(dep,null,0));
		}
		return deps.toArray(new IPluginReference[deps.size()]);
	}

	protected String getFormattedPackageName(String id) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch))
					buffer.append(Character.toLowerCase(ch));
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.')
					buffer.append(ch);
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public void execute(IProject project, IPluginModelBase model,
	        IProgressMonitor monitor) throws CoreException {
	    IPluginBase pluginBase = model.createPluginBase();
	    if (pluginBase instanceof BundlePluginBase) {
	        IBundle bundle = ((BundlePluginBase) pluginBase).getBundle();
	        bundle.setHeader("Import-Package", "org.apache.log4j");
	    }
	    super.execute(project, model, monitor);
	}
	
	private String firstToLower(String s) {
		StringBuilder n = new StringBuilder(s);
		n.setCharAt(0, Character.toLowerCase(n.charAt(0)));
		return n.toString();
	}
}
