/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2020 Micromata GmbH, Germany (www.micromata.com)
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

package org.projectforge.web.vacation.helper;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.projectforge.business.configuration.ConfigurationService;
import org.projectforge.business.fibu.EmployeeDO;
import org.projectforge.business.fibu.EmployeeStatus;
import org.projectforge.business.fibu.api.EmployeeService;
import org.projectforge.business.vacation.model.VacationDO;
import org.projectforge.business.vacation.repository.RemainingLeaveDao;
import org.projectforge.business.vacation.service.VacationService;
import org.projectforge.business.vacation.service.VacationStats;
import org.projectforge.framework.i18n.I18nHelper;
import org.projectforge.framework.time.DateTimeFormatter;
import org.projectforge.web.vacation.VacationEditPage;
import org.projectforge.web.vacation.VacationViewPageSortableDataProvider;
import org.projectforge.web.wicket.*;
import org.projectforge.web.wicket.bootstrap.GridBuilder;
import org.projectforge.web.wicket.bootstrap.GridSize;
import org.projectforge.web.wicket.flowlayout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
public class VacationViewHelper {
  @Autowired
  private VacationService vacationService;

  @Autowired
  private ConfigurationService configService;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private RemainingLeaveDao remainingLeaveDao;

  public void createVacationView(GridBuilder gridBuilder, EmployeeDO currentEmployee, boolean showAddButton, boolean showRecalculateButton, final WebPage returnToPage) {
    LocalDate endDatePreviousYearVacation = configService.getEndDateVacationFromLastYear();
    final int year = Year.now().getValue();
    final VacationStatsModel statsModel = new VacationStatsModel(currentEmployee, year);
    final VacationStatsModel previousYearStatsModel = new VacationStatsModel(currentEmployee, year - 1);

    // leave account
    GridBuilder sectionLeftGridBuilder = gridBuilder.newSplitPanel(GridSize.COL33);
    DivPanel sectionLeft = sectionLeftGridBuilder.getPanel();
    sectionLeft.add(new Heading1Panel(sectionLeft.newChildId(), I18nHelper.getLocalizedMessage("menu.vacation.leaveaccount")));

    appendFieldset(sectionLeftGridBuilder, "vacation.annualleave", statsModel, "vacationDaysInYearFromContractAsString", false);

    //student leave
    if (EmployeeStatus.STUD_ABSCHLUSSARBEIT.equals(employeeService.getEmployeeStatus(currentEmployee)) ||
            EmployeeStatus.STUDENTISCHE_HILFSKRAFT.equals(employeeService.getEmployeeStatus(currentEmployee))) {
      appendFieldset(sectionLeftGridBuilder, "vacation.countPerDay", employeeService.getStudentVacationCountPerDay(currentEmployee), false);
    }

    appendFieldset(sectionLeftGridBuilder, "vacation.previousyearleave", statsModel, "remainingLeaveFromPreviousYearAsString", false);

    BigDecimal subTotal = statsModel.getObject().getVacationDaysInYearFromContract();
    if (subTotal == null) {
      subTotal = BigDecimal.ZERO;
    }
    BigDecimal vacationdaysPreviousYear = statsModel.getObject().getRemainingLeaveFromPreviousYear();
    if (vacationdaysPreviousYear != null) {
      subTotal = subTotal.add(vacationdaysPreviousYear);
    }
    appendFieldset(sectionLeftGridBuilder, "vacation.subtotal", statsModel.getObject().format(subTotal), true);

    appendFieldset(sectionLeftGridBuilder, "vacation.approvedvacation", statsModel, "vacationDaysApprovedAsString", false);

    appendFieldset(sectionLeftGridBuilder, "vacation.plannedvacation", statsModel, "vacationDaysInProgressAsString", false);

    appendFieldset(sectionLeftGridBuilder, "vacation.availablevacation", statsModel, "vacationDaysLeftInYearAsString", true);

    //middle
    GridBuilder sectionMiddleGridBuilder = gridBuilder.newSplitPanel(GridSize.COL33);
    DivPanel sectionMiddle = sectionMiddleGridBuilder.getPanel();
    sectionMiddle.add(new Heading1Panel(sectionMiddle.newChildId(), I18nHelper.getLocalizedMessage("vacation.isSpecial")));
    appendFieldset(sectionMiddleGridBuilder, "vacation.isSpecialPlaned", statsModel, "specialVacationDaysInProgressAsString", false);

    appendFieldset(sectionMiddleGridBuilder, "vacation.isSpecialApproved", statsModel, "specialVacationDaysApprovedAsString", false);

    sectionMiddle.add(new Heading1Panel(sectionMiddle.newChildId(), I18nHelper.getLocalizedMessage("vacation.previousyearleave")));
    appendFieldset(sectionLeftGridBuilder, "vacation.previousyearleaveused", statsModel, "remainingLeaveFromPreviousYearAllocatedAsString", false);
    String endDatePreviousYearVacationString = endDatePreviousYearVacation.getDayOfMonth() + "." + endDatePreviousYearVacation.getMonthValue() + "." + year;
    appendFieldset(sectionLeftGridBuilder, "vacation.previousyearleaveunused", statsModel, "remainingLeaveFromPreviousYearUnusedAsString", false,
            endDatePreviousYearVacationString);

    // right
    GridBuilder sectionRightGridBuilder = gridBuilder.newSplitPanel(GridSize.COL33);
    DivPanel sectionRight = sectionRightGridBuilder.getPanel();

    sectionRight.add(new Heading1Panel(sectionRight.newChildId(), I18nHelper.getLocalizedMessage("vacation.leaveOfYear", String.valueOf(year - 1))));
    appendFieldset(sectionRightGridBuilder, "vacation.remainingLeaveFromYear", previousYearStatsModel, "remainingLeaveFromPreviousYearAllocatedAsString", false,
            String.valueOf(year - 2));
    appendFieldset(sectionRightGridBuilder, "vacation.approvedVacationInYear", previousYearStatsModel, "vacationDaysApprovedAsString", false,
            String.valueOf(year - 1));
    appendFieldset(sectionRightGridBuilder, "vacation.approvedSpecialVacationInYear", previousYearStatsModel, "specialVacationDaysApprovedAsString", false,
            String.valueOf(year - 1));


    // bottom list
    GridBuilder sectionBottomGridBuilder = gridBuilder.newSplitPanel(GridSize.COL100);
    DivPanel sectionBottom = sectionBottomGridBuilder.getPanel();
    if (showAddButton) {
      final PageParameters pageParameter = new PageParameters();
      pageParameter.add("employeeId", currentEmployee.getId());
      LinkPanel addLink = new LinkPanel(sectionBottom.newChildId(), I18nHelper.getLocalizedMessage("add"), VacationEditPage.class, returnToPage, pageParameter) {
        @Override
        public void onClick() {
          statsModel.clear();
        }
      };
      addLink.addLinkAttribute("class", "btn btn-sm btn-success bottom-xs-gap");
      sectionBottom.add(addLink);
    }
    if (showRecalculateButton) {
      final PageParameters pageParameter = new PageParameters();
      pageParameter.add("employeeId", currentEmployee.getId());
      LinkPanel addLink = new LinkPanel(sectionBottom.newChildId(), I18nHelper.getLocalizedMessage("recalculate"), returnToPage.getClass(), returnToPage, pageParameter) {
        @Override
        public void onClick() {
          // Force recalculation of remaining leave (carry from previous year):
          remainingLeaveDao.internalMarkAsDeleted(currentEmployee.getId(), year);
          statsModel.clear();
        }
      };
      addLink.addLinkAttribute("class", "btn btn-sm btn-success bottom-xs-gap");
      sectionBottom.add(addLink);
    }
    int nowYear = Year.now().getValue();
    addLeaveTable(statsModel, returnToPage, sectionBottom, currentEmployee, nowYear);
    addLeaveTable(statsModel, returnToPage, sectionBottom, currentEmployee, nowYear - 1);
  }

