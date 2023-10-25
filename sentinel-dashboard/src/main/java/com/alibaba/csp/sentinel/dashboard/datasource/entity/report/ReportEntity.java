package com.alibaba.csp.sentinel.dashboard.datasource.entity.report;

public class ReportEntity {
    private int id;

    private String app;//组件名

    private String resource;//资源名

    private int condition;//条件 1大于 -1:小于

    private int method;//方法 0:总qps 1:响应时间rt 2:successQps 3:exceptionQps 4:blockQps

    private Long count;//总数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public ReportEntity(int id, String app, String resource, int condition, int method, Long count) {
        this.id = id;
        this.app = app;
        this.resource = resource;
        this.condition = condition;
        this.method = method;
        this.count = count;
    }
}
