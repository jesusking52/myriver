package com.riverauction.riverauction.api.service.auth;

import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.auth.request.CertifyPhoneNumberRequest;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.request.SignUpRequest;
import com.riverauction.riverauction.api.service.auth.response.IssueTokenResult;
import com.riverauction.riverauction.api.service.auth.response.SignUpResult;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface AuthService {

    @POST("/api/auth/sign_up")
    Observable<APISuccessResponse<SignUpResult>> signUp(@Body SignUpRequest request);

    @POST("/api/auth/issue_token")
    Observable<APISuccessResponse<IssueTokenResult>> issueToken(@Body EmailCredentialRequest request);

    @POST("/api/auth/phone_number/request_auth_number")
    Observable<APISuccessResponse<Boolean>> requestAuthNumber(@Body CertifyPhoneNumberRequest request);

    @POST("/api/auth/phone_number/certify_auth_number")
    Observable<APISuccessResponse<Boolean>> certifyAuthNumber(@Body CertifyPhoneNumberRequest request);
}
