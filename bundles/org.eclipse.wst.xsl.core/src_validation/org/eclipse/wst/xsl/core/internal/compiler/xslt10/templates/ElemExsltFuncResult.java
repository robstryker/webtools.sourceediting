/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *                    based on work from Apache Xalan 2.7.0
 *******************************************************************************/
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ElemExsltFuncResult.java,v 1.2 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

import javax.xml.transform.TransformerException;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * Handles the EXSLT result element within an EXSLT function element.
 */
public class ElemExsltFuncResult extends ElemVariable {
	static final long serialVersionUID = -3478311949388304563L;
	/*
	 * To keep the binary compatibility put those three private global variables
	 * back, although they are never used in this verison
	 */
	// A flag indicating whether the return result is set
	private boolean m_isResultSet = false;

	// The return result
	private XObject m_result = null;

	// The frame size of the current caller
	private int m_callerFrameSize = 0;

	/**
	 * Generate the EXSLT function return value, and assign it to the variable
	 * index slot assigned for it in ElemExsltFunction compose().
	 * 
	 */
	@Override
	public void execute(TransformerImpl transformer)
			throws TransformerException {
		XPathContext context = transformer.getXPathContext();

		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEvent(this);

		// Verify that result has not already been set by another result
		// element. Recursion is allowed: intermediate results are cleared
		// in the owner ElemExsltFunction execute().
		if (transformer.currentFuncResultSeen()) {
			throw new TransformerException(
					"An EXSLT function cannot set more than one result!");
		}

		int sourceNode = context.getCurrentNode();

		// Set the return value;
		XObject var = getValue(transformer, sourceNode);
		transformer.popCurrentFuncResult();
		transformer.pushCurrentFuncResult(var);

		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEndEvent(this);
	}

	/**
	 * Get an integer representation of the element type.
	 * 
	 * @return An integer representation of the element, defined in the
	 *         Constants class.
	 * @see org.apache.xalan.templates.Constants
	 */
	@Override
	public int getXSLToken() {
		return Constants.EXSLT_ELEMNAME_FUNCRESULT;
	}

	/**
	 * Return the node name, defined in the Constants class.
	 * 
	 * @see org.apache.xalan.templates.Constants
	 * @return The node name
	 * 
	 */
	@Override
	public String getNodeName() {
		return Constants.EXSLT_ELEMNAME_FUNCRESULT_STRING;
	}
}
