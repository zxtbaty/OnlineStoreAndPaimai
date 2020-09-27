package org.jinyuanjava.litemall.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "litemall.pdf")
public class PdfFontFileProperties {
    private String fontFilePath;
    private String actlogo;
    private String actQRCode;

    public String getFontFilePath() {
        return fontFilePath;
    }

    public void setFontFilePath(String fontFilePath) {
        this.fontFilePath = fontFilePath;
    }

    public String getActlogo() {
        return actlogo;
    }

    public void setActlogo(String actlogo) {
        this.actlogo = actlogo;
    }

    public String getActQRCode() {
        return actQRCode;
    }

    public void setActQRCode(String actQRCode) {
        this.actQRCode = actQRCode;
    }
}
