/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.tests.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.dtd.ui.tests.ProjectUtil;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.reconcile.ISourceReconcilingListener;

/**
 * <p>Tests that code folding annotations are correctly added/removed from DTD Documents</p>
 * <p>All of these tests use the same project and when possible the same open documents</p>
 * 
 * @see org.eclipse.wst.xml.ui.tests.XMLCodeFoldingTest Similar Test - XML Code Folding Test
 * @see org.eclipse.wst.css.ui.tests.viewer.CSSCodeFoldingTest Similar Test - CSS Code Folding Test
 * @see org.eclipse.wst.dtd.ui.tests.viewer.DTDCodeFoldingTest Similar Test - DTD Code Folding Test
 */
public class DTDCodeFoldingTest extends TestCase implements ISourceReconcilingListener {
	/** max amount of time to wait for */
	private static final int MAX_WAIT_TIME = 4000;
	
	/** amount of time to wait for */
	private static final int WAIT_TIME = 200;
	
	/**
	 * The name of the project that all of these tests will use
	 */
	private static final String PROJECT_NAME = "DTDCodeFoldingTest";
	
	/**
	 * The location of the testing files
	 */
	private static final String PROJECT_FILES = "/testresources/folding";
	
	/**
	 * The project that all of the tests use
	 */
	private static IProject fProject;
	
	/**
	 * Used to keep track of the already open editors so that the tests don't go through
	 * the trouble of opening the same editors over and over again
	 */
	private static Map fFileToEditorMap = new HashMap();
	
	/** the last {@link IDocument} to be reconciled */
	private static IDocument fReconciledDoc = null;
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public DTDCodeFoldingTest() {
		super("DTD Code Folding Test");
	}
	
	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public DTDCodeFoldingTest(String name) {
		super(name);
	}
	
	/**
	 * <p>Use this method to add these tests to a larger test suite so set up
	 * and tear down can be performed</p>
	 * 
	 * @return a {@link TestSetup} that will run all of the tests in this class
	 * with set up and tear down.
	 */
	public static Test suite() {
		TestSuite ts = new TestSuite(DTDCodeFoldingTest.class, "DTD Code Folding Test");
		return new DTDCodeFoldingTestSetup(ts);

	}
	
	/**
	 * Reset the state between tests
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		fReconciledDoc = null;
	}
	
	/**
	 * <p><b>TEST:</b> the initially placed folding annotations</p>
	 */
	public void testInitFolding()throws Exception  {
		IFile file = getFile("DTDFoldingTest1.dtd");
		
		StructuredTextEditor editor  = getEditor(file);
		
		List expectedPositions = new ArrayList();
		expectedPositions.add(new Position(1299, 234));
		expectedPositions.add(new Position(39, 296));
		expectedPositions.add(new Position(498, 751));
		expectedPositions.add(new Position(1251, 17));
		expectedPositions.add(new Position(1269, 28));
		expectedPositions.add(new Position(337, 159));
		
		waitForReconcileThenVerify(editor.getTextViewer(), expectedPositions);
	}
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is removed</p>
	 * Removed because it keeps failing during builds but running locally and smoke testing
	 * presents no problems.
	 */
	/*public void testRemoveNode() throws Exception {
		IFile file = getFile("DTDFoldingTest1.dtd");
		
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			doc.replace(337, 159, "");
			editor.doSave(null);
			
			final List expectedPositions = new ArrayList();
			expectedPositions.add(new Position(1110, 28));
			expectedPositions.add(new Position(39, 296));
			expectedPositions.add(new Position(1140, 234));
			expectedPositions.add(new Position(339, 751));
			expectedPositions.add(new Position(1092, 17));
			
			waitForReconcileThenVerify(viewer, expectedPositions);
		} catch(BadLocationException e) {
			fail("Test is broken, replace location has become invalid.\n" + e.getMessage());
		}
	}*/
	
