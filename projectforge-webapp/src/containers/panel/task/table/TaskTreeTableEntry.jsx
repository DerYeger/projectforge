import classNames from 'classnames';
import PropTypes from 'prop-types';
import React from 'react';
import Formatter from '../../../../components/base/Formatter';
import ConsumptionBar from '../ConsumptionBar';
import TaskTreeContext from '../TaskTreeContext';
import styles from '../TaskTreePanel.module.scss';
import TaskTreeTableEntryIcon from './TaskTreeTableEntryIcon';

function TaskTreeTableEntry({ task, consumptionBarClickable }) {
    const {
        columnsVisibility,
        highlightTaskId,
        shortForm,
        selectTask,
    } = React.useContext(TaskTreeContext);

    const { id } = task;

    const handleRowClick = () => selectTask(id, task);

    return (
        <tr
            onClick={handleRowClick}
            className={classNames(styles.task, { [styles.highlighted]: highlightTaskId === id })}
        >
            <td style={{ paddingLeft: `${task.indent * 1.5 + 0.75}rem` }}>
                <TaskTreeTableEntryIcon
                    treeStatus={task.treeStatus}
                    taskId={id}
                />
                {task.title}
            </td>
            <td>
                <ConsumptionBar
                    progress={task.consumption}
                    taskId={consumptionBarClickable ? id : undefined}
                    identifier="task-tree-entry-consumption-bar"
                />
            </td>

            {columnsVisibility.kost2 ? <td>...</td> : undefined}

            {!shortForm && columnsVisibility.orders ? <td>...</td> : undefined}

            <td>{task.shortDescription}</td>

            {!shortForm ? (
                <>

                    {columnsVisibility.protectionUntil ? (
                        <td>
                            <Formatter
                                formatter="DATE"
                                data={task.protectTimesheetsUntil}
                                id="date"
                            />
                        </td>
                    ) : undefined}

                    {columnsVisibility.reference ? <td>{task.reference}</td> : undefined}

                    {columnsVisibility.priority ? <td>{task.priority}</td> : undefined}

                    <td>{task.status}</td>

                    {columnsVisibility.assignedUser ? (
                        <td>{task.responsibleUser ? task.responsibleUser.fullname : ''}</td>
                    ) : undefined}

                </>
            ) : undefined}
        </tr>
    );
}

TaskTreeTableEntry.propTypes = {
    task: PropTypes.shape({
        id: PropTypes.number.isRequired,
        indent: PropTypes.number.isRequired,
        title: PropTypes.string.isRequired,
        treeStatus: PropTypes.oneOf(['OPENED', 'CLOSED', 'LEAF']).isRequired,
        consumption: PropTypes.shape({}),
        protectTimesheetsUntil: PropTypes.string,
        responsibleUser: PropTypes.shape({
            fullname: PropTypes.string,
        }),
        shortDescription: PropTypes.string,
        status: PropTypes.string,
    }).isRequired,
    /* If clickable a click on the consumption bar redirects to task view. */
    consumptionBarClickable: PropTypes.bool,
};

TaskTreeTableEntry.defaultProps = {
    consumptionBarClickable: undefined,
};

export default TaskTreeTableEntry;
