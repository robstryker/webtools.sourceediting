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
 * $Id: ElemApplyImport.java,v 1.3 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

import javax.xml.transform.TransformerException;

import org.eclipse.wst.xsl.core.compiler.xslt10.res.XSLTErrorResources;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;

/**
 * Implement xsl:apply-imports.
 * 
 * <pre>
 * &lt;!ELEMENT xsl:apply-imports EMPTY&gt;
 * </pre>
 * 
 * @see <a href="http://www.w3.org/TR/xslt#apply-imports">apply-imports in XSLT
 *      Specification</a>
 * @xsl.usage advanced
 */
public class ElemApplyImport extends ElemTemplateElement {
	static final long serialVersionUID = 3764728663373024038L;

	/**
	 * Get an int constant identifying the type of element.
	 * 
	 * @see org.apache.xalan.templates.Constants
	 * 
	 * @return Token ID for xsl:apply-imports element types
	 */
	@Override
	public int getXSLToken() {
		return Constants.ELEMNAME_APPLY_IMPORTS;
	}

	/**
	 * Return the node name.
	 * 
	 * @return Element name
	 */
	@Override
	public String getNodeName() {
		return Constants.ELEMNAME_APPLY_IMPORTS_STRING;
	}

	/**
	 * Execute the xsl:apply-imports transformation.
	 * 
	 * @param transformer
	 *            non-null reference to the the current transform-time state.
	 * 
	 * @throws TransformerException
	 */
	@Override
	public void execute(TransformerImpl transformer)
			throws TransformerException {

		if (transformer.currentTemplateRuleIsNull()) {
			transformer.getMsgMgr().error(this,
					XSLTErrorResources.ER_NO_APPLY_IMPORT_IN_FOR_EACH); // "xsl:apply-imports
																		// not
																		// allowed
																		// in a
																		// xsl:for-each");
		}

		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEvent(this);

		int sourceNode = transformer.getXPathContext().getCurrentNode();
		if (DTM.NULL != sourceNode) {
			// supply the current templated (matched, not named)
			ElemTemplate matchTemplate = transformer.getMatchedTemplate();
			transformer.applyTemplateToNode(this, matchTemplate, sourceNode);
		} else // if(null == sourceNode)
		{
			transformer.getMsgMgr().error(this,
					XSLTErrorResources.ER_NULL_SOURCENODE_APPLYIMPORTS); // "sourceNode
																			// is
																			// null
																			// in
																			// xsl:apply-imports!");
		}
		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEndEvent(this);
	}

	/**
	 * Add a child to the child list. <!ELEMENT xsl:apply-imports EMPTY>
	 * 
	 * @param newChild
	 *            New element to append to this element's children list
	 * 
	 * @return null, xsl:apply-Imports cannot have children
	 */
	@Override
	public ElemTemplateElement appendChild(ElemTemplateElement newChild) {

		error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {
				newChild.getNodeName(), this.getNodeName() }); // "Can not add
																// "
																// +((ElemTemplateElement)newChild).m_elemName
																// +

		// " to " + this.m_elemName);
		return null;
	}
}
