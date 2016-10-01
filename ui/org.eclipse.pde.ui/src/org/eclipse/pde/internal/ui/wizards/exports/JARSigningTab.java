/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     EclipseSource Corporation - ongoing enhancements
 *     Martin Karpisek <martin.karpisek@gmail.com> - Bug 387565
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.exports;

import org.eclipse.equinox.security.storage.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

public class JARSigningTab {
	private static final String S_SIGN_JARS = "signJAR"; //$NON-NLS-1$
	private static final String S_KEYSTORE = "keystore"; //$NON-NLS-1$
	private static final String S_KEYPASS = "keypass"; //$NON-NLS-1$
	private static final String S_ALIAS = "alias"; //$NON-NLS-1$
	private static final String S_PASSWORD = "password"; //$NON-NLS-1$

	private Button fButton;

	private Label fKeystoreLabel;
	private Text fKeystoreText;

	private Label fKeypassLabel;
	private Text fKeypassText;

	private Label fAliasLabel;
	private Text fAliasText;

	private Label fPasswordLabel;
	private Text fPasswordText;
	private BaseExportWizardPage fPage;
	private Button fBrowseButton;

	public JARSigningTab(BaseExportWizardPage page) {
		fPage = page;
	}

	public Control createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fButton = new Button(comp, SWT.CHECK);
		fButton.setText(PDEUIMessages.AdvancedPluginExportPage_signButton);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		fButton.setLayoutData(gd);
		fButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateGroup(fButton.getSelection());
				fPage.pageChanged();
			}
		});

		fKeystoreLabel = createLabel(comp, PDEUIMessages.AdvancedPluginExportPage_keystore);
		fKeystoreText = createText(comp, 1);

		fBrowseButton = new Button(comp, SWT.PUSH);
		fBrowseButton.setText(PDEUIMessages.ExportWizard_browse);
		fBrowseButton.setLayoutData(new GridData());
		SWTUtil.setButtonDimensionHint(fBrowseButton);
		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(fPage.getShell(), SWT.OPEN);
				String path = fKeystoreText.getText();
				if (path.trim().length() == 0)
					path = PDEPlugin.getWorkspace().getRoot().getLocation().toString();
				dialog.setFileName(path);
				String res = dialog.open();
				if (res != null) {
					fKeystoreText.setText(res);
				}
			}
		});

		fKeypassLabel = createLabel(comp, PDEUIMessages.JARSigningTab_keypass);
		fKeypassText = createText(comp, 2);
		fKeypassText.setEchoChar('*');

		fAliasLabel = createLabel(comp, PDEUIMessages.AdvancedPluginExportPage_alias);
		fAliasText = createText(comp, 2);

		fPasswordLabel = createLabel(comp, PDEUIMessages.AdvancedPluginExportPage_password);
		fPasswordText = createText(comp, 2);
		fPasswordText.setEchoChar('*');

		Dialog.applyDialogFont(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(comp, IHelpContextIds.ADVANCED_PLUGIN_EXPORT);
		return comp;
	}

	protected void initialize(IDialogSettings settings) {
		ISecurePreferences preferences = getPreferences(settings.getName());
		if (preferences == null) {
			// only in case it is not possible to create secured storage in
			// default location -> in that case default values are used
			return;
		}

		String keystore = ""; //$NON-NLS-1$
		String keypass = ""; //$NON-NLS-1$
		String alias = ""; //$NON-NLS-1$
		String password = ""; //$NON-NLS-1$
		boolean signJars = false;
		if (preferences.keys().length <= 0) {
			// nothing stored in secured preferences, check settings for values
			// from before bug387565 fix
			keystore = getString(settings, S_KEYSTORE);
			keypass = getString(settings, S_KEYPASS);
			alias = getString(settings, S_ALIAS);
			password = getString(settings, S_PASSWORD);
			signJars = getBoolean(settings, S_SIGN_JARS);
		} else {
			// from secured preferences after bug387565 fix
			keystore = getString(preferences, S_KEYSTORE);
			keypass = getString(preferences, S_KEYPASS);
			alias = getString(preferences, S_ALIAS);
			password = getString(preferences, S_PASSWORD);
			signJars = getBoolean(preferences, S_SIGN_JARS);
		}

		fKeystoreText.setText(keystore);
		fKeypassText.setText(keypass);
		fAliasText.setText(alias);
		fPasswordText.setText(password);
		fButton.setSelection(signJars);
		updateGroup(fButton.getSelection());
	}

	private String getString(IDialogSettings settings, String key) {
		String s = settings.get(key);
		return s == null ? "" : s; //$NON-NLS-1$
	}

	private boolean getBoolean(IDialogSettings settings, String key) {
		return settings.getBoolean(key);
	}

	private String getString(ISecurePreferences settings, String key) {
		try {
			return settings.get(key, ""); //$NON-NLS-1$
		} catch (StorageException e) {
			return ""; //$NON-NLS-1$
		}
	}

	private boolean getBoolean(ISecurePreferences settings, String key) {
		try {
			return settings.getBoolean(key, false);
		} catch (StorageException e) {
			return false;
		}
	}

	protected Label createLabel(Composite group, String text) {
		Label label = new Label(group, SWT.NONE);
		label.setText(text);
		GridData gd = new GridData();
		gd.horizontalIndent = 15;
		label.setLayoutData(gd);
		return label;
	}

	protected Text createText(Composite group, int span) {
		Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		text.setLayoutData(gd);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fPage.pageChanged();
			}
		});
		return text;
	}

	protected String validate() {
		String error = null;
		if (fButton.getSelection()) {
			if (fKeystoreText.getText().trim().length() == 0) {
				error = PDEUIMessages.AdvancedPluginExportPage_noKeystore;
			} else if (fAliasText.getText().trim().length() == 0) {
				error = PDEUIMessages.AdvancedPluginExportPage_noAlias;
			} else if (fPasswordText.getText().trim().length() == 0) {
				error = PDEUIMessages.AdvancedPluginExportPage_noPassword;
			}
		}
		return error;
	}

	private void updateGroup(boolean enabled) {
		fKeystoreLabel.setEnabled(enabled);
		fKeystoreText.setEnabled(enabled);
		fBrowseButton.setEnabled(enabled);
		fAliasLabel.setEnabled(enabled);
		fAliasText.setEnabled(enabled);
		fPasswordLabel.setEnabled(enabled);
		fPasswordText.setEnabled(enabled);
		fKeypassLabel.setEnabled(enabled);
		fKeypassText.setEnabled(enabled);
	}

	protected void saveSettings(IDialogSettings settings) {
		ISecurePreferences preferences = getPreferences(settings.getName());
		if (preferences == null) {
			// only in case it is not possible to create secured storage in
			// default location -> in that case do not persist settings
			return;
		}

		try{
			preferences.putBoolean(S_SIGN_JARS, fButton.getSelection(), true);
			preferences.put(S_KEYSTORE, fKeystoreText.getText().trim(), true);
			preferences.put(S_ALIAS, fAliasText.getText().trim(), true);
			preferences.put(S_PASSWORD, fPasswordText.getText().trim(), true);
			preferences.put(S_KEYPASS, fKeypassText.getText().trim(), true);

			// bug387565 - for keys which are starting with this bugfix to be
			// stored
			// in secured storage, replace value in settings with empty string
			// to avoid keeping sensitive info in plain text
			String[] obsoleted = new String[] { S_KEYSTORE, S_ALIAS, S_PASSWORD, S_KEYPASS };
			for (String key : obsoleted) {
				if (settings.get(key) != null) {
					settings.put(key, ""); //$NON-NLS-1$
				}
			}
		}
		catch (StorageException e) {
			PDEPlugin.log("Failed to store JarSigning settings in secured preferences store"); //$NON-NLS-1$
		}

	}

	protected String[] getSigningInfo() {
		if (fButton.getSelection()) {
			return new String[] {fAliasText.getText().trim(), fKeystoreText.getText().trim(), fPasswordText.getText().trim(), fKeypassText.getText().trim()};
		}
		return null;
	}

	/**
	 * Answer secured preferences which can be used for storing sensitive
	 * information of this tab
	 *
	 * @return default instance of secure preferences for this tab,
	 *         <code>null</code> if application was unable to create secure
	 *         preferences using default location
	 */
	private ISecurePreferences getPreferences(String sectionName) {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		if (preferences == null) {
			return null;
		}
		return preferences.node(IPDEUIConstants.PLUGIN_ID).node(sectionName);
	}
}
