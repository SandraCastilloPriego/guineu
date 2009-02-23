/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.taskcontrol.impl;

import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskPriority;
import guineu.taskcontrol.Task.TaskStatus;
import guineu.util.components.LabeledProgressBar;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


/**
 * Task queue
 */
class TaskQueue implements TableModel, BoundedRangeModel {

    private static final int DEFAULT_CAPACITY = 64;

    private WrappedTask[] queue;
    private int size, capacity;

    private HashSet<EventListener> listeners;

    TaskQueue() {

        size = 0;
        capacity = DEFAULT_CAPACITY;
        queue = new WrappedTask[capacity];
        listeners = new HashSet<EventListener>();

    }

    synchronized void addWrappedTask(WrappedTask task) {

        if (size == capacity) {
            capacity *= 2;
            WrappedTask[] temp = new WrappedTask[capacity];
            // copy the old queue to the new one
            for (int i = 0; i < queue.length; i++)
                temp[i] = queue[i];
            queue = temp;
        }

        queue[size] = task;
        size++;

        resort();
        fireRowsChanged();

    }

    synchronized void clear() {
        size = 0;
        capacity = DEFAULT_CAPACITY;
        queue = new WrappedTask[capacity];
        fireRowsChanged();
    }

    synchronized void resort() {
        Arrays.sort(queue, 0, size);
    }

    synchronized void refresh() {

        resort();

        ChangeEvent ce = new ChangeEvent(this);
        TableModelEvent te = new TableModelEvent(this, 0, size - 1);

        Iterator<EventListener> listenerIterator = listeners.iterator();

        while (listenerIterator.hasNext()) {

            EventListener eventListener = listenerIterator.next();

            if (eventListener instanceof ChangeListener)
                ((ChangeListener) eventListener).stateChanged(ce);

            if (eventListener instanceof TableModelListener)
                ((TableModelListener) eventListener).tableChanged(te);

        }
    }

    synchronized WrappedTask getWrappedTask(Task t) {

        for (int i = 0; i < size; i++)
            if (queue[i].getTask() == t)
                return queue[i];

        return null;

    }

    synchronized WrappedTask getWrappedTask(int index) {

        if ((index < 0) || (index >= size))
            return null;
        else
            return queue[index];

    }

    synchronized boolean isEmpty() {
        return size == 0;
    }

    synchronized boolean allTasksFinished() {
        for (int i = 0; i < size; i++) {
            TaskStatus status = queue[i].getTask().getStatus();
            if ((status == TaskStatus.PROCESSING)
                    || (status == TaskStatus.WAITING))
                return false;
        }
        return true;
    }

    synchronized WrappedTask[] getQueueSnapshot() {

        WrappedTask[] snapshot = new WrappedTask[size];
        for (int i = 0; i < size; i++)
            snapshot[i] = queue[i];
        return snapshot;

    }

    private void fireRowsChanged() {

        ChangeEvent ce = new ChangeEvent(this);
        TableModelEvent te = new TableModelEvent(this);

        Iterator<EventListener> listenerIterator = listeners.iterator();

        while (listenerIterator.hasNext()) {

            EventListener eventListener = listenerIterator.next();

            if (eventListener instanceof ChangeListener)
                ((ChangeListener) eventListener).stateChanged(ce);

            if (eventListener instanceof TableModelListener)
                ((TableModelListener) eventListener).tableChanged(te);

        }

    }

    /* TableModel implementation */

    private static final String columns[] = { "Item", "Priority", "Status",
            "% done" };

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return size;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns.length;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public synchronized Object getValueAt(int row, int column) {

        if (row < size) {

            WrappedTask task = queue[row];

            switch (column) {
            case 0:
                return task.getTask().getTaskDescription();
            case 1:
                return task.getPriority();
            case 2:
                return task.getTask().getStatus();
            case 3:
                return new LabeledProgressBar(task.getTask().getFinishedPercentage());
            }
        }

        return null;

    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return String.class;
        case 1:
            return TaskPriority.class;
        case 2:
            return TaskStatus.class;
        case 3:
            return LabeledProgressBar.class;
        }
        return null;

    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object val, int row, int col) {
        // do nothing
    }

    /**
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    /* BoundedRangeModel implementation */

    /**
     * @see javax.swing.BoundedRangeModel#getMinimum()
     */
    public int getMinimum() {
        return 0;
    }

    /**
     * @see javax.swing.BoundedRangeModel#setMinimum(int)
     */
    public void setMinimum(int min) {
        // do nothing
    }

    /**
     * @see javax.swing.BoundedRangeModel#getMaximum()
     */
    public int getMaximum() {
        return size * 100;
    }

    /**
     * @see javax.swing.BoundedRangeModel#setMaximum(int)
     */
    public void setMaximum(int max) {
        // do nothing

    }

    /**
     * @see javax.swing.BoundedRangeModel#getValue()
     */
    public int getValue() {
        int value = 0;
        for (int i = 0; i < size; i++) {
            Task t = queue[i].getTask();
            value += (t.getFinishedPercentage() * 100);
        }
        return value;
    }

    /**
     * @see javax.swing.BoundedRangeModel#setValue(int)
     */
    public void setValue(int val) {
        // do nothing
    }

    /**
     * @see javax.swing.BoundedRangeModel#setValueIsAdjusting(boolean)
     */
    public void setValueIsAdjusting(boolean adjusting) {
        // do nothing
    }

    /**
     * @see javax.swing.BoundedRangeModel#getValueIsAdjusting()
     */
    public boolean getValueIsAdjusting() {
        return false;
    }

    /**
     * @see javax.swing.BoundedRangeModel#getExtent()
     */
    public int getExtent() {
        return 1;
    }

    /**
     * @see javax.swing.BoundedRangeModel#setExtent(int)
     */
    public void setExtent(int ext) {
        // do nothing

    }

    /**
     * @see javax.swing.BoundedRangeModel#setRangeProperties(int, int, int, int,
     *      boolean)
     */
    public void setRangeProperties(int arg0, int arg1, int arg2, int arg3,
            boolean arg4) {
        // do nothing
    }

    /**
     * @see javax.swing.BoundedRangeModel#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * @see javax.swing.BoundedRangeModel#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

}
