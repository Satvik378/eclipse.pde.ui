/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Tests all tags are invalid when parent enum is private or package default
 */
public enum test11 {

	ENUM;
	enum inner1 {

		/**
		 */
		@NoReference
		ENUM;
		/**
		 */
		@NoReference
		public enum inner2 {

		}

	}

}
