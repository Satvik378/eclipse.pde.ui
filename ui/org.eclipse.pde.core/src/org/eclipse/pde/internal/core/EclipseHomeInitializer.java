/**
 * Created on Apr 10, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Workbench>Preferences>Java>Templates.
 */
package org.eclipse.pde.internal.core;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ClasspathVariableInitializer;

/**
 *
 */
public class EclipseHomeInitializer extends ClasspathVariableInitializer {

	/**
	 * Constructor for EclipseHomeInitializer.
	 */
	public EclipseHomeInitializer() {
		super();
	}

	/**
	 * @see ClasspathVariableInitializer#initialize(String)
	 */
	public void initialize(String variable) {
		ExternalModelManager.getEclipseHome(new NullProgressMonitor());
	}
}
