/* Copyright (c) 2009  Arvid Berg <arvid.berg@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
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
		return new ITemplateSection[] {new ManagerTestTemplate()};
	}

    @Override
    public String[] getImportPackages() {
        String[] superPkg = super.getImportPackages();
        String[] pkgs = new String[superPkg.length+1];
        System.arraycopy(superPkg, 0, pkgs, 0, superPkg.length);
        pkgs[pkgs.length-1] = "org.apache.log4j";
        return pkgs;
    }

}
