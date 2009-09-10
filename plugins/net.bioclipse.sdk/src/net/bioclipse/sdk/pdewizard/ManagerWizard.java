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
	
	@Override
	public String[] getImportPackages() {
	    String[] pkgs = {
	       "org.apache.log4j"
	    };
	    return pkgs;
	}
	
}
