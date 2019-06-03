package com.crossover.mobiliza.app.data.remote;

public class Resource<T> {

    private final Status status;
    private final T data;
    private final String errorMessage;

    private Resource(Status status, T data, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(T data, String errorMessage) {
        return new Resource<>(Status.ERROR, data, errorMessage);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public enum Status {
        ERROR, LOADING, SUCCESS
    }
}