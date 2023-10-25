package com.alibaba.csp.sentinel.dashboard.datasource.entity.report;

public class ReportEntity {
    public static final int GT = 1;
    public static final int LT = -1;
    public static final int TOTAL_QPS = 0;
    public static final int RT = 1;

    public static final int SUCCESS_QPS = 2;

    public static final int EXCEPTION_QPS = 3;

    public static final int BLOCK_QPS = 4;

    public static final int PASS_QPS = 5;


    private Long id;

    private String app;//组件名

    private String resource;//资源名

    private int condition;//条件 1大于 -1:小于

    private int method;//方法 0:总qps 1:响应时间rt 2:successQps 3:exceptionQps 4:blockQps 5:passQps

    private Long count;//总数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public ReportEntity() {
    }

    public ReportEntity(Long id, String app, String resource, int condition, int method, Long count) {
        this.id = id;
        this.app = app;
        this.resource = resource;
        this.condition = condition;
        this.method = method;
        this.count = count;
    }
}
