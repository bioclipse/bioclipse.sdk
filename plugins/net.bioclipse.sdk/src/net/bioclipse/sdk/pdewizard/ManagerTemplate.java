package net.bioclipse.sdk.pdewizard;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import net.bioclipse.sdk.Activator;

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
import org.eclipse.pde.internal.ui.IHelpContextIds;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

public class ManagerTemplate extends OptionTemplateSection {

	private static final String KEY_MANAGER_NAME = "managerName";
	private static final String KEY_NAMESPACE = "managerNamespace";
//	private static final String KEY_PACKAGE_MY_NAME = "packageName";

	public ManagerTemplate() {
		super();
		setPageCount(1);
		createOptions();
	}
	@Override
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0,IHelpContextIds.TEMPLATE_INTRO);
		page.setTitle("Bioclipse Manager Template");
		page.setDescription("Create a Bioclipse manager");
		wizard.addPage(page);
		markPagesAdded();
	}

	private void createOptions() {
		addOption(KEY_MANAGER_NAME, "Manager Name", null, 0);
//		addOption(KEY_PACKAGE_NAME, "Package", null, 0);
		addOption(KEY_NAMESPACE, "Namespace", null,0);

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

			int lastIndex = packageName.lastIndexOf('.');
			String mName = packageName.substring(lastIndex+1);

			char ch = mName.charAt(0);
			StringBuilder n = new StringBuilder( mName.substring(1));
			n.insert(0, Character.toUpperCase(ch)).append("Manager");
			initializeOption(KEY_MANAGER_NAME, n.toString());

			initializeOption(KEY_NAMESPACE, mName);

			initializeOption(KEY_PACKAGE_NAME, packageName);
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
		return "managertemplate";
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
				 "net.bioclipse.core",
				 "net.bioclipse.scripting",
				 "net.bioclipse.ui",
				 "org.springframework.bundle.spring.aop",
				 "net.sf.cglib",
				 "org.springframework.osgi.aopalliance.osgi"
		);
	}

	public String[] getImportPackages() {
        return new String[]{
            "org.apache.log4j"
        };
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

	private String firstToLower(String s) {
		StringBuilder n = new StringBuilder(s);
		n.setCharAt(0, Character.toLowerCase(n.charAt(0)));
		return n.toString();
	}
}
