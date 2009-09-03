package net.bioclipse.sdk.pdewizard;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

public class ManagerTestWizard extends NewPluginTemplateWizard {

	protected IFieldData fData;

	public ManagerTestWizard() {
	}

	@Override
	public void init(IFieldData data) {
		super.init(data);
		fData = data;
		setWindowTitle("Bioclipse Manager Test Wizard");
	}
	@Override
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] {new ManagerTemplate()};
	}

}
