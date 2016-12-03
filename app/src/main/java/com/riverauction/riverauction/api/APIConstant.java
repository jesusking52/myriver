package com.riverauction.riverauction.api;

import retrofit.Endpoint;
import retrofit.Endpoints;

public class APIConstant {

    public static final Endpoint API_ENDPOINT = Endpoints.newFixedEndpoint("http://api.matchingtutor.net/", "API");
    //public static final Endpoint API_ENDPOINT = Endpoints.newFixedEndpoint("http://52.78.88.0/", "API");

    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";
    public static final String KEY_RESULT = "data";
    public static final String KEY_PAGING = "paging";
}
