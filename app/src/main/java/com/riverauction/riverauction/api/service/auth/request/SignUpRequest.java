package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CUserType;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class SignUpRequest {

    public SignUpRequest(CUserType type, EmailCredentialRequest emailCredentialRequest,
                         StudentBasicInformationRequest studentBasicInformationRequest,
                         StudentLessonInformationRequest studentLessonInformationRequest,
                         TeacherBasicInformationRequest teacherBasicInformationRequest,
                         TeacherLessonInformationRequest teacherLessonInformationRequest) {
        this.type = type;
        this.emailCredentialRequest = emailCredentialRequest;
        this.studentBasicInformationRequest = studentBasicInformationRequest;
        this.studentLessonInformationRequest = studentLessonInformationRequest;
        this.teacherBasicInformationRequest = teacherBasicInformationRequest;
        this.teacherLessonInformationRequest = teacherLessonInformationRequest;
    }

    @JsonProperty("type")
    private CUserType type;

    @JsonProperty("email_credential")
    private EmailCredentialRequest emailCredentialRequest;

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
        private EmailCredentialRequest emailCredentialRequest;
        private StudentBasicInformationRequest studentBasicInformationRequest;
        private StudentLessonInformationRequest studentLessonInformationRequest;
        private TeacherBasicInformationRequest teacherBasicInformationRequest;
        private TeacherLessonInformationRequest teacherLessonInformationRequest;

        public Builder setType(CUserType type) {
            this.type = type;
            return this;
        }

        public Builder setEmailCredentialRequest(EmailCredentialRequest emailCredentialRequest) {
            this.emailCredentialRequest = emailCredentialRequest;
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

        public SignUpRequest build() {
            return new SignUpRequest(type, emailCredentialRequest, studentBasicInformationRequest, studentLessonInformationRequest, teacherBasicInformationRequest, teacherLessonInformationRequest);
        }
    }
}
