package com.ooad.project.collabeditor.model;

public class ActivityMetrics {

    private long totalEvents;
    private long loginEvents;
    private long editEvents;
    private long createDocumentEvents;
    private long archiveDocumentEvents;

    public ActivityMetrics() {
    }

    public ActivityMetrics(long totalEvents, long loginEvents, long editEvents, long createDocumentEvents, long archiveDocumentEvents) {
        this.totalEvents = totalEvents;
        this.loginEvents = loginEvents;
        this.editEvents = editEvents;
        this.createDocumentEvents = createDocumentEvents;
        this.archiveDocumentEvents = archiveDocumentEvents;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getLoginEvents() {
        return loginEvents;
    }

    public void setLoginEvents(long loginEvents) {
        this.loginEvents = loginEvents;
    }

    public long getEditEvents() {
        return editEvents;
    }

    public void setEditEvents(long editEvents) {
        this.editEvents = editEvents;
    }

    public long getCreateDocumentEvents() {
        return createDocumentEvents;
    }

    public void setCreateDocumentEvents(long createDocumentEvents) {
        this.createDocumentEvents = createDocumentEvents;
    }

    public long getArchiveDocumentEvents() {
        return archiveDocumentEvents;
    }

    public void setArchiveDocumentEvents(long archiveDocumentEvents) {
        this.archiveDocumentEvents = archiveDocumentEvents;
    }
}