	/**
	 * <p><b>TEST:</b> that folding annotations are updated after node is added</p>
	 */
	public void testAddNode() throws Exception {
		IFile file = getFile("DTDFoldingTest2.dtd");
		
		StructuredTextEditor editor  = getEditor(file);
		
		try {
			StructuredTextViewer viewer = editor.getTextViewer();
			IDocument doc = viewer.getDocument();
			String newNodeText =
				"<!ATTLIST BDO\n" +
				"%coreattrs;\t\t\t\t-- id, class, style, title --\n" +
				"lang\t%LanguageCode;\t#IMPLIED\t-- language code --\n" +
				"dir\t(ltr|rtl)\t#REQUIRED\t-- directionality --\n" +
				">\n";
			doc.replace(597, 0, newNodeText);
			editor.doSave(null);
			
			List expectedPositions = new ArrayList();
			expectedPositions.add(new Position(1454, 234));
			expectedPositions.add(new Position(498, 906));
			expectedPositions.add(new Position(39, 296));
			expectedPositions.add(new Position(1406, 17));
			expectedPositions.add(new Position(337, 159));
			expectedPositions.add(new Position(1424, 28));
			
			waitForReconcileThenVerify(viewer, expectedPositions);
		} catch(BadLocationException e) {
			fail("Test is broken, add location has become invalid.\n" + e.getMessage());
		}
	}
	
	/**
	 * <p>Given a file name in <code>fProject</code> attempts to get an <code>IFile</code>
	 * for it, if the file doesn't exist the test fails.</p>
	 * 
	 * @param name the name of the file to get
	 * @return the <code>IFile</code> associated with the given <code>name</code>
	 */
	private static IFile getFile(String name) {
		IFile file = fProject.getFile(name);
		assertTrue("Test file " + file + " can not be found", file.exists());
		
		return file;
	}
	
	/**
	 * <p>Given a <code>file</code> get an editor for it. If an editor has already
	 * been retrieved for the given <code>file</code> then return the same already
	 * open editor.</p>
	 * 
	 * @param file open and return an editor for this
	 * @return <code>StructuredTextEditor</code> opened from the given <code>file</code>
	 */
	private StructuredTextEditor getEditor(IFile file)  {
		StructuredTextEditor editor = (StructuredTextEditor)fFileToEditorMap.get(file);
		
		if(editor == null) {
			try {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IEditorPart editorPart = IDE.openEditor(page, file, true, true);
				if(editorPart instanceof StructuredTextEditor) {
					editor = ((StructuredTextEditor)editorPart);
					standardizeLineEndings(editor);
				} else {
					fail("Unable to open structured text editor");
				}
				
				if(editor != null) {
					fFileToEditorMap.put(file, editor);
				} else {
					fail("Could not open viewer for " + file);
				}
			} catch (Exception e) {
				fail("Could not open editor for " + file + " exception: " + e.getMessage());
			}
		}
		
		return editor;
	}
	
	/**
	 * Waits for the dirty region reconciler to finish and then verifies that only and all
	 * of the <code>expectedPositions</code> have folding annotations in the given <code>viewer</code>
	 * 
	 * @param viewer check for annotations at the given <code>expectedPositions</code>
	 * in here after the dirty region reconciler job has finished
	 * @param expectedPositions check for annotations at these positions in the given <code>viewer</code>
	 * after the dirty region reconciler job has finished
	 * @throws InterruptedException 
	 */
	private void waitForReconcileThenVerify(final StructuredTextViewer viewer, final List expectedPositions) throws Exception {
		IDocument doc = viewer.getDocument();
		int time = 0;
		while(doc != fReconciledDoc && time <= MAX_WAIT_TIME) {
			Thread.sleep(WAIT_TIME);
			time += WAIT_TIME;
		}
		
		if(doc == fReconciledDoc) {
			verifyAnnotationPositions(viewer, expectedPositions);
		} else {
			Assert.fail("Document " + viewer.getDocument() + " was not reconciled with in " + MAX_WAIT_TIME +
					" so gave up waiting and in turn could not validate folding anotations");
		}
	}
	
