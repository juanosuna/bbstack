package com.brownbag.core.view.entity.util;

import com.brownbag.core.util.MethodDelegate;
import com.brownbag.core.util.assertion.Assert;
import com.brownbag.core.view.MessageSource;
import com.vaadin.event.Action;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Juan
 * Date: 6/4/11
 * Time: 11:22 PM
 */
@Scope("prototype")
@Component
public class ActionContextMenu implements Action.Handler {

    @Resource
    private MessageSource uiMessageSource;

    private Map<String, ContextMenuAction> actions = new LinkedHashMap<String, ContextMenuAction>();

    public void addAction(String caption, Object target, String methodName) {
        Action action = new Action(uiMessageSource.getMessage(caption));
        MethodDelegate methodDelegate = new MethodDelegate(target, methodName);
        ContextMenuAction contextMenuAction = new ContextMenuAction(action, methodDelegate);
        actions.put(caption, contextMenuAction);
    }

    public void setActionEnabled(String caption, boolean enabled) {
        actions.get(caption).setEnabled(enabled);
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        List<Action> enabledActions = new ArrayList<Action>();
        for (ContextMenuAction contextMenuAction : actions.values()) {
            if (contextMenuAction.isEnabled()) {
                enabledActions.add(contextMenuAction.getAction());
            }
        }

        return enabledActions.toArray(new Action[enabledActions.size()]);
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        for (ContextMenuAction contextMenuAction : actions.values()) {
            if (action.equals(contextMenuAction.getAction())) {
                Assert.PROGRAMMING.assertTrue(contextMenuAction.isEnabled());
                contextMenuAction.getMethodDelegate().execute();
                break;
            }
        }
    }

    public static class ContextMenuAction {
        private Action action;
        private boolean enabled = false;
        private MethodDelegate methodDelegate;

        public ContextMenuAction(Action action, MethodDelegate methodDelegate) {
            this.action = action;
            this.methodDelegate = methodDelegate;
        }

        public Action getAction() {
            return action;
        }

        public MethodDelegate getMethodDelegate() {
            return methodDelegate;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

