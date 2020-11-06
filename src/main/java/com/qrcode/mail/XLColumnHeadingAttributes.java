
package com.qrcode.mail;

import io.poi.model.annotations.*;

import java.awt.image.BufferedImage;
@Sheet("Define")
public class XLColumnHeadingAttributes extends InputSourceStreamAttributes{
    @SheetColumn("ColumnType")
    private String columnType;
    @SheetColumn("Extension")
    private String extension;
    @SheetColumn("FontColor")
    private String fontColor;
    @SheetColumn("FontName")
    private String fontName;
    @SheetColumn("FontSize")
    private int fontSize;
    @SheetColumn("FontType")
    private int fontType;
    @SheetColumn("ColumnName")
    private String name;
    @SheetColumn("ImageName")
    private String imageName;
    @SheetColumn("width")
    private int width;
    @SheetColumn("height")
    private int height;
    @SheetColumn("X")
    private int x;
    @SheetColumn("Y")
    private int y;
    @SheetColumn("Scale")
    private int scale;
    @SheetColumn("Size")
    private int size;

    @SheetColumn("Border")
    private int border;

    private String value;


    @Override
    public int getBorder() {
        return border;
    }

    @Override
    public void setBorder(int border) {
        this.border = border;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontType() {
        return fontType;
    }

    public void setFontType(int fontType) {
        this.fontType = fontType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }


    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        result = 31 * result + (getName()!=null ? getName().hashCode() : 0 );
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this==obj) {
            return true;
        }
        if (obj==null){
            return false;
        }
        if (this.getClass()!=obj.getClass())
        {
            return false;
        }
        XLColumnHeadingAttributes attributes = (XLColumnHeadingAttributes) obj;
        if (this.getName().equals(attributes.getName()))
        {
            return true;
        }
        return false;
    }
}
