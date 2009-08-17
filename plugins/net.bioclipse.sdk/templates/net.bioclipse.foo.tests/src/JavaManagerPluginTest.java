/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package ${packageName}.tests.business;

import ${packageName}.Activator;

import org.junit.BeforeClass;

public class Java${managerName}PluginTest
    extends Abstract${managerName}PluginTest {

    @BeforeClass public static void setup() {
        ${managerNamespace} = Activator.getDefault().getJava${managerName}();
    }

}
