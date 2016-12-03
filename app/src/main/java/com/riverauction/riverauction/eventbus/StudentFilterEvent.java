package com.riverauction.riverauction.eventbus;

import com.riverauction.riverauction.api.service.lesson.params.GetLessonsParams;

public class StudentFilterEvent {
    private GetLessonsParams.Builder builder;

    public StudentFilterEvent() {
    }

    public StudentFilterEvent(GetLessonsParams.Builder builder) {
        this.builder = builder;
    }

    public GetLessonsParams.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(GetLessonsParams.Builder builder) {
        this.builder = builder;
    }
}
