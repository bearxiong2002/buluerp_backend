package com.ruoyi.web.util.log;

import java.util.Date;

public interface OperationLog {
    String OPERATOR_SYSTEM = "系统";
    String OPERATOR_UNKNOWN = "未知";

    Date getOperationTime();
    String getOperationType();
    String getRecordId();
    String getOperator();
    String getDetails();

    // 便于手动记录日志
    public static class Builder {
        private Date operationTime;
        private String operationType;
        private String recordId;
        private String operator;
        private String details;

        public Builder operationTime(Date operationTime) {
            this.operationTime = operationTime;
            return this;
        }

        public Builder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder recordId(String recordId) {
            this.recordId = recordId;
            return this;
        }

        public Builder operator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public OperationLog build() {
            return new OperationLog() {
                @Override
                public Date getOperationTime() {
                    return operationTime;
                }

                @Override
                public String getOperationType() {
                    return operationType;
                }

                @Override
                public String getRecordId() {
                    return recordId;
                }

                @Override
                public String getOperator() {
                    return operator;
                }

                @Override
                public String getDetails() {
                    return details;
                }
            };
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
