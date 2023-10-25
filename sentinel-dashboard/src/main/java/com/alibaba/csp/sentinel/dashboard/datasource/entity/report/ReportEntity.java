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
    private int method;// 方法 0:总请求次数 1:平均响应时间 2:成功请求数 3:异常请求数 4:拒绝请求数 5:通过请求数

    private Long count;//总数

    private int interval;//间隔时间

    private String email;//邮箱



    private String template;//模版

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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public ReportEntity() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
