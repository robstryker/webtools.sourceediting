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

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.search.SearchParticipant;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Created with a .jsp file, but should appear to be a .java file for indexing
 * and searching purposes. There are purposely few fields in this class, and
 * those fields are lightweight since it's possible for many JSP search
 * documents to exist in memory at one time (eg. after importing a project with
 * a large number of JSP files)
 * 
 * @author pavery
 */
public class JSPSearchDocument {

	private String UNKNOWN_PATH = "**path unknown**"; //$NON-NLS-1$
	private String fJSPPathString = UNKNOWN_PATH;
	private String fCUPath = UNKNOWN_PATH;
	private SearchParticipant fParticipant = null;
	private long fLastModifiedStamp;
	private char[] fCachedCharContents;

	/**
	 * @param file
	 * @param participant
	 * @throws CoreException
	 */
	public JSPSearchDocument(String filePath, SearchParticipant participant) {

		this.fJSPPathString = filePath;
		this.fParticipant = participant;
	}

	public SearchParticipant getParticipant() {
		return this.fParticipant;
	}

	/**
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getCharContents()
	 */
	public char[] getCharContents() {

		if (fCachedCharContents == null || isDirty()) {
			JSPTranslation trans = getJSPTranslation();
			fCachedCharContents = trans != null ? trans.getJsText()
					.toCharArray() : new char[0];
			fCUPath = trans.getJavaPath();
		}
		return fCachedCharContents;
	}

	public String getJavaText() {
		return new String(getCharContents());
	}


	/**
	 * It's not recommended for clients to hold on to this JSPTranslation since
	 * it's kind of large. If possible, hold on to the JSPSearchDocument, which
	 * is more of a lightweight proxy.
	 * 
	 * @return the JSPTranslation for the jsp file, or null if it's an
	 *         unsupported file.
	 */
	public final JSPTranslation getJSPTranslation() {
		JSPTranslation translation = null;
		IFile jspFile = getFile();
		if (!JSPSearchSupport.isJsp(jspFile)) {
			return translation;
		}

		IStructuredModel model = null;
		try {
			// get existing model for read, then get document from it
			IModelManager modelManager =  StructuredModelManager.getModelManager();
			if (modelManager != null) {
				model = modelManager.getModelForRead(jspFile);
			}
			// handle unsupported
			if (model instanceof IDOMModel) {
				IDOMModel xmlModel = (IDOMModel) model;
				JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
				xmlModel.getFactoryRegistry().addFactory(factory);
				IDOMDocument doc = xmlModel.getDocument();
				JSPTranslationAdapter adapter = (JSPTranslationAdapter) doc
						.getAdapterFor(IJSPTranslation.class);
				translation = adapter.getJSPTranslation();
			}
		} catch (IOException e) {
			Logger.logException(e);
		} catch (CoreException e) {
			Logger.logException(e);
		} catch (UnsupportedCharsetExceptionWithDetail e) {
			// no need to log this. Just consider it an invalid file for our
			// purposes.
			// Logger.logException(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return translation;
	}

	/**
	 * the path to the Java compilation unit
	 * 
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getPath()
	 */
	public String getPath() {
		// caching the path since it's expensive to get translation
		// important that isDirty() check is second to cache modification stamp
		if (this.fCUPath == null || isDirty() || this.fCUPath == UNKNOWN_PATH) {
			JSPTranslation trans = getJSPTranslation();
			if (trans != null) {
				this.fCUPath = trans.getJavaPath();
				// save since it's expensive to calculate again later
				fCachedCharContents = trans.getJsText().toCharArray();
			}
		}
		return fCUPath != null ? fCUPath : UNKNOWN_PATH;
	}



	public IFile getFile() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath jspPath = new Path(this.fJSPPathString);
		IFile jspFile = root.getFile(jspPath);
		if (!jspFile.exists()) {
			// possibly outside workspace
			jspFile = root.getFileForLocation(jspPath);
		}
		return jspFile;
	}

	private boolean isDirty() {
		boolean modified = false;
		IFile f = getFile();
		if (f != null) {
			long currentStamp = f.getModificationStamp();
			if (currentStamp != fLastModifiedStamp) {
				modified = true;
			}
			fLastModifiedStamp = currentStamp;
		}
		return modified;
	}

	public void release() {
		// nothing to do now since JSPTranslation is created on the fly
	}

	/**
	 * for debugging
	 */
	@Override
	public String toString() {
		return "[JSPSearchDocument:" + this.fJSPPathString + "]"; //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getEncoding()
	 */
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.search.SearchDocument#getByteContents()
	 */
	public byte[] getByteContents() {
		// TODO Auto-generated method stub
		return null;
	}
}