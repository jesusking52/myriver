package com.riverauction.riverauction.eventbus;

import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;

public class TeacherFilterEvent {
    private GetTeachersParams.Builder builder;
    public TeacherFilterEvent() {
    }

    public TeacherFilterEvent(GetTeachersParams.Builder builder) {
        this.builder = builder;
    }

    public GetTeachersParams.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(GetTeachersParams.Builder builder) {
        this.builder = builder;
    }
}
