/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.intellij.tektoncd.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import com.redhat.devtools.intellij.common.actions.StructureTreeAction;
import com.redhat.devtools.intellij.tektoncd.tree.ClusterTasksNode;
import com.redhat.devtools.intellij.tektoncd.tree.ClusterTriggerBindingsNode;
import com.redhat.devtools.intellij.tektoncd.tree.ConditionsNode;
import com.redhat.devtools.intellij.tektoncd.tree.EventListenersNode;
import com.redhat.devtools.intellij.tektoncd.tree.NamespaceNode;
import com.redhat.devtools.intellij.tektoncd.tree.ParentableNode;
import com.redhat.devtools.intellij.tektoncd.tree.PipelinesNode;
import com.redhat.devtools.intellij.tektoncd.tree.ResourcesNode;
import com.redhat.devtools.intellij.tektoncd.tree.TasksNode;
import com.redhat.devtools.intellij.tektoncd.tree.TektonTreeStructure;
import com.redhat.devtools.intellij.tektoncd.tree.TriggerBindingsNode;
import com.redhat.devtools.intellij.tektoncd.tree.TriggerTemplatesNode;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_CLUSTERTASKS;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_CLUSTERTRIGGERBINDINGS;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_CONDITIONS;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_EVENTLISTENER;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_PIPELINES;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_RESOURCES;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_TASKS;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_TRIGGERBINDINGS;
import static com.redhat.devtools.intellij.tektoncd.Constants.KIND_TRIGGERTEMPLATES;
import static com.redhat.devtools.intellij.tektoncd.Constants.STRUCTURE_PROPERTY;

public class TreeHelper {

    public static Tree getTree(Project project) {
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("Tekton");
        JBScrollPane pane = (JBScrollPane) window.getContentManager().findContent("").getComponent();
        return (Tree) pane.getViewport().getView();
    }

    /**
     *
     * Refresh node with name and kind on tree.
     * If name is empty refresh first node of that kind
     *
     * @param tree tree of node to be refreshed
     * @param kind kind of node to be refreshed
     * @param namespace name of node to be refreshed
     */
    public static void refreshNode(Tree tree, String kind, String namespace) {
        if (tree == null) {
            return;
        }


        Class nodeClass = retrieveNodeClassByKind(kind);

        if (nodeClass == null) return;
        Optional<Object> element = StreamSupport.stream(TreeUtil.treeTraverser(tree).spliterator(), false)
                .map(StructureTreeAction::getElement)
                .filter(el -> el instanceof ParentableNode && ((ParentableNode)el).getParent() instanceof NamespaceNode && ((NamespaceNode)((ParentableNode)el).getParent()).getName().equals(namespace))
                .filter(el -> nodeClass.isInstance(el)).findFirst();
        if (element.isPresent()) {
            ((TektonTreeStructure) tree.getClientProperty(STRUCTURE_PROPERTY)).fireModified(element.get());
        }
    }

    public static Class retrieveNodeClassByKind(String kind) {
        switch(kind) {
            case KIND_CLUSTERTASKS: return ClusterTasksNode.class;
            case KIND_PIPELINES: return PipelinesNode.class;
            case KIND_RESOURCES: return ResourcesNode.class;
            case KIND_TASKS: return TasksNode.class;
            case KIND_CONDITIONS: return ConditionsNode.class;
            case KIND_TRIGGERTEMPLATES: return TriggerTemplatesNode.class;
            case KIND_TRIGGERBINDINGS: return TriggerBindingsNode.class;
            case KIND_CLUSTERTRIGGERBINDINGS: return ClusterTriggerBindingsNode.class;
            case KIND_EVENTLISTENER: return EventListenersNode.class;
            default: return null;
        }
    }
}
