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
package org.eclipse.che.ide.api.machine.execagent;

import org.eclipse.che.api.machine.shared.dto.execagent.event.ProcessStdOutEventDto;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Singleton;

/**
 * Handles event fired by exec agent when process sent text to standard event
 *
 * @author Dmitry Kuleshov
 */
@Singleton
public class ProcessStdOutEventHandler extends AbstractExecAgentEventHandler<ProcessStdOutEventDto, Void> {

    protected ProcessStdOutEventHandler() {
        super(ProcessStdOutEventDto.class, Void.class);
    }

    @Override
    public void handleNotification(ProcessStdOutEventDto params) {
        Log.debug(getClass(), "Handling process standard output event. Params: " + params);
        handle(params);
    }
}