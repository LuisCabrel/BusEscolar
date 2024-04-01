package com.programadoreshuacho.busescolar.Variables;

import android.app.Application;

public class VariablesGlobales extends Application {
    private String UrlApi ="http://192.168.0.11/apiBusEscolar/api/";
    private String Token;

    public String getUrlApi() {
        return UrlApi;
    }

    public void setUrlApi(String urlApi) {
        UrlApi = urlApi;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }


}
