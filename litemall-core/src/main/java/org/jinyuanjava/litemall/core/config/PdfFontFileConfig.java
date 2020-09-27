package org.jinyuanjava.litemall.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdfFontFileConfig {
    @Autowired
    private PdfFontFileProperties pdfFontFileProperties;

    @Bean
    public String returnFontFilePath() {
		return pdfFontFileProperties.getFontFilePath();
    }

    @Bean
    public String returnActlogo() {
        return pdfFontFileProperties.getActlogo();
    }

    @Bean
    public String returnActQRCode() {
        return pdfFontFileProperties.getActQRCode();
    }

}
