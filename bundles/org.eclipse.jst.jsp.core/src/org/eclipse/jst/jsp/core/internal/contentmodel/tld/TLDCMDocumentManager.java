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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexListener;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecordEvent;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandlerExtension;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class TLDCMDocumentManager implements ITaglibIndexListener {

	protected class DirectiveStructuredDocumentRegionHandler implements StructuredDocumentRegionHandler, StructuredDocumentRegionHandlerExtension {

		/**
		 * Adds a block tagname (fully namespace qualified) into the list of
		 * block tag names for the parser. The marker
		 * IStructuredDocumentRegion along with position cues during reparses
		 * allow the JSPSourceParser to enable/ignore the tags as blocks.
		 */
		protected void addBlockTag(String tagnameNS, IStructuredDocumentRegion marker) {
			if (getParser() == null)
				return;
			if (getParser().getBlockMarker(tagnameNS) == null) {
				getParser().addBlockMarker(new BlockMarker(tagnameNS, marker, DOMRegionContext.BLOCK_TEXT, true, false));
				if (_debug) {
					System.out.println("TLDCMDocumentManager added block marker: " + tagnameNS + "@" + marker.getStartOffset()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}

		protected void addTaglibTracker(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tldCMDocument) {
			getTaglibTrackers().add(new TaglibTracker(uri, prefix, tldCMDocument, anchorStructuredDocumentRegion));
		}

		/**
		 * Enables a TLD owning the given prefix loaded from the given URI at
		 * the anchorStructuredDocumentRegion. The list of
		 * additionalCMDocuments will claim to not know any of its tags at
		 * positions earlier than that IStructuredDocumentRegion's position.
		 * 
		 * For taglib directives, the taglib is the anchor while taglibs
		 * registered through include directives use the parent document's
		 * include directive as their anchor.
		 * 
		 * @param prefix
		 * @param uri
		 * @param anchorStructuredDocumentRegion
		 */
		protected void enableTaglibFromURI(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion) {
			if (prefix == null || uri == null || bannedPrefixes.contains(prefix))
				return;
			// Try to load the CMDocument for this URI
			CMDocument tld = getCMDocument(uri);
			if (tld == null || !(tld instanceof TLDDocument)) {
				if (_debug) {
					System.out.println("TLDCMDocumentManager failed to create a CMDocument for " + uri); //$NON-NLS-1$
				}
				return;
			}
			registerTaglib(prefix, uri, anchorStructuredDocumentRegion, tld);
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for " + uri + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
		}

		/**
		 * Enables a TLD owning the given prefix loaded from the given URI at
		 * the anchorStructuredDocumentRegion. The list of
		 * additionalCMDocuments will claim to not know any of its tags at
		 * positions earlier than that IStructuredDocumentRegion's position.
		 * 
		 * For taglib directives, the taglib is the anchor while taglibs
		 * registered through include directives use the parent document's
		 * include directive as their anchor.
		 * 
		 * @param prefix
		 * @param uri
		 * @param taglibStructuredDocumentRegion
		 */
		protected void enableTagsInDir(String prefix, String tagdir, IStructuredDocumentRegion taglibStructuredDocumentRegion) {
			if (prefix == null || tagdir == null || bannedPrefixes.contains(prefix))
				return;
			if (_debug) {
				System.out.println("TLDCMDocumentManager enabling tags from directory" + tagdir + " for prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
			// Try to load the CMDocument for this URI
			CMDocument tld = getImplicitCMDocument(tagdir);
			if (tld == null || !(tld instanceof TLDDocument)) {
				if (_debug) {
					System.out.println("TLDCMDocumentManager failed to create a CMDocument for director " + tagdir); //$NON-NLS-1$
				}
				return;
			}
			registerTaglib(prefix, tagdir, taglibStructuredDocumentRegion, tld);
			if (_debug) {
				System.out.println("TLDCMDocumentManager registered a tracker for directory" + tagdir + " with prefix " + prefix); //$NON-NLS-2$//$NON-NLS-1$
			}
		}

		public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			// could test > 1, but since we only care if there are 8 (<%@,
			// taglib, uri, =, where, prefix, =, what) [or 4 for includes]
			if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = aCoreStructuredDocumentRegion.getRegions().get(1);
				try {
					if (getParser() == null) {
						Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else {
						boolean taglibDetected = false;
						boolean taglibDirectiveDetected = false;
						boolean includeDetected = false;
						boolean includeDirectiveDetected = false;
						int startOffset = aCoreStructuredDocumentRegion.getStartOffset(name);
						int textLength = name.getTextLength();

						if (getParser() != null) {
							taglibDetected = getParser().regionMatches(startOffset, textLength, JSP12TLDNames.TAGLIB);
							taglibDirectiveDetected = getParser().regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							includeDetected = getParser().regionMatches(startOffset, textLength, JSP12TLDNames.INCLUDE);
							includeDirectiveDetected = getParser().regionMatches(startOffset, textLength, JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
						}
						else {
							// old fashioned way
							String directiveName = getParser().getText(startOffset, textLength);
							taglibDetected = directiveName.equals(JSP12TLDNames.TAGLIB);
							taglibDirectiveDetected = directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB);
							includeDetected = directiveName.equals(JSP12TLDNames.INCLUDE);
							includeDirectiveDetected = directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE);
						}
						if (taglibDetected || taglibDirectiveDetected) {
							processTaglib(aCoreStructuredDocumentRegion);
						}
						else if (includeDetected || includeDirectiveDetected) {
							processInclude(aCoreStructuredDocumentRegion);
						}
					}
				}
				catch (StringIndexOutOfBoundsException sioobExc) {
					// do nothing
				}
			}
			// could test > 1, but since we only care if there are 5 (<,
			// jsp:root, xmlns:prefix, =, where)
			else if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME) {
				if (getParser() == null) {
					Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					processJSPRoot(aCoreStructuredDocumentRegion);
				}
			}
		}

		protected void processInclude(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			processInclude(aCoreStructuredDocumentRegion, aCoreStructuredDocumentRegion, getParser());
		}

		/**
		 * Process an include directive found by the textSource parser and
		 * anchor any taglibs found within at the
		 * anchorStructuredDocumentRegion. Includes use the including file as
		 * the point of reference, not necessarily the "top" file.
		 */
		protected void processInclude(IStructuredDocumentRegion includeStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = includeStructuredDocumentRegion.getRegions();
			String includedFile = null;
			boolean isFilename = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						if (textSource.getText(includeStructuredDocumentRegion.getStartOffset(region), region.getTextLength()).equals(JSP12TLDNames.FILE)) {
							isFilename = true;
						}
						else {
							isFilename = false;
						}
					}
					else if (isFilename && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						includedFile = textSource.getText(includeStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						isFilename = false;
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				includedFile = null;
			}

			if (includedFile != null) {
				// strip any extraneous quotes and white space
				includedFile = StringUtils.strip(includedFile).trim();
				IPath filePath = null;
				if (includedFile.startsWith("/")) { //$NON-NLS-1$
					IPath contextRoot = TaglibIndex.getContextRoot(TaglibController.getFileBuffer(TLDCMDocumentManager.this).getLocation());
					filePath = contextRoot.append(includedFile);
				}
				else {
					if (getIncludes().isEmpty())
						filePath = TaglibController.getFileBuffer(TLDCMDocumentManager.this).getLocation().removeLastSegments(1).append(includedFile);
					else
						filePath = ((IPath) getIncludes().peek()).removeLastSegments(1).append(includedFile);
				}
				// check for "loops"
				if (filePath != null && !getIncludes().contains(filePath) && !filePath.equals(TaglibController.getFileBuffer(TLDCMDocumentManager.this).getLocation())) {
					/*
					 * Prevent slow performance when editing scriptlet part of
					 * the JSP by only processing includes if they've been
					 * modified. The IncludeHelper remembers any CMDocuments
					 * created from the files it parses. Caching the URI and
					 * prefix/tagdir allows us to just enable the CMDocument
					 * when the previously parsed files.
					 * 
					 * REMAINING PROBLEM: fTLDCMReferencesMap does not map
					 * from a fragment's path and also include all of the CM
					 * references in fragments that *it* includes. The
					 * fragments that it includes won't have its CM references
					 * loaded, but then we'd need to record the URI and
					 * location of the included fragment to resolve them
					 * correctly, modifying enableTaglib() to also take a base
					 * path and resolve the URI appropriately.
					 */
					if (hasAnyIncludeBeenModified(filePath)) {
						getIncludes().push(filePath);
						if (getParser() != null) {
							IncludeHelper includeHelper = new IncludeHelper(anchorStructuredDocumentRegion, getParser());
							includeHelper.parse(filePath);
							List references = includeHelper.taglibReferences;
							fTLDCMReferencesMap.put(filePath, references);
							/*
							 * TODO: walk up the include hierarchy and add
							 * these references to each of the parents.
							 */
						}
						else
							Logger.log(Logger.WARNING, "Warning: parser text was requested by " + getClass().getName() + " but none was available; taglib support disabled"); //$NON-NLS-1$ //$NON-NLS-2$
						getIncludes().pop();
					}
					else {
						// Add from that saved list of uris/prefixes/documents
						List references = (List) fTLDCMReferencesMap.get(filePath);
						for (int i = 0; references != null && i < references.size(); i++) {
							TLDCMDocumentReference reference = (TLDCMDocumentReference) references.get(i);
							/*
							 * The uri might not be resolved properly if
							 * relative to the JSP fragment.
							 */
							enableTaglibFromURI(reference.prefix, reference.uri, includeStructuredDocumentRegion);

						}
					}
				}
				else {
					if (Debug.debugTokenizer)
						System.out.println("LOOP IN @INCLUDES FOUND: " + filePath); //$NON-NLS-1$
				}
			}
		}

		// Pulls the URI and prefix from the given jsp:root
		// IStructuredDocumentRegion and
		// makes sure the tags are known.
		protected void processJSPRoot(IStructuredDocumentRegion jspRootStructuredDocumentRegion) {
			processJSPRoot(jspRootStructuredDocumentRegion, jspRootStructuredDocumentRegion, getParser());
		}

		protected void processJSPRoot(IStructuredDocumentRegion taglibStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = taglibStructuredDocumentRegion.getRegions();
			String uri = null;
			String prefix = null;
			boolean taglib = false;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						String name = textSource.getText(taglibStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						if (name.startsWith(XMLNS)) { //$NON-NLS-1$
							prefix = name.substring(XMLNS_LENGTH);
							if (!bannedPrefixes.contains(prefix))
								taglib = true;
						}
						else {
							prefix = null;
							taglib = false;
						}
					}
					else if (taglib && region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						uri = textSource.getText(taglibStructuredDocumentRegion.getStartOffset(region), region.getTextLength());
						if (uri != null && prefix != null && (StringUtils.strip(uri).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
							if (anchorStructuredDocumentRegion == null)
								enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), taglibStructuredDocumentRegion);
							else
								enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), anchorStructuredDocumentRegion);
							uri = null;
							prefix = null;
						}
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				uri = null;
				prefix = null;
			}
		}

		protected void processTaglib(IStructuredDocumentRegion taglibStructuredDocumentRegion) {
			processTaglib(taglibStructuredDocumentRegion, taglibStructuredDocumentRegion, getParser());
		}

		/**
		 * Pulls the URI and prefix from the given taglib directive
		 * IStructuredDocumentRegion and makes sure the tags are known.
		 */
		protected void processTaglib(IStructuredDocumentRegion taglibStructuredDocumentRegion, IStructuredDocumentRegion anchorStructuredDocumentRegion, JSPSourceParser textSource) {
			ITextRegionList regions = taglibStructuredDocumentRegion.getRegions();
			String uri = null;
			String prefix = null;
			String tagdir = null;
			String attrName = null;
			try {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					// remember attribute name
					int startOffset = taglibStructuredDocumentRegion.getStartOffset(region);
					int textLength = region.getTextLength();
					if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						// String name = textSource.getText(startOffset,
						// textLength);
						if (textSource.regionMatches(startOffset, textLength, JSP11TLDNames.PREFIX)) {
							attrName = JSP11TLDNames.PREFIX;
						}
						else if (textSource.regionMatches(startOffset, textLength, JSP12TLDNames.URI)) {
							attrName = JSP11TLDNames.URI;
						}
						else if (textSource.regionMatches(startOffset, textLength, JSP20TLDNames.TAGDIR)) {
							attrName = JSP20TLDNames.TAGDIR;
						}
						else {
							attrName = null;
						}
					}
					// process value
					else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						if (JSP11TLDNames.PREFIX.equals(attrName))
							prefix = textSource.getText(startOffset, textLength);
						else if (JSP11TLDNames.URI.equals(attrName))
							uri = textSource.getText(startOffset, textLength);
						else if (JSP20TLDNames.TAGDIR.equals(attrName))
							tagdir = textSource.getText(startOffset, textLength);
					}
				}
			}
			catch (StringIndexOutOfBoundsException sioobExc) {
				// nothing to be done
				uri = null;
				prefix = null;
			}
			if (uri != null && prefix != null && (StringUtils.strip(uri).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
				if (anchorStructuredDocumentRegion == null)
					enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), taglibStructuredDocumentRegion);
				else
					enableTaglibFromURI(StringUtils.strip(prefix), StringUtils.strip(uri), anchorStructuredDocumentRegion);
			}
			else if (tagdir != null && prefix != null && (StringUtils.strip(tagdir).length() > 0) && (StringUtils.strip(prefix).length() > 0)) {
				if (anchorStructuredDocumentRegion == null)
					enableTagsInDir(StringUtils.strip(prefix), StringUtils.strip(tagdir), taglibStructuredDocumentRegion);
				else
					enableTagsInDir(StringUtils.strip(prefix), StringUtils.strip(tagdir), anchorStructuredDocumentRegion);
			}
		}

		private void registerTaglib(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tld) {
			CMNamedNodeMap elements = tld.getElements();
			/*
			 * Go through the CMDocument for any tags that must be marked as
			 * block tags starting at the anchoring IStructuredDocumentRegion.
			 * As the document is edited and the IStructuredDocumentRegion
			 * moved around, the block tag enablement will automatically
			 * follow it.
			 */
			for (int i = 0; i < elements.getLength(); i++) {
				TLDElementDeclaration ed = (TLDElementDeclaration) elements.item(i);
				if (ed.getBodycontent() == JSP12TLDNames.CONTENT_TAGDEPENDENT)
					addBlockTag(prefix + ":" + ed.getNodeName(), anchorStructuredDocumentRegion); //$NON-NLS-1$
			}
			/*
			 * Since modifications to StructuredDocumentRegions adjacent to a
			 * taglib directive can cause that IStructuredDocumentRegion to be
			 * reported, filter out any duplicated URIs. When the taglib is
			 * actually modified, a full rebuild will occur and no duplicates
			 * will/should be found.
			 */
			boolean doTrack = true;
			List trackers = getTaglibTrackers();
			for (int i = 0; i < trackers.size(); i++) {
				TaglibTracker tracker = (TaglibTracker) trackers.get(i);
				if (tracker.getPrefix().equals(prefix) && tracker.getURI().equals(uri)) {
					doTrack = false;
				}
			}
			if (doTrack) {
				addTaglibTracker(prefix, uri, anchorStructuredDocumentRegion, tld);
			}
		}

		private void resetBlockTags() {
			if (getParser() == null)
				return;
			Iterator names = getParser().getBlockMarkers().iterator();
			while (names.hasNext()) {
				BlockMarker marker = (BlockMarker) names.next();
				if (!marker.isGlobal() && marker.getContext() == DOMRegionContext.BLOCK_TEXT) {
					if (_debug) {
						System.out.println("TLDCMDocumentManager removing block tag named: " + marker.getTagName()); //$NON-NLS-1$
					}
					names.remove();
				}
			}
		}

		public void resetNodes() {
			if (Debug.debugTaglibs)
				System.out.println(getClass().getName() + ": resetting"); //$NON-NLS-1$
			getIncludes().clear();
			resetBlockTags();
			resetTaglibTrackers();
		}

		public void setStructuredDocument(IStructuredDocument newDocument) {
			Assert.isTrue(newDocument != null, "null document"); //$NON-NLS-1$
			Assert.isTrue(newDocument.getParser() != null, "null document parser"); //$NON-NLS-1$
			Assert.isTrue(newDocument.getParser() instanceof JSPSourceParser, "can only listen to document with a JSPSourceParser"); //$NON-NLS-1$
			getSourceParser().removeStructuredDocumentRegionHandler(this);
			setSourceParser((JSPSourceParser) newDocument.getParser());
			getSourceParser().addStructuredDocumentRegionHandler(this);
		}
	}

	protected class IncludeHelper extends DirectiveStructuredDocumentRegionHandler {
		protected IStructuredDocumentRegion fAnchor = null;
		protected JSPSourceParser fLocalParser = null;
		protected JSPSourceParser fParentParser = null;
		List taglibReferences = null;

		public IncludeHelper(IStructuredDocumentRegion anchor, JSPSourceParser rootParser) {
			super();
			fAnchor = anchor;
			fParentParser = rootParser;
			taglibReferences = new ArrayList(0);
		}

		protected void addTaglibTracker(String prefix, String uri, IStructuredDocumentRegion anchorStructuredDocumentRegion, CMDocument tldCMDocument) {
			super.addTaglibTracker(prefix, uri, anchorStructuredDocumentRegion, tldCMDocument);
			TLDCMDocumentReference reference = new TLDCMDocumentReference();
			reference.prefix = prefix;
			reference.uri = uri;
			taglibReferences.add(reference);
		}

		private String detectCharset(IFile file) {
			if (file.getType() == IResource.FILE && file.isAccessible()) {
				IContentDescription d = null;
				try {
					// optimized description lookup, might not succeed
					d = file.getContentDescription();
					if (d != null)
						return d.getCharset();
				}
				catch (CoreException e) {
					// should not be possible given the accessible and file
					// type
					// check above
				}
				InputStream contents = null;
				try {
					contents = file.getContents();
					IContentDescription description = Platform.getContentTypeManager().getDescriptionFor(contents, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
					if (description != null) {
						return description.getCharset();
					}
				}
				catch (IOException e) {
					// will try to cleanup in finally
				}
				catch (CoreException e) {
					Logger.logException(e);
				}
				finally {
					if (contents != null) {
						try {
							contents.close();
						}
						catch (Exception e) {
							// not sure how to recover at this point
						}
					}
				}
			}
			return ResourcesPlugin.getEncoding();
		}

		protected String getContents(IPath filePath) {
			StringBuffer s = new StringBuffer();
			IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
			if (iFile != null && iFile.exists()) {
				String charset = detectCharset(iFile);
				InputStream contents = null;
				try {
					contents = iFile.getContents();
					Reader reader = new InputStreamReader(contents, charset);
					char[] readBuffer = new char[2048];
					int n = reader.read(readBuffer);
					while (n > 0) {
						s.append(readBuffer, 0, n);
						n = reader.read(readBuffer);
					}
				}
				catch (Exception e) {
					if (Debug.debugStructuredDocument)
						Logger.log(Logger.WARNING, "An exception occured while scanning " + filePath, e); //$NON-NLS-1$
				}
				finally {
					try {
						if (contents != null) {
							contents.close();
						}
					}
					catch (Exception e) {
						// nothing to do
					}
				}
			}
			else {
				int c = 0;
				int length = 0;
				int count = 0;
				File file = null;
				FileInputStream fis = null;
				try {
					file = new File(filePath.toString());
					length = (int) file.length();
					fis = new FileInputStream(file);
					while (((c = fis.read()) >= 0) && (count < length)) {
						count++;
						s.append((char) c);
					}
				}
				catch (FileNotFoundException e) {
					if (Debug.debugStructuredDocument)
						System.out.println("File not found : \"" + filePath + "\""); //$NON-NLS-2$//$NON-NLS-1$
				}
				catch (ArrayIndexOutOfBoundsException e) {
					if (Debug.debugStructuredDocument)
						System.out.println("Usage wrong: specify inputfile"); //$NON-NLS-1$
					//$NON-NLS-1$
				}
				catch (IOException e) {
					if (Debug.debugStructuredDocument)
						System.out.println("An I/O error occured while scanning :"); //$NON-NLS-1$
					//$NON-NLS-1$
				}
				catch (Exception e) {
					if (Debug.debugStructuredDocument)
						e.printStackTrace();
				}
				finally {
					try {
						if (fis != null) {
							fis.close();
						}
					}
					catch (Exception e) {
						// nothing to do
					}
				}
			}
			return s.toString();
		}

		public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion) {
			// could test > 1, but since we only care if there are 8 (<%@,
			// taglib, uri, =, where, prefix, =, what)
			if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 1 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				ITextRegion name = aCoreStructuredDocumentRegion.getRegions().get(1);
				try {
					String directiveName = fLocalParser.getText(aCoreStructuredDocumentRegion.getStartOffset(name), name.getTextLength());
					if (directiveName.equals(JSP12TLDNames.TAGLIB) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB)) {
						processTaglib(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
					}
					if (directiveName.equals(JSP12TLDNames.INCLUDE) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE)) {
						processInclude(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
					}
				}
				catch (StringIndexOutOfBoundsException sioobExc) {
					// do nothing
				}
			}
			// could test > 1, but since we only care if there are 5 (<,
			// jsp:root, xmlns:prefix, =, where)
			else if (aCoreStructuredDocumentRegion.getNumberOfRegions() > 4 && aCoreStructuredDocumentRegion.getRegions().get(1).getType() == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME) {
				processJSPRoot(aCoreStructuredDocumentRegion, fAnchor, fLocalParser);
			}
		}

		/**
		 * @param path -
		 *            the fullpath for the resource to be parsed
		 */
		void parse(IPath path) {
			JSPSourceParser p = new JSPSourceParser();
			fLocalParser = p;
			List blockTags = fParentParser.getBlockMarkers();
			String s = getContents(path);
			fLocalParser.addStructuredDocumentRegionHandler(this);
			fLocalParser.reset(s);
			for (int i = 0; i < blockTags.size(); i++) {
				BlockMarker marker = (BlockMarker) blockTags.get(i);
				fLocalParser.addBlockMarker(new BlockMarker(marker.getTagName(), null, marker.getContext(), marker.isCaseSensitive()));
			}
			// force parse
			fLocalParser.getDocumentRegions();
			fLocalParser = null;
		}

		public void resetNodes() {
		}
	}

	/**
	 * An entry in the shared cache map
	 */
	static class TLDCacheEntry {
		CMDocument document;
		int referenceCount;
	}

	private static class TLDCMDocumentDescriptor {
		Object cacheKey;
		CMDocument document;

		TLDCMDocumentDescriptor() {
			super();
		}
	}

	private class TLDCMDocumentReference {
		String prefix;
		String uri;
	}

	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/manager")); //$NON-NLS-1$ //$NON-NLS-2$
	static final boolean _debugCache = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/cache")); //$NON-NLS-1$ //$NON-NLS-2$
	// will hold the prefixes banned by the specification; taglibs may not use
	// them
	protected static List bannedPrefixes = null;

	private static Hashtable fCache = null;
	static final String XMLNS = "xmlns:"; //$NON-NLS-1$ 

	static final int XMLNS_LENGTH = XMLNS.length();

	static {
		bannedPrefixes = new ArrayList(7);
		bannedPrefixes.add("jsp"); //$NON-NLS-1$
		bannedPrefixes.add("jspx"); //$NON-NLS-1$
		bannedPrefixes.add("java"); //$NON-NLS-1$
		bannedPrefixes.add("javax"); //$NON-NLS-1$
		bannedPrefixes.add("servlet"); //$NON-NLS-1$
		bannedPrefixes.add("sun"); //$NON-NLS-1$
		bannedPrefixes.add("sunw"); //$NON-NLS-1$
	}

	/**
	 * Gets all of the known documents.
	 * 
	 * @return Returns a Hashtable of either TLDCacheEntrys or WeakReferences
	 *         to TLD CMDocuments
	 */
	public static Hashtable getSharedDocumentCache() {
		if (fCache == null) {
			fCache = new Hashtable();
		}
		return fCache;
	}


	public static Object getUniqueIdentifier(ITaglibRecord reference) {
		Object identifier = null;
		switch (reference.getRecordType()) {
			case (ITaglibRecord.TLD) : {
				ITLDRecord record = (ITLDRecord) reference;
				identifier = record.getPath();
			}
				break;
			case (ITaglibRecord.JAR) : {
				IJarRecord record = (IJarRecord) reference;
				identifier = record.getLocation();
			}
				break;
			case (ITaglibRecord.TAGDIR) : {
				// TagDirRecord record = (TagDirRecord) reference;
				// document =
				// buildCMDocumentFromDirectory(record.getLocation().toFile());
			}
				break;
			case (ITaglibRecord.URL) : {
				IURLRecord record = (IURLRecord) reference;
				identifier = record.getURL();
			}
				break;
			default :
				identifier = reference;
				break;
		}
		return identifier;
	}
	private CMDocumentFactoryTLD fCMDocumentBuilder = null;

	private DirectiveStructuredDocumentRegionHandler fDirectiveHandler = null;

	/**
	 * The locally-know list of CMDocuments
	 */
	private Hashtable fDocuments = null;

	// timestamp cache to prevent excessive reparsing
	// of included files
	// IPath (filepath) > Long (modification stamp)
	HashMap fInclude2TimestampMap = new HashMap();

	private Stack fIncludes = null;

	private JSPSourceParser fParser = null;

	private List fTaglibTrackers = null;

	Map fTLDCMReferencesMap = new HashMap();

	public TLDCMDocumentManager() {
		super();
	}

	public void clearCache() {
		if (_debugCache) {
			System.out.println("TLDCMDocumentManager cleared its private CMDocument cache"); //$NON-NLS-1$
		}
		for (Iterator iter = getDocuments().keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			synchronized (getSharedDocumentCache()) {
				Object o = getSharedDocumentCache().get(key);
				if (o instanceof TLDCacheEntry) {
					TLDCacheEntry entry = (TLDCacheEntry) o;
					entry.referenceCount--;
					if (entry.referenceCount <= 0) {
						getSharedDocumentCache().put(key, new WeakReference(entry.document));
					}
				}
			}
		}
	}

	/**
	 * Derives an unique cache key for the give URI. The URI is "resolved"
	 * and a unique value generated from the result. This ensures that two
	 * different relative references from different files do not have overlapping
	 * TLD records in the shared cache if they don't resolve to the same TLD.
	 * 
	 * @param uri
	 * @return
	 */
	protected Object getCacheKey(String uri) {
		ITaglibRecord record = TaglibIndex.resolve(getCurrentParserPath().toString(), uri, false);
		if (record != null) {
			return getUniqueIdentifier(record);
		}
		String location = URIResolverPlugin.createResolver().resolve(getCurrentBaseLocation().toString(), null, uri);
		return location;
	}

	/**
	 * Return the CMDocument at the uri (cached)
	 */
	protected CMDocument getCMDocument(String uri) {
		if (uri == null || uri.length() == 0)
			return null;
		String reference = uri;
		/**
		 * JSP 1.2 Specification, section 5.2.2 jsp-1_2-fcs-spec.pdf, page 87
		 */
		String URNprefix = "urn:jsptld:"; //$NON-NLS-1$
		if (reference.startsWith(URNprefix)) {
			/**
			 * @see section 7.3.2
			 */
			if (reference.length() > URNprefix.length())
				reference = reference.substring(11);
		}
		else {
			/**
			 * @see section 7.3.6
			 */
		}
		Object cacheKey = getCacheKey(reference);
		CMDocument doc = (CMDocument) getDocuments().get(cacheKey);
		if (doc == null) {
			/*
			 * If hasn't been moved into the local table, do so and increment
			 * the count. A local URI reference can be different depending on
			 * the file from which it was referenced. Use a computed key to
			 * keep them straight.
			 */
			Object o = getSharedDocumentCache().get(cacheKey);
			if (o != null) {
				if (o instanceof TLDCacheEntry) {
					TLDCacheEntry entry = (TLDCacheEntry) o;
					if (_debugCache) {
						System.out.println("TLDCMDocument cache hit on " + cacheKey);
					}
					doc = entry.document;
					entry.referenceCount++;
				}
				else if (o instanceof WeakReference) {
					doc = (CMDocument) ((WeakReference) o).get();
					if (doc != null) {
						TLDCacheEntry entry = new TLDCacheEntry();
						entry.document = doc;
						entry.referenceCount = 1;
						getSharedDocumentCache().put(cacheKey, entry);
					}
				}
			}
			/* No document was found cached, create a new one and share it */
			if (doc == null) {
				if (_debugCache) {
					System.out.println("TLDCMDocument cache miss on " + cacheKey);
				}
				TLDCMDocumentDescriptor descriptor = loadTaglib(reference);
				if (descriptor != null) {
					TLDCacheEntry entry = new TLDCacheEntry();
					doc = entry.document = descriptor.document;
					entry.referenceCount = 1;
					getSharedDocumentCache().put(cacheKey, entry);
				}
			}
			if (doc != null) {
				getDocuments().put(cacheKey, doc);
			}
		}
		return doc;
	}

	/**
	 * Gets the cMDocumentBuilder.
	 * 
	 * @return Returns a CMDocumentFactoryTLD, since it has more builder
	 *         methods
	 */
	protected CMDocumentFactoryTLD getCMDocumentBuilder() {
		if (fCMDocumentBuilder == null)
			fCMDocumentBuilder = new CMDocumentFactoryTLD();
		return fCMDocumentBuilder;
	}

	public List getCMDocumentTrackers(int offset) {
		List validDocs = new ArrayList();
		Iterator alldocs = getTaglibTrackers().iterator();
		while (alldocs.hasNext()) {
			TaglibTracker aTracker = (TaglibTracker) alldocs.next();
			if (aTracker.getStructuredDocumentRegion().getStartOffset() < offset || offset < 0) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	public List getCMDocumentTrackers(String prefix, int offset) {
		List validDocs = new ArrayList();
		Iterator alldocs = getTaglibTrackers().iterator();
		while (alldocs.hasNext()) {
			TaglibTracker aTracker = (TaglibTracker) alldocs.next();
			if ((aTracker.getStructuredDocumentRegion().getStartOffset() < offset || offset < 0) && aTracker.getPrefix().equals(prefix)) {
				validDocs.add(aTracker);
			}
		}
		return validDocs;
	}

	/**
	 * Return the filesystem location in the current parser. This method is
	 * called while recursing through included fragments, so it much check the
	 * include stack. The filesystem location is needed for common URI
	 * resolution in case the Taglib Index doesn't know the URI being loaded.
	 * 
	 * @return
	 */
	IPath getCurrentBaseLocation() {
		IPath baseLocation = null;
		IPath path = getCurrentParserPath();
		baseLocation = ResourcesPlugin.getWorkspace().getRoot().getFile(path).getLocation();
		if (baseLocation == null) {
			baseLocation = path;
		}
		return baseLocation;
	}

	/**
	 * Return the path used in the current parser. This method is called while
	 * recursing through included fragments, so it much check the include
	 * stack.
	 * 
	 * @return
	 */
	IPath getCurrentParserPath() {
		IPath path = null;
		if (!getIncludes().isEmpty()) {
			path = (IPath) getIncludes().peek();
		}
		else {
			path = TaglibController.getFileBuffer(this).getLocation();
		}

		return path;
	}

	protected DirectiveStructuredDocumentRegionHandler getDirectiveStructuredDocumentRegionHandler() {
		if (fDirectiveHandler == null)
			fDirectiveHandler = new DirectiveStructuredDocumentRegionHandler();
		return fDirectiveHandler;
	}

	/**
	 * Gets the documents.
	 * 
	 * @return Returns a java.util.Hashtable
	 */
	public Hashtable getDocuments() {
		if (fDocuments == null)
			fDocuments = new Hashtable();
		return fDocuments;
	}

	/**
	 * Return the CMDocument at the tagdir (cached)
	 */
	protected CMDocument getImplicitCMDocument(String tagdir) {
		if (tagdir == null || tagdir.length() == 0)
			return null;
		String reference = tagdir;
		/**
		 * JSP 1.2 Specification, section 5.2.2 jsp-1_2-fcs-spec.pdf, page 87
		 */
		String URNprefix = "urn:jsptld:"; //$NON-NLS-1$
		if (reference.startsWith(URNprefix)) {
			/**
			 * @see section 7.3.2
			 */
			if (reference.length() > URNprefix.length())
				reference = reference.substring(11);
		}
		else {
			/**
			 * @see section 7.3.6
			 */
		}
		CMDocument doc = (CMDocument) getDocuments().get(reference);
		if (doc == null) {
			doc = loadTagDir(reference);
			if (doc != null)
				getDocuments().put(reference, doc);
		}
		return doc;
	}

	/**
	 * Gets the includes.
	 * 
	 * @return Returns a Stack
	 */
	protected Stack getIncludes() {
		if (fIncludes == null)
			fIncludes = new Stack();
		return fIncludes;
	}

	JSPSourceParser getParser() {
		return fParser;
	}

	public JSPSourceParser getSourceParser() {
		return fParser;
	}

	public StructuredDocumentRegionHandler getStructuredDocumentRegionHandler() {
		return getDirectiveStructuredDocumentRegionHandler();
	}

	/**
	 * 
	 * @return java.util.List
	 */
	public List getTaglibTrackers() {
		if (fTaglibTrackers == null)
			fTaglibTrackers = new ArrayList();
		return fTaglibTrackers;
	}

	/**
	 * @param filePath
	 *            the path to check for modification
	 */
	boolean hasAnyIncludeBeenModified(IPath filePath) {
		boolean result = false;
		// check the top level
		if (hasBeenModified(filePath)) {
			result = true;
		}
		else {
			// check all includees
			Iterator iter = fInclude2TimestampMap.keySet().iterator();
			while (iter.hasNext()) {
				if (hasBeenModified((IPath) iter.next())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @param filename
	 * @return
	 */
	boolean hasBeenModified(IPath filePath) {
		boolean result = false;
		// quick filename/timestamp cache check here...
		IFile f = null;
		if (f == null && filePath.segmentCount() > 1) {
			f = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		}
		if (f != null && f.exists()) {
			Long currentStamp = new Long(f.getModificationStamp());
			Object o = fInclude2TimestampMap.get(filePath);
			if (o != null) {
				Long previousStamp = (Long) o;
				// stamps don't match, file changed
				if (currentStamp.longValue() != previousStamp.longValue()) {
					result = true;
					// store for next time
					fInclude2TimestampMap.put(filePath, currentStamp);
				}
			}
			else {
				// return true, since we've not encountered this file yet.
				result = true;
				// store for next time
				fInclude2TimestampMap.put(filePath, currentStamp);
			}
		}
		return result;
	}

	public void indexChanged(ITaglibRecordEvent event) {}

	/**
	 * Loads the tags from the specified URI. It must point to a URL of valid
	 * tag files to work.
	 */
	protected CMDocument loadTagDir(String uri) {
		ITaglibRecord reference = TaglibIndex.resolve(getCurrentParserPath().toString(), uri, false);
		if (reference != null) {
			CMDocument document = getCMDocumentBuilder().createCMDocument(reference);
			if (document != null) {
				return document;
			}
		}
		// JSP2_TODO: implement for JSP 2.0
		String location = URIResolverPlugin.createResolver().resolve(getCurrentBaseLocation().toString(), null, uri);
		if (location == null)
			return null;
		if (_debug) {
			System.out.println("Loading tags from dir" + uri + " at " + location); //$NON-NLS-2$//$NON-NLS-1$
		}
		return getCMDocumentBuilder().createCMDocument(location);
	}

	/**
	 * Loads the taglib from the specified URI. It must point to a valid
	 * taglib descriptor to work.
	 */
	protected TLDCMDocumentDescriptor loadTaglib(String uri) {
		TLDCMDocumentDescriptor entry = null;
		IPath currentPath = getCurrentParserPath();
		if (currentPath != null) {
			CMDocument document = null;
			ITaglibRecord record = TaglibIndex.resolve(currentPath.toString(), uri, false);
			if (record != null) {
				document = getCMDocumentBuilder().createCMDocument(record);
				if (document != null) {
					entry = new TLDCMDocumentDescriptor();
					entry.document = document;
					entry.cacheKey = getUniqueIdentifier(record);
				}
			}
			else {
				/* Not a very-often used code path (we hope) */
				IPath currentBaseLocation = getCurrentBaseLocation();
				if (currentBaseLocation != null) {
					String location = URIResolverPlugin.createResolver().resolve(currentBaseLocation.toString(), null, uri);
					if (location != null) {
						if (_debug) {
							System.out.println("Loading tags from " + uri + " at " + location); //$NON-NLS-2$//$NON-NLS-1$
						}
						document = getCMDocumentBuilder().createCMDocument(location);
						entry = new TLDCMDocumentDescriptor();
						entry.document = document;
						entry.cacheKey = location;
					}
				}
			}
		}
		return entry;
	}

	protected void resetTaglibTrackers() {
		if (_debug) {
			System.out.println("TLDCMDocumentManager cleared its taglib trackers\n"); //$NON-NLS-1$
		}
		getTaglibTrackers().clear();
	}

	public void setSourceParser(JSPSourceParser parser) {
		if (fParser != null)
			fParser.removeStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
		fParser = parser;
		if (fParser != null)
			fParser.addStructuredDocumentRegionHandler(getStructuredDocumentRegionHandler());
	}
}