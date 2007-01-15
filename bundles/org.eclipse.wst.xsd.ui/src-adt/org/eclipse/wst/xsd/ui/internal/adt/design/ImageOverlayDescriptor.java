/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class ImageOverlayDescriptor extends CompositeImageDescriptor
{
  protected Image baseImage;
  protected Image extensionOverlay;
  protected Point imageSize;
  
  public ImageOverlayDescriptor(Image baseImage)
  {
    super();
    this.baseImage = baseImage;
    imageSize = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
    extensionOverlay = XSDEditorPlugin.getPlugin().getIcon("ovr16/textoverlay.gif");  //$NON-NLS-1$ 
  }

  public Image getImage()
  {
    return createImage();
  }
  
  protected void drawCompositeImage(int width, int height)
  {
    drawImage(baseImage.getImageData(), 0, 0);  
    ImageData extensionImageData = extensionOverlay.getImageData();
    drawImage (extensionImageData, imageSize.x - extensionImageData.width, 0);  // Top Right corner
  }

  protected Point getSize()
  {
    return imageSize;
  }
}