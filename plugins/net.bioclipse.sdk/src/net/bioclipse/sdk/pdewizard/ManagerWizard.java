package net.bioclipse.sdk.pdewizard;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

public class ManagerWizard extends NewPluginTemplateWizard {

	protected IFieldData fData;

	public ManagerWizard() {
	}

	@Override
	public void init(IFieldData data) {
		super.init(data);
		fData = data;
		setWindowTitle("Bioclipse Manager Wizard");
	}
	@Override
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] {new ManagerTemplate()};
	}

}
