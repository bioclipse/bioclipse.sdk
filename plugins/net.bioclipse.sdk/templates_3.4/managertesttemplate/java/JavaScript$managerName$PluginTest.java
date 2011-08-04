/*******************************************************************************
 * Copyright (c) $copyrightYear$  $copyrightOwner$ <$copyrightOwnerEmail$>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package $packageName$;

import org.junit.BeforeClass;

public class JavaScript$managerName$PluginTest
    extends Abstract$managerName$PluginTest {

    @BeforeClass public static void setup() {
        $managerNamespace$ = $managerPackage$.Activator.getDefault()
            .getJavaScript$managerName$();
    }

	@Override
	public IBioclipseManager getManager() {
		return $managerNamespace$;
	}
}
