package com.riverauction.riverauction.api.service.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.StudentBasicInformationRequest;
import com.riverauction.riverauction.api.service.auth.request.StudentLessonInformationRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherBasicInformationRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherLessonInformationRequest;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class UserPatchRequest {

    public UserPatchRequest(CUserType type,
                         StudentBasicInformationRequest studentBasicInformationRequest,
                         StudentLessonInformationRequest studentLessonInformationRequest,
                         TeacherBasicInformationRequest teacherBasicInformationRequest,
                         TeacherLessonInformationRequest teacherLessonInformationRequest
    ) {
        this.type = type;
        this.studentBasicInformationRequest = studentBasicInformationRequest;
        this.studentLessonInformationRequest = studentLessonInformationRequest;
        this.teacherBasicInformationRequest = teacherBasicInformationRequest;
        this.teacherLessonInformationRequest = teacherLessonInformationRequest;
    }

    @JsonProperty("type")
    private CUserType type;

    @JsonProperty("student_basic_information")
    private StudentBasicInformationRequest studentBasicInformationRequest;

    @JsonProperty("student_lesson_information")
    private StudentLessonInformationRequest studentLessonInformationRequest;

    @JsonProperty("teacher_basic_information")
    private TeacherBasicInformationRequest teacherBasicInformationRequest;

    @JsonProperty("teacher_lesson_information")
    private TeacherLessonInformationRequest teacherLessonInformationRequest;

    public static class Builder {
        private CUserType type;
        private StudentBasicInformationRequest studentBasicInformationRequest;
        private StudentLessonInformationRequest studentLessonInformationRequest;
        private TeacherBasicInformationRequest teacherBasicInformationRequest;
        private TeacherLessonInformationRequest teacherLessonInformationRequest;

        public Builder setType(CUserType type) {
            this.type = type;
            return this;
        }

        public Builder setStudentBasicInformationRequest(StudentBasicInformationRequest studentBasicInformationRequest) {
            this.studentBasicInformationRequest = studentBasicInformationRequest;
            return this;
        }

        public Builder setStudentLessonInformationRequest(StudentLessonInformationRequest studentLessonInformationRequest) {
            this.studentLessonInformationRequest = studentLessonInformationRequest;
            return this;
        }

        public Builder setTeacherBasicInformationRequest(TeacherBasicInformationRequest teacherBasicInformationRequest) {
            this.teacherBasicInformationRequest = teacherBasicInformationRequest;
            return this;
        }

        public Builder setTeacherLessonInformationRequest(TeacherLessonInformationRequest teacherLessonInformationRequest) {
            this.teacherLessonInformationRequest = teacherLessonInformationRequest;
            return this;
        }

        public UserPatchRequest build() {
            return new UserPatchRequest(type, studentBasicInformationRequest, studentLessonInformationRequest, teacherBasicInformationRequest, teacherLessonInformationRequest);
        }
    }
}
