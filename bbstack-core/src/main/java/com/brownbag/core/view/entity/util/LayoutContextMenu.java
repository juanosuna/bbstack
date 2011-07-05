package com.brownbag.core.view.entity.util;

import com.brownbag.core.util.MethodDelegate;
import com.brownbag.core.util.assertion.Assert;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbstractOrderedLayout;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: Juan
 * Date: 6/4/11
 * Time: 11:22 PM
 */
public class LayoutContextMenu extends ContextMenu implements LayoutEvents.LayoutClickListener, ContextMenu.ClickListener {

    private Map<String, ContextMenuAction> actions = new LinkedHashMap<String, ContextMenuAction>();

    public LayoutContextMenu(AbstractOrderedLayout layout) {
        super();
        layout.addListener(this);
        layout.addComponent(this);
        addListener(this);
    }

    public ContextMenu.ContextMenuItem addAction(String caption, Object target, String methodName) {
        ContextMenu.ContextMenuItem item = super.addItem(caption);

        MethodDelegate methodDelegate = new MethodDelegate(target, methodName, ContextMenu.ContextMenuItem.class);
        ContextMenuAction contextMenuAction = new ContextMenuAction(item, methodDelegate);
        actions.put(caption, contextMenuAction);

        return item;
    }

    public ContextMenu.ContextMenuItem getContextMenuItem(String caption) {
        return actions.get(caption).getItem();
    }

    public boolean containsItem(String caption) {
        return actions.containsKey(caption);
    }

    @Override
    public void contextItemClick(ContextMenu.ClickEvent clickEvent) {
        ContextMenu.ContextMenuItem clickedItem = clickEvent.getClickedItem();
        ContextMenuAction action = actions.get(clickedItem.getName());
        action.getMethodDelegate().execute(clickedItem);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (LayoutEvents.LayoutClickEvent.BUTTON_RIGHT == event.getButton()) {
            show(event.getClientX(), event.getClientY());
        }
    }

    public static class ContextMenuAction {
        private ContextMenu.ContextMenuItem item;
        private MethodDelegate methodDelegate;

        public ContextMenuAction(ContextMenu.ContextMenuItem item, MethodDelegate methodDelegate) {
            this.item = item;
            this.methodDelegate = methodDelegate;
        }

        public MethodDelegate getMethodDelegate() {
            return methodDelegate;
        }

        public ContextMenuItem getItem() {
            return item;
        }

        public Object execute() {
            return methodDelegate.execute(item);
        }
    }
}