  private void addLeaveTable(final VacationStatsModel statsModel, WebPage returnToPage, DivPanel sectionBottom, EmployeeDO currentEmployee, int year) {
    sectionBottom.add(new Heading3Panel(sectionBottom.newChildId(),
            I18nHelper.getLocalizedMessage("vacation.title.list") + " " + year));
    TablePanel tablePanel = new TablePanel(sectionBottom.newChildId());
    sectionBottom.add(tablePanel);
    final DataTable<VacationDO, String> dataTable = createDataTable(createColumns(statsModel, returnToPage), "startDate", SortOrder.ASCENDING,
            currentEmployee, year);
    tablePanel.add(dataTable);
  }

  private DataTable<VacationDO, String> createDataTable(final List<IColumn<VacationDO, String>> columns,
                                                        final String sortProperty, final SortOrder sortOrder, final EmployeeDO employee, int year) {
    final SortParam<String> sortParam = sortProperty != null
            ? new SortParam<String>(sortProperty, sortOrder == SortOrder.ASCENDING) : null;
    return new DefaultDataTable<VacationDO, String>(TablePanel.TABLE_ID, columns,
            createSortableDataProvider(sortParam, employee, year), 50);
  }

  private ISortableDataProvider<VacationDO, String> createSortableDataProvider(final SortParam<String> sortParam,
                                                                               EmployeeDO employee, int year) {
    return new VacationViewPageSortableDataProvider<VacationDO>(sortParam, vacationService, employee, year);
  }