	/**
	 * Verifies that only and all of the <code>expectedPositions</code> have folding annotations
	 * in the given <code>viewer</code>
	 * 
	 * @param viewer check for annotations at the given <code>expectedPositions</code> in here 
	 * @param expectedPositions check for annotations at these positions in the given <code>viewer</code>
	 */
	private void verifyAnnotationPositions(StructuredTextViewer viewer, List expectedPositions) throws Exception{
		ProjectionAnnotationModel projectionModel = viewer.getProjectionAnnotationModel();
		Iterator annotationIter = projectionModel.getAnnotationIterator();
		
		List unexpectedPositions = new ArrayList();
		
		while(annotationIter.hasNext()) {
			Object obj = annotationIter.next();
			if(obj instanceof ProjectionAnnotation) {
				ProjectionAnnotation annotation = (ProjectionAnnotation)obj;
				Position pos = projectionModel.getPosition(annotation);
				
				boolean found = expectedPositions.remove(pos);
				if(!found) {
					unexpectedPositions.add(pos);
				}
				
			}
		}
		
		String error = "";
		if(unexpectedPositions.size() != 0) {
			error += "There were " + unexpectedPositions.size() + " unexpected positions that were found";
			for(int i = 0; i < unexpectedPositions.size(); ++i) {
				Position pos = (Position) unexpectedPositions.get(i);
				error += "\n\t" + pos;
				error += "\n(" + fReconciledDoc.get(pos.offset, pos.length) + ")";
			}
		}
		
		if(expectedPositions.size() != 0 ) {
			error += "\nThere were " + expectedPositions.size() + " expected positions that were not found";
			for(int i = 0; i < expectedPositions.size(); ++i) {
				error += "\n\t" + expectedPositions.get(i);
			}
		}
		
		if(expectedPositions.size() != 0 || unexpectedPositions.size() != 0) {
			error += "\nFull Document:\n(" + fReconciledDoc.get() +")";
		}
		
		if(error.length() != 0) {
			fail(error);
		}
	}
	
	/**
	 * <p>Line endings can be an issue when running tests on different OSs.
	 * This function standardizes the line endings to use <code>\n</code></p>
	 * 
	 * <p>It will get the text from the given editor, change the line endings,
	 * and then save the editor</p>
	 * 
	 * @param editor standardize the line endings of the text presented in this
	 * editor.
	 */
	private void standardizeLineEndings(StructuredTextEditor editor) {
		IDocument doc = editor.getTextViewer().getDocument();
		String contents = doc.get();
		contents = StringUtils.replace(contents, "\r\n", "\n");
		contents = StringUtils.replace(contents, "\r", "\n");
		doc.set(contents);
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after (respectively) all tests in the inclosing class have run.</p>
	 */
	private static class DTDCodeFoldingTestSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public DTDCodeFoldingTestSetup(Test test) {
			super(test);
		}

		/**
		 * <p>This is run once before all of the tests</p>
		 * 
		 * @see junit.extensions.TestSetup#setUp()
		 */
		public void setUp() throws Exception {
			
			initializeResources();
			
			String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
			
			if (noninteractive != null) {
				previousWTPAutoTestNonInteractivePropValue = noninteractive;
			} else {
				previousWTPAutoTestNonInteractivePropValue = "false";
			}
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			//close out the editors
			Iterator iter = fFileToEditorMap.values().iterator();
			while(iter.hasNext()) {
				StructuredTextEditor editor = (StructuredTextEditor)iter.next();
				editor.doSave(null);
				editor.close(false);
			}
			
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
			
			fProject.delete(true, new NullProgressMonitor());
		}
		
		/**
		 * Set up the project and workbench, this should only be done once
		 */
		private static void initializeResources() {
			fProject = ProjectUtil.createProject(PROJECT_NAME, null, null);
			ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
		}
	}
	
	/**
	 * ignore
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.ISourceReconcilingListener#aboutToBeReconciled()
	 */
	public void aboutToBeReconciled() {
		//ignore
	}

	/**
	 * keep track of last document reconciled
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.ISourceReconcilingListener#reconciled(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.source.IAnnotationModel, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void reconciled(IDocument document, IAnnotationModel model,
			boolean forced, IProgressMonitor progressMonitor) {
		fReconciledDoc = document;
	}
}
