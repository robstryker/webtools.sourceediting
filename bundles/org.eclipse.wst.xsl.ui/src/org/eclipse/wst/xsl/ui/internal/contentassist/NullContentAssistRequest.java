/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.Node;

/**
 * 
 * @author dcarver
 *
 */
public class NullContentAssistRequest extends AbstractXSLContentAssistRequest {

	/**
	 * A NULL ContentAssistRequest has no proposals.
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public NullContentAssistRequest(Node node, 
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length,
				filter, textViewer);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ICompletionProposal[] getCompletionProposals() {
		return null;
	}

}
