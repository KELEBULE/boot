package com.izpan.common.constants;

import java.util.List;

public final class BasePermissionConstant {

    private BasePermissionConstant() {
    }

    public static final Long PERMISSION_USER_INFO = 2017188988086910977L;

    public static final Long PERMISSION_USER_ROUTE = 2017600000000000001L;

    public static final Long PERMISSION_ALL_DICT_MAP = 2017600000000000002L;

    public static final List<Long> BASE_PERMISSION_IDS = List.of(
            PERMISSION_USER_INFO,
            PERMISSION_USER_ROUTE,
            PERMISSION_ALL_DICT_MAP
    );
}
