package com.ruoyi.wcs.enums.wcs;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hewei
 * @date 2023/4/13 0013 09:36
 */
@Getter
public enum WcsWeekEnum {
    /**
     * 周一
     */
    Monday("星期一", "Mon"),
    /**
     * 周二
     */
    Tuesday("星期二", "Tue"),
    /**
     * 周三
     */
    Wednesday("星期三", "Wed"),
    /**
     * 周四
     */
    Thursday("星期四", "Thu"),
    /**
     * 周五
     */
    Friday("星期五", "Fri"),
    /**
     * 周六
     */
    Saturday("星期六", "Sat"),
    /**
     * 周日
     */
    Sunday("星期日", "Sun"),
    ;

    private static final Map<String, WcsWeekEnum> CODE_ROLE_MAP = new HashMap<>();

    private static final Map<String, WcsWeekEnum> NAME_ROLE_MAP = new HashMap<>();

    static {
        for (WcsWeekEnum type : WcsWeekEnum.values()) {
            NAME_ROLE_MAP.put(type.name, type);
            CODE_ROLE_MAP.put(type.code, type);
        }
    }

    private final String name;

    private final String code;

    WcsWeekEnum(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * to WcsWeekEnum by code.
     *
     * @param code code
     * @return WcsWeekEnum
     */
    public static WcsWeekEnum codeOf(final String code) {
        if (StrUtil.isBlank(code)) {
            return Sunday;
        }
        WcsWeekEnum matchType = CODE_ROLE_MAP.get(code);
        return Objects.isNull(matchType) ? Sunday : matchType;
    }

    /**
     * to WcsWeekEnum by name.
     *
     * @param name name
     * @return WcsWeekEnum
     */
    public static WcsWeekEnum nameOf(final String name) {
        if (Strings.isNullOrEmpty(name)) {
            return Sunday;
        }
        WcsWeekEnum matchType = NAME_ROLE_MAP.get(name);
        return Objects.isNull(matchType) ? Sunday : matchType;
    }
}
