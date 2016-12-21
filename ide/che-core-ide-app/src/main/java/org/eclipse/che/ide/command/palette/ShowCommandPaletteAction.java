/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.command.palette;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;

/**
 * Action for opening Command Palette.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class ShowCommandPaletteAction extends Action {

    private final CommandPalettePresenter presenter;

    @Inject
    public ShowCommandPaletteAction(CommandPalettePresenter presenter) {
        super("Command Palette", "Show Command Palette", null, null);

        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }
}
