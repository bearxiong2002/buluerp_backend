package com.ruoyi.web.service.support;

import com.ruoyi.common.exception.ServiceException;
import java.util.*;

/** 所有手工录入及 Excel 导入共用编号规则。 */
public final class ProductInnerCodes {
    private ProductInnerCodes() { }

    public static List<String> normalize(List<String> values) {
        if (values == null || values.isEmpty()) throw new ServiceException("至少保留一个内部编号");
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String value : values) {
            String code = value == null ? "" : value.trim();
            if (code.isEmpty()) throw new ServiceException("内部编号不能为空");
            if (code.length() > 50) throw new ServiceException("内部编号不能超过50个字符：" + code);
            if (code.chars().anyMatch(c -> Character.isISOControl(c) || Character.isSurrogate((char)c))) {
                throw new ServiceException("内部编号不能包含换行、控制字符或表情符号");
            }
            if (!seen.add(code)) throw new ServiceException("内部编号重复：" + code);
            result.add(code);
        }
        return result;
    }

    public static List<String> forCreate(String primary, List<String> values, String additional) {
        List<String> codes = new ArrayList<>();
        if (values != null) {
            codes.addAll(values);
        } else {
            codes.add(primary);
        }
        if (additional != null && !additional.trim().isEmpty()) {
            codes.addAll(Arrays.asList(additional.split("\\r?\\n|\\r", -1)));
        }
        codes = normalize(codes);
        if (primary != null && !primary.trim().isEmpty() && !primary.trim().equals(codes.get(0))) {
            throw new ServiceException("默认内部编号必须是编号列表的第一项");
        }
        return codes;
    }
}
