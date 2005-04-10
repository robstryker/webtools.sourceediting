/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Text;

/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @since 1.0
 * 
 */
public interface IDOMText extends IDOMNode, Text {

	/**
	 * NOT API - can be eliminated or moved to ltk level
	 *
	 * Appends the content of the text node
	 * 
	 * @param text -
	 *            the Text to append.
	 */
	void appendText(Text text);

	/**
	 * NOT API - can be eliminated or moved to ltk level
	 * 
	 * Returns true if is not valid.
	 */
	boolean isInvalid();

	/**
	 * Returns true if is entirely white space.
	 * 
	 * This is intened to be better performing that all clients getting the
	 * source, and checking themselves.
	 * 
	 * ISSUE: need to clarify if implementation is pure to "white space" as
	 * per DOM spec?
	 * 
	 * @return true if is entirely white space.
	 */
	boolean isWhitespace();
}
