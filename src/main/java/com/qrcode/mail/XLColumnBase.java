package com.qrcode.mail;

import io.poi.model.annotations.SheetColumn;

public class XLColumnBase {
    @SheetColumn("Execution Date Time")
    private String execDateTime;

    public String getExecDateTime() {
        return execDateTime;
    }

    public void setExecDateTime(String execDateTime) {
        this.execDateTime = execDateTime;
    }
}
