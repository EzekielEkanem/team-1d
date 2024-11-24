package edu.vassar.cmpu203.vassareats.model;

public interface IWebPageCallback {
    void onSuccess(String htmlContent);
    void onError(String errorMessage);
}
