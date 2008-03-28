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
 * $Id: ProcessorDecimalFormat.java,v 1.2 2008/03/28 02:38:16 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.DecimalFormatProperties;
import org.xml.sax.Attributes;

/**
 * Process xsl:decimal-format by creating a DecimalFormatProperties object and
 * passing it to the stylesheet.
 * 
 * @see org.apache.xalan.templates.Stylesheet#setDecimalFormat
 * @see org.apache.xalan.templates.DecimalFormatProperties
 * @see <a href="http://www.w3.org/TR/xslt#format-number">format-number in XSLT
 *      Specification</a>
 * @xsl.usage internal
 */
class ProcessorDecimalFormat extends XSLTElementProcessor {
	static final long serialVersionUID = -5052904382662921627L;

	/**
	 * Receive notification of the start of an element.
	 * 
	 * @param handler
	 *            The calling StylesheetHandler/TemplatesBuilder.
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed.
	 * @param rawName
	 *            The raw XML 1.0 name (with prefix), or the empty string if raw
	 *            names are not available.
	 * @param attributes
	 *            The attributes attached to the element. If there are no
	 *            attributes, it shall be an empty Attributes object.
	 * @see org.apache.xalan.processor.StylesheetHandler#startElement
	 * @see org.apache.xalan.processor.StylesheetHandler#endElement
	 * @see org.xml.sax.ContentHandler#startElement
	 * @see org.xml.sax.ContentHandler#endElement
	 * @see org.xml.sax.Attributes
	 */
	@Override
	public void startElement(StylesheetHandler handler, String uri,
			String localName, String rawName, Attributes attributes)
			throws org.xml.sax.SAXException {

		DecimalFormatProperties dfp = new DecimalFormatProperties(handler
				.nextUid());

		dfp.setDOMBackPointer(handler.getOriginatingNode());
		dfp.setLocaterInfo(handler.getLocator());

		setPropertiesFromAttributes(handler, rawName, attributes, dfp);
		handler.getStylesheet().setDecimalFormat(dfp);

		handler.getStylesheet().appendChild(dfp);
	}
}
