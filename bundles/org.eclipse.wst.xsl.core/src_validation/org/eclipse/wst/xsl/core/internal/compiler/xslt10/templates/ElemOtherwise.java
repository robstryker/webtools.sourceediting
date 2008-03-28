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
 * $Id: ElemOtherwise.java,v 1.2 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

/**
 * Implement xsl:otherwise.
 * 
 * <pre>
 * &lt;!ELEMENT xsl:otherwise %template;&gt;
 * &lt;!ATTLIST xsl:otherwise %space-att;&gt;
 * </pre>
 * 
 * @see <a
 *      href="http://www.w3.org/TR/xslt#section-Conditional-Processing-with-xsl:choose">XXX
 *      in XSLT Specification</a>
 * @xsl.usage advanced
 */
public class ElemOtherwise extends ElemTemplateElement {
	static final long serialVersionUID = 1863944560970181395L;

	/**
	 * Get an int constant identifying the type of element.
	 * 
	 * @see org.apache.xalan.templates.Constants
	 * 
	 * @return The token ID for this element
	 */
	@Override
	public int getXSLToken() {
		return Constants.ELEMNAME_OTHERWISE;
	}

	/**
	 * Return the node name.
	 * 
	 * @return The element's name
	 */
	@Override
	public String getNodeName() {
		return Constants.ELEMNAME_OTHERWISE_STRING;
	}
}
