/* Copyright (c) 2009  Arvid Berg <goglepox@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.sdk.wizards;

import static net.bioclipse.sdk.Activator.PLUGIN_ID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author arvid
 *
 */
public class ManagerDataInputPage extends WizardPage implements Listener{

	Text managerName;
	Text namespace;

	Status managerNameStatus = noError();
	Status namespaceStatus = noError();

	public ManagerDataInputPage() {
		this("Manager data","Input manager info",null);
	}

	public ManagerDataInputPage(String pageName) {
		this(pageName,"",null);
	}

	public ManagerDataInputPage( String pageName,String tile,
								 ImageDescriptor titleImage) {
		super(pageName,tile,titleImage	);
	}

	private static final Status noError() {
		return new Status(IStatus.OK, "not_used", 0, "", null);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent,SWT.NONE);

		GridLayout gl = new GridLayout();
		int numColumns = 4;
		gl.numColumns = numColumns;

		composite.setLayout(gl);

		new Label(composite,SWT.NONE).setText("Manager name:");
		managerName = new Text(composite,SWT.BORDER);
		managerName.addListener(SWT.KeyUp, this);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns -1;
		managerName.setLayoutData(gd);

		new Label(composite,SWT.NONE).setText("Manager namespace:");
		namespace = new Text(composite,SWT.BORDER);
		managerName.addListener(SWT.KeyUp, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns -1;
		namespace.setLayoutData(gd);

		setControl(composite);

	}

	@Override
	public boolean canFlipToNextPage() {
		if(getErrorMessage() != null ) return false;
		if( isTextNonEmpty(managerName) &&
		    isTextNonEmpty(namespace))
			return true;
		else
			return false;
	}

	private static boolean isTextNonEmpty(Text t)
	{
		String s = t.getText();
		if ((s!=null) && (s.trim().length() >0)) return true;
		return false;
	}

	private void updateStatusLine(Status status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
		case IStatus.OK:
			setMessage(message);
				setErrorMessage(null);
			break;
		case IStatus.WARNING:
			setMessage(message, WizardPage.WARNING);
			setErrorMessage(null);
		case IStatus.INFO:
			setMessage(message,WizardPage.INFORMATION);
			setErrorMessage(null);
		default:
			setMessage(null);
			setErrorMessage(message);
			break;
		}
	}

	public void handleEvent(Event event) {
	     Status status = noError();

	     if ((event.widget == managerName)) {
			String mName = managerName.getText();
			if(mName.contains(" "))
				status = new Status(IStatus.ERROR, PLUGIN_ID,
						"Manager name should not container spaces",null);
			if (mName.toLowerCase().endsWith("manager")) {
				status = new Status(IStatus.ERROR, "not_used", 0,
						"Manager name should not end with manager", null);
			}
			managerNameStatus = status;
		}
	     if(event.widget == namespace) {
	    	 if(namespace.getText().length()>10) {
	    		 status = new Status( IStatus.WARNING,PLUGIN_ID,
	    				 "Namespace should be kept short",null);
	    	 }
	    	 namespaceStatus = status;
	     }

	     updateStatusLine(findMostSevere());
	}
	private Status findMostSevere() {
		if(managerNameStatus.matches(IStatus.ERROR)) return managerNameStatus;
		if(namespaceStatus.matches(IStatus.ERROR)) return namespaceStatus;
		if(namespaceStatus.getSeverity() > managerNameStatus.getSeverity())
			return namespaceStatus;
		else
			return managerNameStatus;
	}
}
