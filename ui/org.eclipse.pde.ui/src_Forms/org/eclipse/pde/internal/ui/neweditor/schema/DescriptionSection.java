/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.neweditor.schema;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.core.IEditable;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.schema.*;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.XMLConfiguration;
import org.eclipse.pde.internal.ui.editor.text.*;
import org.eclipse.pde.internal.ui.editor.text.IColorManager;
import org.eclipse.pde.internal.ui.neweditor.*;
import org.eclipse.pde.internal.ui.neweditor.PDESection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.actions.*;
import org.eclipse.ui.forms.*;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.forms.widgets.Section;

public class DescriptionSection extends PDESection implements IPartSelectionListener {
	private Button applyButton;
	private Button resetButton;
	private IDocument document;
	private boolean editable = true;
	private SourceViewerConfiguration sourceConfiguration;
	private ISchemaObject element;
	private SourceViewer sourceViewer;
	public static final String SECTION_TITLE =
		"SchemaEditor.DescriptionSection.title";
	public static final String KEY_PENDING_TITLE =
		"SchemaEditor.DescriptionSection.pending.title";
	public static final String KEY_APPLY = "Actions.apply.flabel";
	public static final String KEY_RESET = "Actions.reset.flabel";
	public static final String KEY_PENDING_MESSAGE =
		"SchemaEditor.DescriptionSection.pending.message";
	public static final String SECTION_DESC =
		"SchemaEditor.DescriptionSection.desc";
	private IDocumentPartitioner partitioner;
	private ISchema schema;
	private boolean ignoreChange = false;

