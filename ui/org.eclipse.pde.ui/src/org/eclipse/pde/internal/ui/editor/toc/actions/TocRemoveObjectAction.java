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

package org.eclipse.pde.internal.ui.editor.toc.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.core.itoc.ITocConstants;
import org.eclipse.pde.internal.core.toc.TocObject;
import org.eclipse.pde.internal.core.toc.TocTopic;
import org.eclipse.pde.internal.ui.PDEUIMessages;

public class TocRemoveObjectAction extends Action {

	//The object designated for removal
	private TocObject[] fTocObjects;

	//The next object to be selected after the
	//selected object is removed
	private TocObject fObjectToSelect;	

	public TocRemoveObjectAction() {
		setText(PDEUIMessages.TocPage_remove);
		fTocObjects = null;
		fObjectToSelect = null;		
	}

	/**
	 * @param tocObjects the objects to remove
	 */
	public void setToRemove(TocObject[] tocObjects) {
		fTocObjects = tocObjects;
	}

	/**
	 * @param tocObject the object to remove
	 */
	public void setToRemove(TocObject tocObject) {
		fTocObjects = new TocObject[] { tocObject };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if (fTocObjects == null) return;

		for(int i = 0; i < fTocObjects.length; ++i)
		{	if (fTocObjects[i] != null && fTocObjects[i].canBeRemoved()) {
				TocObject parent = fTocObjects[i].getParent();
				if (parent != null && 
						(parent.getType() == ITocConstants.TYPE_TOPIC ||
						parent.getType() == ITocConstants.TYPE_TOC)) {
					// Determine the object to select after the deletion 
					// takes place 
					determineNextSelection(parent, i);
					// Remove the TOC object
					((TocTopic)parent).removeChild(fTocObjects[i]);
				}
			}
		}
	}

	/**
	 * Determine the next object that should be selected
	 * after the designated object has been removed
	 * 
	 * @param parent The parent of the deleted object
	 */
	private void determineNextSelection(TocObject parent, int index) {
		// Select the next sibling
		fObjectToSelect = parent.getNextSibling(fTocObjects[index]);
		if (fObjectToSelect == null) {
			// No next sibling
			// Select the previous sibling
			fObjectToSelect = parent.getPreviousSibling(fTocObjects[index]);
			if (fObjectToSelect == null) {
				// No previous sibling
				// Select the parent
				fObjectToSelect = parent;
			}
		}
	}
	
	/**
	 * @return the object that should be selected
	 * after the current one is removed
	 */
	public TocObject getNextSelection() {
		return fObjectToSelect;
	}	
}
