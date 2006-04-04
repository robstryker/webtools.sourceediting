/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.WebPropertiesUtil;

/**
 * This has been slated for removal post WTP 1.5. Do not use this class/interface
 * 
 * @deprecated
 */
public class SimpleContextRootComposite extends Composite implements DoNotUseMeThisWillBeDeletedPost15 {
	protected Text contextRootField;
	private String errorMessage = null;

	// listeners interested in the event when the context root
	// value is modified.
	private java.util.List modifyListeners;

	protected Listener contextRootModifyListener = new Listener() {
		public void handleEvent(Event e) {
			if (contextRootField != null)
				contextRootModified();
		}
	};

	public SimpleContextRootComposite(Composite parent) {
		super(parent, SWT.NONE);
		createControls();
		modifyListeners = new Vector(1);
	}

	protected void createControls() {
		// container specification group
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		setLayoutData(data);

		// New Context Root Label
		Label contextRootLabel = new Label(this, SWT.CHECK);
		contextRootLabel.setText(ResourceHandler.StaticContextRootComposite_Context_Root_Label); 

		// New Context Root Entryfield
		contextRootField = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		contextRootField.setLayoutData(data);

		contextRootField.addListener(SWT.Modify, contextRootModifyListener);

	}

	public String getContextRoot() {
		return contextRootField.getText();
	}

	public void setEnabled(boolean enabled) {
		if (contextRootField != null)
			contextRootField.setEnabled(enabled);
	}

	protected void contextRootModified() {
		errorMessage = WebPropertiesUtil.validateContextRoot(getContextRoot());
		// notify listeners
		Event e = new Event();
		e.type = SWT.Modify;
		e.widget = contextRootField;
		for (int i = 0; i < modifyListeners.size(); i++) {
			((Listener) modifyListeners.get(i)).handleEvent(e);
		}
	}

	public void setContextRoot(String cr) {
		contextRootField.setText(cr);
	}

	/**
	 * There is a default context root validation listener provided by the
	 * composite, adding external listeners will replace the default listener.
	 */
	public void addModifyListener(Listener l) {
		modifyListeners.add(l);
	}

	public void removeModifyListener(Listener l) {
		modifyListeners.remove(l);
	}

	/**
	 * validate the context root value and return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean setFocus() {
		return contextRootField.setFocus();
	}
}