	public DescriptionSection(PDEFormPage page, Composite parent, IColorManager colorManager) {
		super(page, parent, Section.DESCRIPTION);
		getSection().setText(PDEPlugin.getResourceString(SECTION_TITLE));
		getSection().setDescription(PDEPlugin.getResourceString(SECTION_DESC));
		sourceConfiguration = new XMLConfiguration(colorManager);
		document = new Document();
		partitioner =
			new DefaultPartitioner(
				new XMLPartitionScanner(),
				new String[] {
					XMLPartitionScanner.XML_TAG,
					XMLPartitionScanner.XML_COMMENT });
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		createClient(getSection(), page.getManagedForm().getToolkit());
	}
	private void checkForPendingChanges() {
		if (applyButton.isEnabled()) {
			if (MessageDialog
				.openQuestion(
					PDEPlugin.getActiveWorkbenchShell(),
					PDEPlugin.getResourceString(KEY_PENDING_TITLE),
					PDEPlugin.getResourceString(KEY_PENDING_MESSAGE))
				== true)
				handleApply();
		}
	}
	public void commit(boolean onSave) {
		handleApply();
		if (onSave) {
			resetButton.setEnabled(false);
		}
		super.commit(onSave);
	}
	public void createClient(
		Section section,
		FormToolkit toolkit) {
		Composite container = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		container.setLayout(layout);
		GridData gd;
		int styles =
			SWT.MULTI
				| SWT.WRAP
				| SWT.V_SCROLL
				| SWT.H_SCROLL;
		sourceViewer = new SourceViewer(container, null, styles);
		sourceViewer.configure(sourceConfiguration);
		sourceViewer.setDocument(document);
		sourceViewer.setEditable(isEditable());
		sourceViewer
			.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelection(event.getSelection());
			}
		});
		Control styledText = sourceViewer.getTextWidget();
		styledText.setFont(
			JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT));
		if (SWT.getPlatform().equals("motif") == false)
			toolkit.paintBordersFor(container);
		styledText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		Control[] children = container.getChildren();
		Control control = children[children.length - 1];
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 200;
		gd.heightHint = 64;
		control.setLayoutData(gd);
		styledText.setMenu(getPage().getPDEEditor().getContextMenu());
		styledText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				updateSelection(sourceViewer.getSelection());
			}
		});

		Composite buttonContainer = toolkit.createComposite(container);
		layout = new GridLayout();
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);

		applyButton =
			toolkit.createButton(
				buttonContainer,
				PDEPlugin.getResourceString(KEY_APPLY),
				SWT.PUSH);
		applyButton.setEnabled(false);
		gd =
			new GridData(
				GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		applyButton.setLayoutData(gd);
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleApply();
			}
		});

		resetButton =
			toolkit.createButton(
				buttonContainer,
				PDEPlugin.getResourceString(KEY_RESET),
				SWT.PUSH);
		resetButton.setEnabled(false);
		gd =
			new GridData(
				GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		resetButton.setLayoutData(gd);
		resetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleReset();
			}
		});
		section.setClient(container);
		initialize();
	}

	private void updateSelection(ISelection selection) {
		getPage().getPDEEditor().setSelection(selection);
	}

	public boolean doGlobalAction(String actionId) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			sourceViewer.doOperation(SourceViewer.CUT);
			return true;
		} else if (
			actionId.equals(ActionFactory.COPY.getId())) {
			sourceViewer.doOperation(SourceViewer.COPY);
			return true;
		} else if (
			actionId.equals(ActionFactory.PASTE.getId())) {
			sourceViewer.doOperation(SourceViewer.PASTE);
			return true;
		} else if (
			actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			sourceViewer.doOperation(SourceViewer.SELECT_ALL);
			return true;
		} else if (
			actionId.equals(ActionFactory.DELETE.getId())) {
			sourceViewer.doOperation(SourceViewer.DELETE);
			return true;
		} else if (
			actionId.equals(ActionFactory.UNDO.getId())) {
			sourceViewer.doOperation(SourceViewer.UNDO);
			return true;
		} else if (
			actionId.equals(ActionFactory.REDO.getId())) {
			sourceViewer.doOperation(SourceViewer.REDO);
			return true;
		}
		return false;
	}
	protected void fillContextMenu(IMenuManager manager) {
		getPage().getPDEEditor().getContributor().contextMenuAboutToShow(
			manager);
	}
	private void handleApply() {
		if (element != null) {
			if (element == schema) {
				((Schema)schema).setDescription(document.get());
			}
			else {
			 ((SchemaObject) element).setDescription(document.get());
			}
		}
		applyButton.setEnabled(false);
		resetButton.setEnabled(false);
	}
	private void handleReset() {
		updateDocument();
	}
	public void initialize() {
		schema = (ISchema) getPage().getModel();
		element = schema;
		updateDocument();
		document.addDocumentListener(new IDocumentListener() {
			public void documentChanged(DocumentEvent e) {
				if (!ignoreChange && schema instanceof IEditable) {
					markDirty();
				}
				applyButton.setEnabled(true);
				resetButton.setEnabled(true);
			}
			public void documentAboutToBeChanged(DocumentEvent e) {
			}
		});
	}
	public boolean isEditable() {
		return editable;
	}
	public void selectionChanged(IFormPart part, ISelection selection) {
		checkForPendingChanges();
		if (!(part instanceof ElementSection))
			return;
		Object changeObject = ((IStructuredSelection)selection).getFirstElement();
		element = (ISchemaObject) changeObject;
		if (element == null)
			element = schema;
		updateDocument();
	}
	public void setFocus() {
		sourceViewer.getTextWidget().setFocus();
	}
	public void setEditable(boolean newEditable) {
		editable = newEditable;
	}
	public void updateDocument() {
		ignoreChange = true;
		String text = element.getDescription();
		if (text == null)
			text = "";
		/*
		else
			text = TextUtil.createMultiLine(text, 60, false);
		*/
		document.set(text);
		resetButton.setEnabled(false);
		applyButton.setEnabled(false);
		ignoreChange = false;
//		ISchemaObject eobj = element;
//		if (element instanceof ISchemaAttribute) {
//			eobj = element.getParent();
//		}
		//sourceViewer.setEditable(eobj.getName().equals("extension")==false);
	}

	public boolean canPaste(Clipboard clipboard) {
		return sourceViewer.canDoOperation(SourceViewer.PASTE);
	}
}
