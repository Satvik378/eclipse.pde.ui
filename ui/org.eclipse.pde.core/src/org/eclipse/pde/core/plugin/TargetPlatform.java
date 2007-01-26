/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.core.plugin;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.TargetPlatformHelper;
import org.eclipse.pde.internal.core.UpdateManagerHelper;

/**
 * The central class for the plug-in development target platform. This class cannot
 * be instantiated or subclassed by clients; all functionality is provided 
 * by static methods.  Features include:
 * <ul>
 * <li>the target platform's OS/WS/ARCH</li>
 * <li>the default application and product</li>
 * <li>the available applications and products</li>
 * </ul>
 * <p>
 * @since 3.3
 * </p>
 */
public class TargetPlatform {
	
	private static String PRODUCT_PROPERTY = "eclipse.product"; //$NON-NLS-1$
	private static String APPLICATION_PROPERTY = "eclipse.application"; //$NON-NLS-1$
	
	private static String SDK_PRODUCT = "org.eclipse.sdk.ide"; //$NON-NLS-1$
	private static String PLATFORM_PRODUCT = "org.eclipse.platform.ide"; //$NON-NLS-1$
	
	private static String IDE_APPLICATION = "org.eclipse.ui.ide.workbench"; //$NON-NLS-1$
	
	/**
	 * Returns the target platform's main location as specified on the <b>Environment</b>
	 * tab of the <b>Plug-in Development > Target Platform</b> preference page.
	 *  
	 * @return the target platform's main location
	 */
	public static String getLocation() {
		Preferences preferences = PDECore.getDefault().getPluginPreferences();
		return preferences.getString(ICoreConstants.PLATFORM_PATH);
	}
	
	/**
	 * Returns the location of the default target platform, namely the location 
	 * of the host (running) instance of Eclipse.
	 *  
	 * @return the location of the default target platform
	 */
	public static String getDefaultLocation() {
		URL installURL = Platform.getInstallLocation().getURL();
		IPath path = new Path(installURL.getFile()).removeTrailingSeparator();
		return path.toOSString();
		
	}

	/**
	 * Returns the target operating system as specified on the <b>Environment</b>
	 * tab of the <b>Plug-in Development > Target Platform</b> preference page.
	 *  
	 * @return the target operating system
	 */
	public static String getOS() {
		return getProperty(ICoreConstants.OS, Platform.getOS());
	}

	/**
	 * Returns the target windowing system as specified on the <b>Environment</b>
	 * tab of the <b>Plug-in Development > Target Platform</b> preference page.
	 *  
	 * @return the target windowing system
	 */
	public static String getWS() {
		return getProperty(ICoreConstants.WS, Platform.getWS());
	}

	/**
	 * Returns the target locale as specified on the <b>Environment</b>
	 * tab of the <b>Plug-in Development > Target Platform</b> preference page.
	 *  
	 * @return the target locale
	 */
	public static String getNL() {
		return getProperty(ICoreConstants.NL, Platform.getNL());
	}

	/**
	 * Returns the target system architecture as specified on the <b>Environment</b>
	 * tab of the <b>Plug-in Development > Target Platform</b> preference page.
	 *  
	 * @return the target system architecture
	 */
	public static String getOSArch() {
		return getProperty(ICoreConstants.ARCH, Platform.getOSArch());
	}

	private static String getProperty(String key, String defaultValue) {
		Preferences preferences = PDECore.getDefault().getPluginPreferences();
		String value = preferences.getString(key);
		return value.equals("") ? defaultValue : value; //$NON-NLS-1$
	}
	
	/**
	 * Returns a list of identifiers for all available applications 
	 * (i.e. <code>org.eclipse.core.runtime.applications</code> extensions) declared in the workspace
	 * and target platform plug-ins.
	 * <p>
	 * If a workspace plug-in has the same ID as a plug-in in the target platform, the extensions
	 * declared in the target counterpart are ignored.
	 * </p>
	 * 
	 * @return a list of identifiers for all available applications
	 */
	public static String[] getApplications() {
		return TargetPlatformHelper.getApplicationNames();
	}
	
	/**
	 * Returns a list of identifiers for all available products 
	 * (i.e. <code>org.eclipse.core.runtime.products</code> extensions) declared in the workspace
	 * and target platform plug-ins.
	 * <p>
	 * If a workspace plug-in has the same ID as a plug-in in the target platform, the extensions
	 * declared in the target counterpart are ignored.
	 * </p>
	 * 
	 * @return a list of identifiers for all available products
	 */
	public static String[] getProducts() {
		return TargetPlatformHelper.getProductNames();
	}
	
	/**
	 * Returns the ID for the default product 
	 * (<code>org.eclipse.core.runtime.products</code> extension) for the current target platform,
	 * or <code>null</code> if none can be determined.
	 * 
	 * If any of the 
	 * 
	 * @return the ID for the default product or <code>null</code> if none could be determined
	 */
	public static String getDefaultProduct() {
		Properties config = TargetPlatformHelper.getConfigIniProperties();
		Set set = TargetPlatformHelper.getProductNameSet();
		if (config != null) {
			String product = (String) config.get(PRODUCT_PROPERTY);
			if (product != null && set.contains(product))
				return product;
		}

		if (set.contains(SDK_PRODUCT))
			return SDK_PRODUCT;
		
		return set.contains(PLATFORM_PRODUCT) ? PLATFORM_PRODUCT : null;
	}

	/**
	 * Returns the ID for the default application
	 * (<code>org.eclipse.core.runtime.applications</code> extension) for the current target
	 * application.  If none could be determined, then the default <code>org.eclipse.ui.ide.workbench</code>
	 * application is returned.
	 * 
	 * @return the default application 
	 */
	public static String getDefaultApplication() {
		Properties properties = TargetPlatformHelper.getConfigIniProperties(); 
		String appName = properties != null ? properties.getProperty(APPLICATION_PROPERTY) : null; 
		return appName != null ? appName : IDE_APPLICATION;
	}
	
	/**
	 * Creates a platform configuration to be used when launching an Eclipse
	 * application that uses Update Manager as a configurator
	 * 
	 * @param location the location where the configuration should be persisted
	 * @param plugins the list of plug-ins that make up the configuration
	 * @param brandingPlugin  if specified, a entry for the feature containing the branding plug-in will
	 * 					be created in the platform configuration
	 * 
	 * @throws CoreException an exception is thrown if there was a problem writing the platform
	 * 			configuration file
	 */
	public static void createPlatformConfiguration(
			File location, IPluginModelBase[] plugins, IPluginModelBase brandingPlugin)
			throws CoreException {
		UpdateManagerHelper.createPlatformConfiguration(location, plugins, brandingPlugin);
	}
		
}
