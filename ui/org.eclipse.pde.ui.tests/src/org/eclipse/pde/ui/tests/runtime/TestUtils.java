/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.tests.runtime;

import java.util.Collections;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.ui.tests.PDETestsPlugin;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Utility methods for JUnit tests.
 */
@SuppressWarnings("deprecation")
// We use package admin to access bundles during the tests
public class TestUtils {

	private static PackageAdmin packageAdmin;

	public static Bundle getBundle(String symbolicName) {
		if (packageAdmin == null) {
			packageAdmin = PDETestsPlugin.getBundleContext()
					.getService(PDETestsPlugin.getBundleContext().getServiceReference(PackageAdmin.class));
		}
		Bundle[] bundles = packageAdmin.getBundles(symbolicName, null);

		if (bundles != null) {
			return bundles[0];
		}

		return null;
	}

	public static IExtensionPoint getExtensionPoint(String extensionPointId) {
		return Platform.getExtensionRegistry().getExtensionPoint(extensionPointId);
	}

	public static IExtension getExtension(String extensionId) {
		return Platform.getExtensionRegistry().getExtension(extensionId);
	}

	public static String findPath(String path) {
		return FileLocator.find(PDETestsPlugin.getBundleContext().getBundle(), new Path(path), Collections.EMPTY_MAP).toString();
	}
}
