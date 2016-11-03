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
package org.eclipse.che.ide.command.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ui.radiobuttongroup.RadioButtonGroup;
import org.eclipse.che.ide.ui.smartTree.NodeLoader;
import org.eclipse.che.ide.ui.smartTree.NodeStorage;
import org.eclipse.che.ide.ui.smartTree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.ui.smartTree.SelectionModel.Mode.SINGLE;

/**
 * Implementation of {@link CommandsExplorerView}.
 *
 * @author Artem Zatsarynnyi
 */
public class CommandsExplorerViewImpl extends BaseView<CommandsExplorerView.ActionDelegate> implements CommandsExplorerView {

    private static final CommandsExplorerViewImplUiBinder UI_BINDER = GWT.create(CommandsExplorerViewImplUiBinder.class);

    @UiField(provided = true)
    Tree workspaceCommandsTree;

    @UiField(provided = true)
    Tree projectCommandsTree;

    @UiField
    RadioButtonGroup pagesSwitcher;

    @UiField
    DeckPanel pagesPanel;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    private int pageCounter;

    @Inject
    public CommandsExplorerViewImpl(org.eclipse.che.ide.Resources coreResources,
                                    CommandsExplorerResources resources) {
        super(coreResources);

        resources.styles().ensureInjected();

        setTitle("Commands Explorer");

        workspaceCommandsTree = new Tree(new NodeStorage(), new NodeLoader());
        workspaceCommandsTree.getSelectionModel().setSelectionMode(SINGLE);
        workspaceCommandsTree.setPresentationRenderer(new CommandsTreeRenderer(workspaceCommandsTree.getTreeStyles(),
                                                                               resources,
                                                                               delegate));
        workspaceCommandsTree.getSelectionModel().addSelectionHandler(new SelectionHandler<Node>() {
            @Override
            public void onSelection(SelectionEvent<Node> event) {
                Node selectedNode = event.getSelectedItem();
                if (selectedNode instanceof CommandNode) {
                    delegate.onCommandSelected(((CommandNode)selectedNode).getCommand());
                }
            }
        });

        projectCommandsTree = new Tree(new NodeStorage(), new NodeLoader());
        projectCommandsTree.getSelectionModel().setSelectionMode(SINGLE);
        projectCommandsTree.setPresentationRenderer(new CommandsTreeRenderer(projectCommandsTree.getTreeStyles(),
                                                                             resources,
                                                                             delegate));
        projectCommandsTree.getSelectionModel().addSelectionHandler(new SelectionHandler<Node>() {
            @Override
            public void onSelection(SelectionEvent<Node> event) {
                Node selectedNode = event.getSelectedItem();
                if (selectedNode instanceof CommandNode) {
                    delegate.onCommandSelected(((CommandNode)selectedNode).getCommand());
                }
            }
        });

        setContentWidget(UI_BINDER.createAndBindUi(this));

        setSaveEnabled(false);

        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCommandSave(getSelectedCommand());
            }
        });
    }

    @Override
    public void addPage(IsWidget page, String title, String tooltip) {
        final int pageIndex = pageCounter;

        pagesSwitcher.addButton(title, tooltip, null, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pagesPanel.showWidget(pageIndex);
            }
        });

        pagesPanel.add(page);

        if (pageCounter == 0) {
            pagesSwitcher.selectButton(0);
            pagesPanel.showWidget(0);
        }

        pageCounter++;
    }

    @Override
    public void setCommands(Map<CommandType, List<CommandImpl>> workspaceCommands,
                            Map<Project, Map<CommandType, List<CommandImpl>>> projectsCommands) {
        renderWorkspaceCommands(workspaceCommands);
        renderProjectsCommands(projectsCommands);
    }

    @Override
    public CommandImpl getSelectedCommand() {
        return null;
    }

    @Override
    public void setSaveEnabled(boolean enable) {
        cancelButton.setEnabled(enable);
        saveButton.setEnabled(enable);
    }

    private void renderWorkspaceCommands(Map<CommandType, List<CommandImpl>> workspaceCommands) {
        workspaceCommandsTree.getNodeStorage().clear();

        for (Map.Entry<CommandType, List<CommandImpl>> entry : workspaceCommands.entrySet()) {
            List<CommandNode> commandNodes = new ArrayList<>(entry.getValue().size());
            for (CommandImpl command : entry.getValue()) {
                commandNodes.add(new CommandNode(command));
            }

            CommandTypeNode commandTypeNode = new CommandTypeNode(entry.getKey(), commandNodes);
            workspaceCommandsTree.getNodeStorage().add(commandTypeNode);
        }

        workspaceCommandsTree.expandAll();
    }

    private void renderProjectsCommands(Map<Project, Map<CommandType, List<CommandImpl>>> projectsCommands) {
        projectCommandsTree.getNodeStorage().clear();

        for (Map.Entry<Project, Map<CommandType, List<CommandImpl>>> entry1 : projectsCommands.entrySet()) {
            final Project project = entry1.getKey();

            List<CommandTypeNode> commandTypeNodes = new ArrayList<>();
            for (Map.Entry<CommandType, List<CommandImpl>> entry2 : entry1.getValue().entrySet()) {

                List<CommandNode> commandNodes = new ArrayList<>();
                for (CommandImpl command : entry2.getValue()) {
                    commandNodes.add(new CommandNode(command));
                }

                CommandType commandType = entry2.getKey();
                commandTypeNodes.add(new CommandTypeNode(commandType, commandNodes));
            }

            ProjectNode projectNode = new ProjectNode(project.getName(), commandTypeNodes);
            projectCommandsTree.getNodeStorage().add(projectNode);
        }

        projectCommandsTree.expandAll();
    }

    @UiHandler("cancelButton")
    public void handleCancelButton(ClickEvent clickEvent) {
        delegate.onCommandRevert(getSelectedCommand());
    }

    @UiHandler("saveButton")
    public void handleSaveButton(ClickEvent clickEvent) {
        delegate.onCommandSave(getSelectedCommand());
    }

    interface CommandsExplorerViewImplUiBinder extends UiBinder<Widget, CommandsExplorerViewImpl> {
    }
}