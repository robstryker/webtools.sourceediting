/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.internal.core.search.JavaSearchDocument;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;

/**
 * Wrapper method to set getPath() path to be the path of the compilation unit
 * for the jsp file. (since it's a final method, it needs to be set via
 * constructor)
 * 
 * @author pavery
 */

/* Used to extend SearchDocument */

public class JavaSearchDocumentDelegate extends JavaSearchDocument {

	private JSPSearchDocument fJSPSearchDoc = null;

	public JavaSearchDocumentDelegate(JSPSearchDocument jspSearchDoc) {

		super(jspSearchDoc.getPath(), jspSearchDoc.getParticipant());
		this.fJSPSearchDoc = jspSearchDoc;
	}

	@Override
	public byte[] getByteContents() {

		return this.fJSPSearchDoc.getByteContents();
	}

	@Override
	public char[] getCharContents() {

		return this.fJSPSearchDoc.getCharContents();
	}

	public String getJavaText() {
		return this.fJSPSearchDoc.getJavaText();
	}

	@Override
	public String getEncoding() {

		return this.fJSPSearchDoc.getEncoding();
	}

	public IFile getFile() {

		return this.fJSPSearchDoc.getFile();
	}

	public JSPTranslation getJspTranslation() {

		return this.fJSPSearchDoc.getJSPTranslation();
	}

	public void release() {
		this.fJSPSearchDoc.release();
	}
	
	public boolean isVirtual() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getJavaElement()
	 */
	public IJavaElement getJavaElement() {
		return getJspTranslation().getCompilationUnit();
	}
	
	
}
