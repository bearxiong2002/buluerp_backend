package com.ruoyi.web.log;

import java.util.Date;

public interface OperationLog {
    Date getOperationTime();
    String getOperationType();
    String getOperator();
    String getDetails();
}
