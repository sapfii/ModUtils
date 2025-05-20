package net.sapfii.modutils.features.reportdisplay;

import java.util.ArrayList;
import java.util.Collection;

public class ReportList extends ArrayList<Report> {
    @Override
    public boolean add(Report report) {
        boolean val = super.add(report);
        updateIndices();
        return val;
    }

    @Override
    public void add(int index, Report element) {
        super.add(index, element);
        updateIndices();
    }

    @Override
    public void addFirst(Report element) {
        super.addFirst(element);
        updateIndices();
    }

    @Override
    public void addLast(Report element) {
        super.addLast(element);
        updateIndices();
    }

    @Override
    public boolean addAll(Collection<? extends Report> c) {
        boolean val = super.addAll(c);
        updateIndices();
        return val;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Report> c) {
        boolean val = super.addAll(index, c);
        updateIndices();
        return val;
    }

    @Override
    public Report remove(int index) {
        Report val = super.remove(index);
        updateIndices();
        return val;
    }

    @Override
    public boolean remove(Object o) {
        boolean val = super.remove(o);
        updateIndices();
        return val;
    }

    @Override
    public Report removeFirst() {
        Report val = super.removeFirst();
        updateIndices();
        return val;
    }

    @Override
    public Report removeLast() {
        Report val = super.removeFirst();
        updateIndices();
        return val;
    }

    private void updateIndices() {
        for (int i = 0; i < this.size(); ++i) {
            Report report = this.get(i);
            report.data.index = i;
        }
    }
}
