/*******************************************************************************
 * Copyright (c) $copyrightYear$  $copyrightOwner$ <$copyrightOwnerEmail$>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package $packageName$;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;

import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.opentox.business.IOpentoxManager;
import $managerPackage$.business.I$managerName$;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.junit.Assert;
import org.junit.Test;

public abstract class Abstract$managerName$PluginTest
extends AbstractManagerTest {

    protected static I$managerName$ $managerNamespace$;
    
    @Test public void testDoSomething() {
        Assert.fail("This method should test something.");
    }

    public Class<? extends IBioclipseManager> getManagerInterface() {
    	return I$managerName$.class;
    }
}
