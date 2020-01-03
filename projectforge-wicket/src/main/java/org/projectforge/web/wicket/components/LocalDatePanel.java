/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.web.wicket.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.projectforge.Const;
import org.projectforge.web.wicket.LambdaModel;
import org.projectforge.web.wicket.WicketRenderHeadUtils;
import org.projectforge.web.wicket.WicketUtils;
import org.projectforge.web.wicket.converter.MyDateConverter;
import org.projectforge.web.wicket.flowlayout.ComponentWrapperPanel;

import java.util.Calendar;
import java.util.Date;
import java.util.function.BooleanSupplier;

/**
 * Panel for date selection. Works for java.util.Date and java.sql.Date. For java.sql.Date don't forget to call the constructor with
 * targetType java.sql.Date.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
public class LocalDatePanel extends FormComponentPanel<Date> implements ComponentWrapperPanel
{
  private static final long serialVersionUID = 3785639935585959803L;

  private BooleanSupplier requiredSupplier;

  private Date date;

  private final DateTextField dateField;

  private boolean modelMarkedAsChanged;

  private Boolean required;

  private boolean autosubmit;

  private int minYear = Const.MINYEAR, maxYear = Const.MAXYEAR;

  /**
   * @param id
   * @param model
   * @param settings         with target type etc.
   * @param useModelDirectly use the given model directly in the internal dateField
   */
  public LocalDatePanel(final String id, final LocalDateModel model, final DatePanelSettings settings, final boolean useModelDirectly)
  {
    this(id, model, settings, useModelDirectly, null);
  }

  /**
   * @param id
   * @param model
   * @param settings         with target type etc.
   * @param useModelDirectly use the given model directly in the internal dateField
   * @param requiredSupplier a callback which supplies the return value for the isRequired method of the date field.
   */
  @SuppressWarnings("serial")
  public LocalDatePanel(final String id, final LocalDateModel model, final DatePanelSettings settings, final boolean useModelDirectly,
                        final BooleanSupplier requiredSupplier)
  {
    super(id, model);
    this.requiredSupplier = requiredSupplier;
    setType(settings.targetType);
    final MyDateConverter dateConverter = new MyDateConverter(settings.targetType, "M-");
    dateConverter.setTimeZone(settings.timeZone);
    final IModel<Date> modelForDateField = useModelDirectly ? model : LambdaModel.of(() -> this.date, date -> this.date = date);
    dateField = new DateTextField("dateField", modelForDateField, dateConverter)
    {
      /**
       * @see org.apache.wicket.Component#renderHead(IHeaderResponse)
       */
      @Override
      public void renderHead(final IHeaderResponse response)
      {
        super.renderHead(response);
        WicketRenderHeadUtils.renderMainJavaScriptIncludes(response);
        DatePickerUtils.renderHead(response, getLocale(), dateField.getMarkupId(), autosubmit);
      }

      @Override
      public boolean isRequired()
      {
        return (LocalDatePanel.this.requiredSupplier != null) ? LocalDatePanel.this.requiredSupplier.getAsBoolean() : super.isRequired();
      }
    };
    dateField.add(AttributeModifier.replace("size", "10"));
    dateField.setOutputMarkupId(true);
    add(dateField);
    if (settings.required == true) {
      this.required = true;
    }
    if (settings.tabIndex != null) {
      dateField.add(AttributeModifier.replace("tabindex", String.valueOf(settings.tabIndex)));
    }
    dateField.add(new IValidator<Date>()
    {

      @Override
      public void validate(final IValidatable<Date> validatable)
      {
        final Date date = validatable.getValue();
        if (date != null) {
          final Calendar cal = Calendar.getInstance();
          cal.setTime(date);
          final int year = cal.get(Calendar.YEAR);
          if (year < minYear || year > maxYear) {
            validatable.error(new ValidationError().addKey("error.date.yearOutOfRange").setVariable("minimumYear", minYear)
                .setVariable("maximumYear", maxYear));
          }
        }
      }
    });
  }

  @Override
  public void validate()
  {
    dateField.validate();
    super.validate();
  }

  /**
   * Minimum year for validation (default is 1900).
   *
   * @param minYear the minYear to set
   * @return this for chaining.
   */
  public LocalDatePanel setMinYear(final int minYear)
  {
    this.minYear = minYear;
    return this;
  }

  /**
   * Maximum year for validation (default is 2100).
   *
   * @param maxYear the maxYear to set
   * @return this for chaining.
   */
  public LocalDatePanel setMaxYear(final int maxYear)
  {
    this.maxYear = maxYear;
    return this;
  }

  /**
   * @see FormComponent#setLabel(IModel)
   */
  @Override
  public LocalDatePanel setLabel(final IModel<String> labelModel)
  {
    dateField.setLabel(labelModel);
    super.setLabel(labelModel);
    return this;
  }

  public LocalDatePanel setFocus()
  {
    dateField.add(WicketUtils.setFocus());
    return this;
  }

  /**
   * If true then the parent form of this field will be submitted (no Ajax submit!).
   *
   * @param autosubmit the autosubmit to set
   * @return this for chaining.
   */
  public LocalDatePanel setAutosubmit(final boolean autosubmit)
  {
    this.autosubmit = autosubmit;
    return this;
  }

  public void setRequiredSupplier(final BooleanSupplier requiredSupplier)
  {
    this.requiredSupplier = requiredSupplier;
  }

  /**
   * Work around: If you change the model call this method, so onBeforeRender calls DateField.modelChanged() for updating the form text
   * field.
   */
  public void markModelAsChanged()
  {
    modelMarkedAsChanged = true;
  }

  @Override
  protected void onBeforeRender()
  {
    date = (Date) getDefaultModelObject(); // copy the value from the outer model to the dateField's model
    if (modelMarkedAsChanged == true) {
      dateField.modelChanged();
      modelMarkedAsChanged = false;
    }
    if (this.required != null) {
      dateField.setRequired(this.required);
    } else {
      dateField.setRequired(isRequired());
    }
    super.onBeforeRender();
  }

  /**
   * @see FormComponent#updateModel()
   */
  @Override
  public void updateModel()
  {
    if (modelMarkedAsChanged == true) {
      // Work-around: update model only if not marked as changed. Prevent overwriting the model by the user's input.
      modelMarkedAsChanged = false;
    } else {
      super.updateModel();
    }
  }

  public DateTextField getDateField()
  {
    return dateField;
  }

  @Override
  public void convertInput()
  {
    setConvertedInput(dateField.getConvertedInput());
  }

  @Override
  public String getInput()
  {
    return dateField.getInput();
  }

  /**
   * @see ComponentWrapperPanel#getComponentOutputId()
   */
  @Override
  public String getComponentOutputId()
  {
    return dateField.getMarkupId();
  }

  /**
   * @see ComponentWrapperPanel#getFormComponent()
   */
  @Override
  public FormComponent<?> getFormComponent()
  {
    return dateField;
  }
}