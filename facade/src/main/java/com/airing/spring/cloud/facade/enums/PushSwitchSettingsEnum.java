package com.airing.spring.cloud.facade.enums;

import java.util.HashMap;
import java.util.Map;

public enum PushSwitchSettingsEnum {
    DELIVERED(1, "delivered", "发货"),
    DISPATCHING(1 << 1, "dispatching", "派件"),
    SIGNED(1 << 2, "signed", "签收"),
    EXCEPTION(1 << 3, "exception", "异常"),
    CLEAR(1 << 4, "clear", "清关"),
    CLEAR_SUC(1 << 5, "clearSuc", "清关成功"),
    EXPRESS_STAT(1 << 6, "expressStat", "今日包裹统计"),
    DISPATCH_REMIND(1 << 7, "dispatchRemind", "每日派奖提醒"),
    ;

    private int decimalIdx;
    private String key;
    private String desc;

    public int getDecimalIdx() {
        return decimalIdx;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    PushSwitchSettingsEnum(int decimalIdx, String key, String desc) {
        this.decimalIdx = decimalIdx;
        this.key = key;
        this.desc = desc;
    }

    /**
     * 判断指定的开关是否打开
     *
     * @param e 指定开关
     * @param totalVal 开关状态的总值
     * @return boolean
     * @author GEYI
     * @date 2021年05月12日 16:35
     */
    public static boolean getSwitchStatus(PushSwitchSettingsEnum e, int totalVal) {
        return (e.getDecimalIdx() & totalVal) == e.getDecimalIdx();
    }

    /**
     * 设置指定开关的状态
     *
     * @param e
     * @param status
     * @param totalVal
     * @return int
     * @author GEYI
     * @date 2021年05月12日 16:52
     */
    public static int setSwitchStatus(PushSwitchSettingsEnum e, boolean status, int totalVal) {
        boolean switchStatus = getSwitchStatus(e, totalVal);
        if (status != switchStatus) {
            if (status) {
                totalVal = totalVal | e.getDecimalIdx();
            } else {
                totalVal = totalVal ^ e.getDecimalIdx();
            }
        }
        return totalVal;
    }


    /**
     * 根据开关状态的总值解析出所有的开关状态
     *
     * @param totalVal
     * @return java.util.Map<java.lang.String,java.lang.Boolean>
     * @author GEYI
     * @date 2021年05月12日 16:45
     */
    public static Map<String, Boolean> getAllSwitchStatus(int totalVal) {
        Map<String, Boolean> ret = new HashMap<>();
        for (PushSwitchSettingsEnum value : PushSwitchSettingsEnum.values()) {
            boolean status = (value.getDecimalIdx() & totalVal) == value.getDecimalIdx();
            ret.put(value.getKey(), status);
        }
        return ret;
    }

    /**
     * 根据给定的开关状态计算出开关状态的总值
     *
     * @param statusMap
     * @return int
     * @author GEYI
     * @date 2021年05月12日 17:24
     */
    public static int getTotalVal(Map<String, Boolean> statusMap) {
        int totalVal = 0;
        if (statusMap == null || statusMap.isEmpty()) {
            return totalVal;
        }
        for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
            String key = entry.getKey();
            PushSwitchSettingsEnum e = getByKey(key);
            if (e == null) {
                throw new RuntimeException("不存在的推送开关");
            }
            Boolean value = entry.getValue();
            if (value) {
                totalVal = totalVal | e.getDecimalIdx();
            }
        }
        return totalVal;
    }

    public static int getOpenTotalVal() {
        int totalVal = 0;
        for (PushSwitchSettingsEnum value : PushSwitchSettingsEnum.values()) {
            totalVal = totalVal | value.getDecimalIdx();
        }
        return totalVal;
    }

    public static PushSwitchSettingsEnum getByKey(String key) {
        for (PushSwitchSettingsEnum value : PushSwitchSettingsEnum.values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println(PushSwitchSettingsEnum.getSwitchStatus(PushSwitchSettingsEnum.DISPATCHING, 10));
        System.out.println(PushSwitchSettingsEnum.getAllSwitchStatus(35));

        int totalVal = 35;
        System.out.println(totalVal = PushSwitchSettingsEnum.setSwitchStatus(PushSwitchSettingsEnum.DELIVERED, true, totalVal));
        System.out.println(totalVal = PushSwitchSettingsEnum.setSwitchStatus(PushSwitchSettingsEnum.DELIVERED, false, totalVal));
        System.out.println(totalVal = PushSwitchSettingsEnum.setSwitchStatus(PushSwitchSettingsEnum.DISPATCHING, false, totalVal));
        System.out.println(totalVal = PushSwitchSettingsEnum.setSwitchStatus(PushSwitchSettingsEnum.CLEAR, true, totalVal));

        Map<String, Boolean> statusMap = new HashMap<>();
        statusMap.put(PushSwitchSettingsEnum.SIGNED.getKey(), true);
        statusMap.put(PushSwitchSettingsEnum.EXCEPTION.getKey(), true);
        statusMap.put(PushSwitchSettingsEnum.CLEAR.getKey(), true);
        statusMap.put(PushSwitchSettingsEnum.CLEAR_SUC.getKey(), true);
        statusMap.put(PushSwitchSettingsEnum.DELIVERED.getKey(), true);
        statusMap.put(PushSwitchSettingsEnum.DISPATCHING.getKey(), true);
        System.out.println(PushSwitchSettingsEnum.getTotalVal(statusMap));

        System.out.println(PushSwitchSettingsEnum.getOpenTotalVal());
    }
}
