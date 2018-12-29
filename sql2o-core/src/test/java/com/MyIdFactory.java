package com;

import scorpio.core.IdDefined;
import scorpio.utils.UUIDUtils;

public class MyIdFactory implements IdDefined {
    @Override
    public String getId() {
        return "t_option"+UUIDUtils.get();
    }
}
