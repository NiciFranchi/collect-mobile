package org.openforis.collect.android.gui.input;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import org.openforis.collect.android.SurveyService;
import org.openforis.collect.android.gui.ServiceLocator;
import org.openforis.collect.android.viewmodel.*;

/**
 * @author Daniel Wiell
 */
public abstract class InputComponent<T extends UiAttribute> {
    private final T attribute;
    private final Context context;
    protected final SurveyService surveyService;

    protected InputComponent(T attribute, Context context) {
        this.attribute = attribute;
        this.context = context;
        this.surveyService = ServiceLocator.surveyService();
    }

    public abstract View getView();

    public abstract void updateAttribute();

    public View getDefaultFocusedView() {
        return null;
    }

    /**
     * Invoked when an attribute has changed.
     */
    public void onAttributeChange(UiAttribute attribute) {
        // Empty default implementation
    }

    protected final void notifyAboutAttributeChange() {
        surveyService.updateAttribute(attribute);
    }

    protected final T attribute() {
        return attribute;
    }

    protected final Context context() {
        return context;
    }

    public static InputComponent create(UiAttribute attribute, Context context) {
        if (attribute instanceof UiTextAttribute)
            return new TextComponent((UiTextAttribute) attribute, context);
        if (attribute instanceof UiIntegerAttribute)
            return new IntegerComponent((UiIntegerAttribute) attribute, context);
        if (attribute instanceof UiDoubleAttribute)
            return new DoubleComponent((UiDoubleAttribute) attribute, context);
        if (attribute instanceof UiTimeAttribute)
            return new TimePickerComponent((UiTimeAttribute) attribute, context);
        if (attribute instanceof UiDateAttribute)
            return new DatePickerComponent((UiDateAttribute) attribute, context);
        if (attribute instanceof UiCodeAttribute)
            return new CodeComponent((UiCodeAttribute) attribute, context);
        if (attribute instanceof UiTaxonAttribute)
            return new TaxonComponent((UiTaxonAttribute) attribute, context);
        else
            return new DummyInputComponent(attribute, context); // TODO: Remove once all are implemented
//            throw new IllegalStateException("Unexpected attribute type: " + attribute.getClass());
    }

    private static class DummyInputComponent extends InputComponent {
        @SuppressWarnings("unchecked")
        public DummyInputComponent(UiAttribute attribute, Context context) {
            super(attribute, context);
        }

        public View getView() {
            TextView textView = new TextView(context());
            textView.setText("Attribute type not implemented: " + attribute().getClass().getSimpleName());
            return textView;
        }

        public void updateAttribute() {

        }
    }
}
