/*******************************************************************************
 *  Copyright (c) 2016, 2017 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.tests.wizards;

import org.eclipse.pde.ui.tests.ee.ExecutionEnvironmentTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * They run on minimal plugin bundles and dont require the whole SDK ( for
 * hudson gerrit). This class is refactored out of AllNewProjectTests
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ NewFeatureProjectTestCase.class, NewSiteProjectTestCase.class,
	// ConvertProjectToPluginTestCase.class,
	ExecutionEnvironmentTests.class })
public class AllNewProjectMinimalTests {

}