  private List<IColumn<VacationDO, String>> createColumns(final VacationStatsModel statsModel, WebPage returnToPage) {
    final List<IColumn<VacationDO, String>> columns = new ArrayList<IColumn<VacationDO, String>>();

    final CellItemListener<VacationDO> cellItemListener = new CellItemListener<VacationDO>() {
      private static final long serialVersionUID = 1L;

      @Override
      public void populateItem(final Item<ICellPopulator<VacationDO>> item, final String componentId,
                               final IModel<VacationDO> rowModel) {
        //Nothing to do here
      }
    };
    columns.add(new CellItemListenerPropertyColumn<VacationDO>(VacationDO.class, "startDate", "startDate", cellItemListener) {
      @Override
      public void populateItem(final Item<ICellPopulator<VacationDO>> item, final String componentId,
                               final IModel<VacationDO> rowModel) {
        final VacationDO vacation = rowModel.getObject();
        item.add(new ListSelectActionPanel(componentId, rowModel, VacationEditPage.class, vacation.getId(),
                returnToPage, DateTimeFormatter.instance().getFormattedDate(vacation.getStartDate())) {
          @Override
          public void onClick() {
            statsModel.clear();
          }
        });
        cellItemListener.populateItem(item, componentId, rowModel);
        final Item<?> row = (item.findParent(Item.class));
        WicketUtils.addRowClick(row);
      }
    });

    columns.add(new CellItemListenerPropertyColumn<VacationDO>(VacationDO.class, "endDate", "endDate", cellItemListener) {
      @Override
      public void populateItem(final Item<ICellPopulator<VacationDO>> item, final String componentId,
                               final IModel<VacationDO> rowModel) {
        final VacationDO vacation = rowModel.getObject();
        item.add(new TextPanel(componentId, DateTimeFormatter.instance().getFormattedDate(vacation.getEndDate())));
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    });
    columns.add(new CellItemListenerPropertyColumn<>(VacationDO.class, "status", "status", cellItemListener));
    columns.add(new CellItemListenerLambdaColumn<>(new ResourceModel("vacation.workingdays"),
            rowModel -> vacationService.getVacationDays(rowModel.getObject().getStartDate(), rowModel.getObject().getEndDate(), rowModel.getObject().getHalfDayBegin()),
            cellItemListener)
    );

    columns.add(new CellItemListenerPropertyColumn<VacationDO>(VacationDO.class, "special", "special", cellItemListener) {
      @Override
      public void populateItem(final Item<ICellPopulator<VacationDO>> item, final String componentId,
                               final IModel<VacationDO> rowModel) {
        final VacationDO vacation = rowModel.getObject();
        if (vacation.getSpecial() != null && vacation.getSpecial() == Boolean.TRUE) {
          item.add(new TextPanel(componentId, I18nHelper.getLocalizedMessage("yes")));
        } else {
          item.add(new TextPanel(componentId, I18nHelper.getLocalizedMessage("no")));
        }
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    });
    return columns;
  }

  private boolean appendFieldset(GridBuilder gridBuilder, final String label, final Model<VacationStats> statsModel, final String property, final boolean bold, final String... labelParameters) {
    final FieldsetPanel fs = gridBuilder.newFieldset(I18nHelper.getLocalizedMessage(label, (Object[]) labelParameters)).suppressLabelForWarning();
    DivTextPanel divTextPanel = new DivTextPanel(fs.newChildId(), new PropertyModel<>(statsModel, property));
    return appendFieldset(label, fs, bold, divTextPanel);
  }

  private boolean appendFieldset(GridBuilder gridBuilder, final String label, final String value, final boolean bold,final String... labelParameters) {
    final FieldsetPanel fs = gridBuilder.newFieldset(I18nHelper.getLocalizedMessage(label, (Object[]) labelParameters)).suppressLabelForWarning();
    DivTextPanel divTextPanel = new DivTextPanel(fs.newChildId(), value);
    return appendFieldset(label, fs, bold, divTextPanel);
  }

  private boolean appendFieldset(final String label, final FieldsetPanel fs, final boolean bold,final DivTextPanel divTextPanel) {
    WebMarkupContainer fieldset = fs.getFieldset();
    fieldset.add(AttributeAppender.append("class", "vacationPanel"));
    if (bold) {
      WebMarkupContainer fieldsetLabel = (WebMarkupContainer) fieldset.get("label");
      WebMarkupContainer fieldsetControls = (WebMarkupContainer) fieldset.get("controls");
      fieldsetLabel.add(AttributeModifier.replace("style", "font-weight: bold;"));
      fieldsetControls.add(AttributeModifier.replace("style", "font-weight: bold;"));
    }
    fs.add(divTextPanel);
    return true;
  }

  private class VacationStatsModel extends Model<VacationStats> {
    VacationStats stats;
    EmployeeDO employeeDO;
    int year;

    VacationStatsModel(EmployeeDO employeeDO, int year) {
      this.employeeDO = employeeDO;
      this.year = year;
    }

    @Override
    public VacationStats getObject() {
      if (stats == null) {
        stats = vacationService.getVacationStats(employeeDO, year);
      }
      return stats;
    }

    public void clear() {
      stats = null;
    }
  }
}
