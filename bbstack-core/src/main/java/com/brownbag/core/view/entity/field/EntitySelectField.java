package com.brownbag.core.view.entity.field;

import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.singleselect.EntitySingleSelect;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.apache.commons.beanutils.BeanUtils;
import org.vaadin.addon.customfield.CustomField;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/12/11
 * Time: 4:45 PM
 */
public class EntitySelectField extends CustomField {

    private TextField field;

    private EntitySingleSelect entitySingleSelect;

    private Button clearButton;

    private Window popupWindow;

    private EntityForm entityForm;
    private String propertyId;


    public EntitySelectField(EntityForm entityForm, String propertyId, EntitySingleSelect entitySingleSelect) {
        this.entityForm = entityForm;
        this.propertyId = propertyId;
        this.entitySingleSelect = entitySingleSelect;
        postConstruct();
    }

    public void postConstruct() {
        setSizeUndefined();
        field = new TextField();
        FormField.initAbstractFieldDefaults(field);
        FormField.initTextFieldDefaults(field);
        field.setReadOnly(true);

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(field);

        final Button searchButton = new Button();
        searchButton.setSizeUndefined();
        searchButton.addStyleName("borderless");
        searchButton.setIcon(new ThemeResource("../chameleon/img/magnifier.png"));
        searchButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                open();
            }
        });
        layout.addComponent(searchButton);

        clearButton = new Button();
        clearButton.setSizeUndefined();
        clearButton.addStyleName("borderless");
        clearButton.setIcon(new ThemeResource("../runo/icons/16/cancel.png"));
        layout.addComponent(clearButton);

        entitySingleSelect.getEntityResults().addSelectButtonListener(this, "itemSelected");
        addClearListener(this, "itemCleared");

        setCompositionRoot(layout);
    }

    public void itemSelected() {
        Object selectedValue = getSelectedValue();
        Object entity = entityForm.getEntity();
        try {
            BeanUtils.setProperty(entity, propertyId, selectedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        entityForm.refreshFromDataSource();
        close();
    }

    public void itemCleared() {
        Object entity = entityForm.getEntity();
        try {
            BeanUtils.setProperty(entity, propertyId, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        entityForm.refreshFromDataSource();
    }

    public void addClearListener(Object target, String methodName) {
        clearButton.addListener(Button.ClickEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        return entitySingleSelect.getEntityResults().getSelectedValue();
    }

    public void open() {
        popupWindow = new Window(getCaption());
        popupWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        popupWindow.setSizeUndefined();
        popupWindow.setModal(true);
        popupWindow.addComponent(entitySingleSelect);
        popupWindow.setClosable(true);
        entitySingleSelect.getEntityResults().getEntityQuery().clear();
        entitySingleSelect.getEntityResults().search();
        MainApplication.getInstance().getMainWindow().addWindow(popupWindow);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(popupWindow);
    }


    public String getRequiredError() {
        return field.getRequiredError();
    }

    public boolean isRequired() {
        return field.isRequired();
    }

    public void setRequired(boolean required) {
        field.setRequired(required);
    }

    public void setRequiredError(String requiredMessage) {
        field.setRequiredError(requiredMessage);
    }

    public boolean isInvalidCommitted() {
        return field.isInvalidCommitted();
    }

    public void setInvalidCommitted(boolean isCommitted) {
        field.setInvalidCommitted(isCommitted);
    }

    public void commit() throws SourceException, Validator.InvalidValueException {
//        field.commit();
    }

    public void discard() throws SourceException {
        field.discard();
    }

    public boolean isModified() {
        return field.isModified();
    }

    public boolean isReadThrough() {
        return field.isReadThrough();
    }

    public boolean isWriteThrough() {
        return field.isWriteThrough();
    }

    public void setReadThrough(boolean readThrough) throws SourceException {
        field.setReadThrough(readThrough);
    }

    public void setWriteThrough(boolean writeThrough) throws SourceException,
            Validator.InvalidValueException {
        field.setWriteThrough(writeThrough);
    }

    public void addValidator(Validator validator) {
        field.addValidator(validator);
    }

    public Collection<Validator> getValidators() {
        return field.getValidators();
    }

    public boolean isInvalidAllowed() {
        return field.isInvalidAllowed();
    }

    public boolean isValid() {
        return field.isValid();
    }

    public void removeValidator(Validator validator) {
        field.removeValidator(validator);

    }

    public void setInvalidAllowed(boolean invalidValueAllowed)
            throws UnsupportedOperationException {
        field.setInvalidAllowed(invalidValueAllowed);
    }

    public void validate() throws Validator.InvalidValueException {
        field.validate();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        field.setValue(newValue);
    }

    public void addListener(ValueChangeListener listener) {
        field.addListener(listener);
    }

    public void removeListener(ValueChangeListener listener) {
        field.removeListener(listener);
    }

    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        field.valueChange(event);
    }

    public Property getPropertyDataSource() {
        return field.getPropertyDataSource();
    }

    public void setPropertyDataSource(Property newDataSource) {
        field.setPropertyDataSource(newDataSource);

    }

    public void focus() {
        field.focus();
    }

    public int getTabIndex() {
        return field.getTabIndex();
    }

    public void setTabIndex(int tabIndex) {
        field.setTabIndex(tabIndex);
    }

    public Object getValue() {
        return field.getValue();
    }
}
