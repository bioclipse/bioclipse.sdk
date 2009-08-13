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
	Text packageName;

	Status managerNameStatus = noError();
	Status namespaceStatus = noError();
	Status packageNameStatus = noError();

	boolean namespaceChanged = false;

	String initialPluginName = null;

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
		managerName.addListener(SWT.Modify, this);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns -1;
		managerName.setLayoutData(gd);

		new Label(composite,SWT.NONE).setText("package:");
		packageName = new Text(composite,SWT.BORDER);
		packageName.addListener(SWT.Modify, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns -1;
		packageName.setLayoutData(gd);

		new Label(composite,SWT.NONE).setText("Manager namespace:");
		namespace = new Text(composite,SWT.BORDER);
		managerName.addListener(SWT.KeyUp, this);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns -1;
		namespace.setLayoutData(gd);

		updateManagerName();

		setControl(composite);
	}

	@Override
	public boolean canFlipToNextPage() {
		if(getErrorMessage() != null ) return false;
		if( isTextNonEmpty(managerName) &&
		    isTextNonEmpty(namespace) &&
		    isTextNonEmpty(packageName))
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
			break;
		case IStatus.INFO:
			setMessage(message,WizardPage.INFORMATION);
			setErrorMessage(null);
			break;
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
			if(mName.length()<=0) {
				status = new Status(IStatus.INFO,PLUGIN_ID,
							"Manager name must be specified",null);
			}
			if(mName.contains(" "))
				status = new Status(IStatus.ERROR, PLUGIN_ID,
						"Manager name should not container spaces",null);

			if(!namespaceChanged) {
				int end = mName.lastIndexOf("Manager");
				namespace.setText(firstToLower(mName).substring(0,end));
			}
			managerNameStatus = status;
		}
	     if(event.widget == namespace) {
	    	 if(!namespaceChanged) namespaceChanged = true;
	    	 if(namespace.getText().contains(" ")) {
	    		 status = new Status( IStatus.WARNING,PLUGIN_ID,
	    				 "Namespace not contain spaces",null);
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

	private void updateManagerName() {
		if(initialPluginName!=null) {
			int lastIndex = initialPluginName.lastIndexOf('.');
			String mName = initialPluginName.substring(lastIndex+1);
			Character ch = mName.charAt(0);
			StringBuilder n = new StringBuilder( mName.substring(1));
			n.insert(0, Character.toUpperCase(ch)).append("Manager");
			if(managerName.getText().length()==0)
				managerName.setText(n.toString());
			if(packageName.getText().length()==0)
				packageName.setText(initialPluginName+".business");
		}

	}

	private String firstToLower(String s) {
		StringBuilder n = new StringBuilder(s);
		n.setCharAt(0, Character.toLowerCase(n.charAt(0)));
		return n.toString();
	}

	public void setPluginName(String pluginName) {
		initialPluginName = pluginName;
		updateManagerName();
	}

	public String getManager() { return managerName.getText();}
	public String getNamespace() { return namespace.getText();}
	public String getPackage() { return packageName.getText();}
}
