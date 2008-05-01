/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Chris Aniszczyk <caniszczyk@gmail.com>
 *     Rafael Oliveira N�brega <rafael.oliveira@gmail.com> - bug 223738
 *******************************************************************************/
package org.eclipse.pde.internal.ds.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.pde.ds.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(SharedImages.DESC_IMPLEMENTATION,
				createImageDescriptor(SharedImages.DESC_IMPLEMENTATION));
		registry.put(SharedImages.DESC_PROPERTIES,
				createImageDescriptor(SharedImages.DESC_PROPERTIES));
		registry.put(SharedImages.DESC_PROPERTY,
				createImageDescriptor(SharedImages.DESC_PROPERTY));
		registry.put(SharedImages.DESC_PROVIDE,
				createImageDescriptor(SharedImages.DESC_PROVIDE));
		registry.put(SharedImages.DESC_REFERENCE,
				createImageDescriptor(SharedImages.DESC_REFERENCE));
		registry.put(SharedImages.DESC_ROOT,
				createImageDescriptor(SharedImages.DESC_ROOT));
		registry.put(SharedImages.DESC_SERVICE,
				createImageDescriptor(SharedImages.DESC_SERVICE));
		registry.put(SharedImages.DESC_DS,
				createImageDescriptor(SharedImages.DESC_DS));
		
	}
	
	private ImageDescriptor createImageDescriptor(String id) {
		return imageDescriptorFromPlugin(PLUGIN_ID, id);
	}


}
