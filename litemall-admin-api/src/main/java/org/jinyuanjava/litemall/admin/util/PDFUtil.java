package org.jinyuanjava.litemall.admin.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.jinyuanjava.litemall.core.config.PdfFontFileConfig;
import org.jinyuanjava.litemall.core.util.CharUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wz on 2018/8/16.
 */
@Component
public class PDFUtil extends PdfPageEventHelper {


    @Autowired
    private  PdfFontFileConfig pdfFontFileConfig;


    private static PDFUtil pdfUtil;

    public static final BaseColor GAINSBORO = new BaseColor(220, 220, 220);

    /**
     *注释用于在完成依赖项注入以执行任何初始化之后需要执行的方法。必须在类投入使用之前调用此方法。
     */
    @PostConstruct
    public void initialize() {
        pdfUtil= this;
        pdfUtil.pdfFontFileConfig = this.pdfFontFileConfig;
    }

    public static void exportPdf(HttpServletRequest request, HttpServletResponse response, ArrayList<List<Map>> arrayList) throws Exception {
//        HeaderFooter footer = new HeaderFooter(new Phrase("第：",FontChinese), new Phrase("页",FontChinese));
//        footer.setBorder(Rectangle.NO_BORDER);
//        document.setFooter(footer);
        Document document= new Document(PageSize.A4);
        document.newPage();
        PdfWriter.getInstance(document, response.getOutputStream());
        //        writer.setPageEvent(new PDFUtil());
//        String path="Pingf.ttf";
//        path = new ClassPathResource(path).getPath();

        ClassPathResource resource = new ClassPathResource("msyh.ttf");
        String pathTest = resource.getPath(); //获取resources根目录

        String path=pdfUtil.pdfFontFileConfig.returnFontFilePath();

        //String path = PDFUtil.class.getClassLoader().getResource("Pingf.ttf").getPath();
        //String path = PDFUtil.class.getClassLoader().getResource("msyh.ttf").getPath();
        String urlPath = java.net.URLDecoder.decode(path, "utf-8");
        // 设置字体
        // BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        BaseFont bfChinese = BaseFont.createFont(urlPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        com.itextpdf.text.Font FontChinese10 = new com.itextpdf.text.Font(bfChinese, 10);
        com.itextpdf.text.Font FontChinese18 = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font FontChinese11 = new com.itextpdf.text.Font(bfChinese, 11);
        com.itextpdf.text.Font FontChinese12 = new com.itextpdf.text.Font(bfChinese, 12);
        com.itextpdf.text.Font FontChinese11Bold = new com.itextpdf.text.Font(bfChinese, 11,
                com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font red = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);
        red.setColor(BaseColor.RED);
        com.itextpdf.text.Font FontChinese11Normal = new com.itextpdf.text.Font(bfChinese, 11,
                com.itextpdf.text.Font.NORMAL);
        document.open();

        for (List<Map> list:arrayList) {

//            Image jpeg = Image.getInstance(
//                    "https://aiplatform.qiniu.uuair.com.cn//PEKMallCms/images/421542270663_.pic_thumb.jpg_88be7df0-7c69-4edc-8f63-3ee1c0a25fe6");
            String pathLog=pdfUtil.pdfFontFileConfig.returnActlogo();
            Image jpeg =Image.getInstance(pathLog);
            jpeg.setAlignment(Image.MIDDLE);
            jpeg.scaleAbsolute(80, 30);
            document.add(jpeg);

            // 创建一个直线的实例
            LineSeparator line = new LineSeparator();
            // 设置位置
            line.setAlignment(Element.ALIGN_CENTER);
            // 长度(占据PDF百分比)
            line.setPercentage(90);// 线的长度(百分比)
            // 宽度
            line.setOffset(0.2f);
            // 颜色
            line.setLineColor(new BaseColor(0, 0, 0));
            Paragraph topline = new Paragraph();
            topline.add(line);
            // 直线距离上面大小
            // topline.setSpacingBefore(20);
            document.add(topline);

            Paragraph blankRow1 = new Paragraph(30f, " ", FontChinese18);
            document.add(blankRow1);

            PdfPTable table = new PdfPTable(4);
            // 设置表格无边框
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            // 设置居中对齐
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 设置单元格高度
            table.getDefaultCell().setMinimumHeight(25);
            table.setTotalWidth(450);
            table.setLockedWidth(true);

            Map data = list.get(0);

            PdfPTable table1 = new PdfPTable(4);
            int width1[] = {10, 25, 10, 25};
            table1.setWidths(width1);
            table1.setTotalWidth(480);

//        table1.getDefaultCell().setMinimumHeight(70);
            table1.setLockedWidth(true);
            PdfPCell cellOderNoLabel = new PdfPCell(new Paragraph("订单号", FontChinese11));
            PdfPCell cellOrderNoValue = new PdfPCell(new Paragraph(data.get("orderNo").toString(), FontChinese11));
            cellOderNoLabel.setMinimumHeight(30);


            PdfPCell cellOderTimeLabel = new PdfPCell(new Paragraph("下单时间", FontChinese11));
            PdfPCell cellOrderTimeValue = new PdfPCell(new Paragraph(data.get("createTime").toString(), FontChinese11));
            cellOderTimeLabel.setMinimumHeight(30);

            PdfPCell cellReceivePersonLabel = new PdfPCell(new Paragraph("收货人", FontChinese11));
            PdfPCell cellReceivePersonValue = new PdfPCell(new Paragraph(CharUtil.objectConverToString(data.get("receiveName")), FontChinese11));
            cellReceivePersonLabel.setMinimumHeight(30);
            cellReceivePersonValue.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell cellOrderPersonLabel = new PdfPCell(new Paragraph("订购人", FontChinese11));
            PdfPCell cellOrderPersonValue = new PdfPCell(new Paragraph(CharUtil.objectConverToString(data.get("nickname")), FontChinese11));
            cellOrderPersonLabel.setMinimumHeight(30);

            PdfPCell cellOrderPersonPhoneLabel = new PdfPCell(new Paragraph("订购人电话", FontChinese11));
            PdfPCell cellOrderPersonPhoneValue = new PdfPCell(new Paragraph(CharUtil.objectConverToString(data.get("userPhone")), FontChinese11));
            cellOrderPersonPhoneLabel.setMinimumHeight(30);
            cellOrderPersonPhoneValue.setColspan(2);


            PdfPCell cellOderReceivePersonPhoneLabel = new PdfPCell(new Paragraph("收货人电话", FontChinese11));
            PdfPCell cellOrderReceivePersonPhoneValue = new PdfPCell(new Paragraph(CharUtil.objectConverToString(data.get("receivePhone")), FontChinese11));
            cellOderReceivePersonPhoneLabel.setMinimumHeight(30);
            cellOrderReceivePersonPhoneValue.setHorizontalAlignment(Element.ALIGN_LEFT);


            PdfPCell cellOrderReceiveAddressLabel = new PdfPCell(new Paragraph("收货地址", FontChinese11));
            PdfPCell cellOrderReceiveAddressValue = new PdfPCell(new Paragraph(CharUtil.objectConverToString(data.get("receiveAddress")), FontChinese11));

            cellOrderReceiveAddressLabel.setMinimumHeight(30);
            cellOrderReceiveAddressLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellOrderReceiveAddressValue.setRowspan(2);


            cellOderNoLabel.setBorder(0);
            cellOrderNoValue.setBorder(0);

            cellOderTimeLabel.setBorder(0);
            cellOrderTimeValue.setBorder(0);

            cellReceivePersonLabel.setBorder(0);
            cellReceivePersonValue.setBorder(0);

            cellOrderPersonLabel.setBorder(0);
            cellOrderPersonValue.setBorder(0);

            cellOrderPersonPhoneLabel.setBorder(0);
            cellOrderPersonPhoneValue.setBorder(0);

            cellOderReceivePersonPhoneLabel.setBorder(0);
            cellOrderReceivePersonPhoneValue.setBorder(0);

            cellOrderReceiveAddressLabel.setBorder(0);
            cellOrderReceiveAddressValue.setBorder(0);
            //订单编号
            table1.addCell(cellOderNoLabel);
            table1.addCell(cellOrderNoValue);
            //收货人
            table1.addCell(cellReceivePersonLabel);
            table1.addCell(cellReceivePersonValue);
            //下单时间
            table1.addCell(cellOderTimeLabel);
            table1.addCell(cellOrderTimeValue);
            //收货人电话
            table1.addCell(cellOderReceivePersonPhoneLabel);
            table1.addCell(cellOrderReceivePersonPhoneValue);
            //订购人名称
            table1.addCell(cellOrderPersonLabel);
            table1.addCell(cellOrderPersonValue);

            //收货人地址
            table1.addCell(cellOrderReceiveAddressLabel);
            table1.addCell(cellOrderReceiveAddressValue);
            //订购人电话
            table1.addCell(cellOrderPersonPhoneLabel);
            table1.addCell(cellOrderPersonPhoneValue);

            document.add(table1);
            Paragraph blankRow2 = new Paragraph(20f, " ", FontChinese18);
            document.add(blankRow2);

            // table3
            PdfPTable table3 = new PdfPTable(4);
            int width3[] = {15, 40, 20, 15};
            table3.setWidths(width3);
            table3.setTotalWidth(480);
            table3.setLockedWidth(true);
            // table3.getDefaultCell().setMinimumHeight(10);
            // table3.addCell(table3.getDefaultCell());
            table3.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cell31 = new PdfPCell(new Paragraph("商品", FontChinese11));
            PdfPCell cell32 = new PdfPCell(new Paragraph("单价", FontChinese11));
            PdfPCell cell33 = new PdfPCell(new Paragraph("数量", FontChinese11));
            cell31.setBorder(0);
            cell32.setBorder(0);
            cell33.setBorder(0);
            cell32.setColspan(2);
            cell31.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell32.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell33.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell31);
            table3.addCell(cell32);
            table3.addCell(cell33);
            document.add(table3);
            // 创建一个直线的实例
            LineSeparator line2 = new LineSeparator();
            // 设置位置
            line2.setAlignment(Element.ALIGN_CENTER);
            // 长度(占据PDF百分比)
            line2.setPercentage(90);// 线的长度(百分比)
            // 宽度
            line2.setOffset(0.2f);
            // 颜色
            line2.setLineColor(new BaseColor(0, 0, 0));
            Paragraph topline2 = new Paragraph();
            topline2.add(line2);
            // 直线距离上面大小
            topline2.setSpacingBefore(-13);
            topline2.setSpacingAfter(20);

            document.add(topline2);

            PdfPTable table4 = new PdfPTable(4);
            int width4[] = {15, 40, 20, 15};
            table4.setWidths(width4);
            table4.setTotalWidth(480);
            table4.setLockedWidth(true);
            for (int i = 0; i < list.size(); i++) {
                Map goods = list.get(i);
                PdfPCell cell37 = new PdfPCell();
                PdfPCell cell34 = new PdfPCell(new Paragraph(CharUtil.objectConverToString(goods.get("goodName")) + " 规格:" +
                        CharUtil.objectConverToString(goods.get("specifications")), FontChinese11));
                PdfPCell cell35 = new PdfPCell(new Paragraph("¥" + goods.get("price").toString(), FontChinese11));
                PdfPCell cell36 = new PdfPCell(new Paragraph("×" + goods.get("number").toString(), FontChinese11));

                //TODO 临时处理图片地址
                String imageUrl = goods.get("goodImg").toString();

//                //正式环境
//                if (imageUrl.startsWith("http://airportshoppingonline.com/upload")) {
//                    imageUrl = imageUrl.replace("http://airportshoppingonline.com/upload", "http://10.30.49.54:8086/upload");
//                }
//                if (imageUrl.startsWith("https://airportshoppingonline.com/upload")) {
//                    imageUrl = imageUrl.replace("https://airportshoppingonline.com/upload", "http://10.30.49.54:8086/upload");
//                }

                //办公环境，无需额外处理

                Image jpeg2 = Image.getInstance(imageUrl);
                jpeg2.setAlignment(Image.LEFT);
                jpeg2.scaleAbsolute(40, 20);
                cell37.addElement(jpeg2);
                cell35.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell36.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell34.setBorderColor(GAINSBORO);
                cell34.setBorder(2);
                cell35.setBorderColor(GAINSBORO);
                cell35.setBorder(2);
                cell36.setBorderColor(GAINSBORO);
                cell36.setBorder(2);
                cell37.setBorder(0);
                table4.addCell(cell37);
                table4.addCell(cell34);
                table4.addCell(cell35);
                table4.addCell(cell36);
            }
            PdfPCell cell38 = new PdfPCell(new Paragraph("合计", FontChinese11));
            PdfPCell cell39 = new PdfPCell(new Paragraph("¥" + data.get("sumTotal").toString(), FontChinese11));
            cell38.setColspan(3);
            cell38.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell39.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell38.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell39.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell38.setBorder(0);
            cell39.setBorder(0);

            cell38.setMinimumHeight(25);

            table4.addCell(cell38);
            table4.addCell(cell39);
            if (null != data.get("manjian")) {
                if (!data.get("manjian").toString().equals(String.valueOf(0)) && StringUtils.hasText(data.get("manjian").toString())) {
                    PdfPCell cell40 = new PdfPCell(new Paragraph("优惠券减免", FontChinese11));
                    PdfPCell cell41 = new PdfPCell(new Paragraph("-¥" + data.get("manjian"), FontChinese11));
                    cell40.setColspan(3);
                    cell40.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell41.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell40.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell41.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell40.setBorder(0);
                    cell41.setBorder(0);
                    cell40.setMinimumHeight(25);
                    table4.addCell(cell40);
                    table4.addCell(cell41);
                }
            }


//        PdfPCell cell42 = new PdfPCell(new Paragraph("积分抵现", FontChinese12));
//        PdfPCell cell43 = new PdfPCell(new Paragraph("-¥12", red));
//        cell42.setColspan(3);
//        cell42.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        cell43.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cell44 = new PdfPCell(new Paragraph("运费", FontChinese11));
            PdfPCell cell45 = new PdfPCell(new Paragraph("¥" + data.get("freightPrice"), FontChinese11));
            cell44.setColspan(3);
            cell44.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell45.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell44.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell45.setHorizontalAlignment(Element.ALIGN_RIGHT);

            //积分抵现
            PdfPCell cellJifenLabel = new PdfPCell(new Paragraph("积分抵现", FontChinese11));
            PdfPCell cellJifenValue = new PdfPCell(new Paragraph("¥" + data.get("integralPrice"), FontChinese11));
            cellJifenLabel.setColspan(3);
            cellJifenLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellJifenValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellJifenLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellJifenValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cell46 = new PdfPCell(new Paragraph("实际支付", FontChinese11));
            PdfPCell cell47 = new PdfPCell(new Paragraph("¥" + data.get("payPrice"), FontChinese11));
            cell46.setColspan(3);
            cell46.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell47.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell46.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell47.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cell62 = new PdfPCell(new Paragraph("", FontChinese11));
            PdfPCell cell60 = new PdfPCell(new Paragraph("支付方式", FontChinese11));
            PdfPCell cell61 = new PdfPCell(new Paragraph(data.get("payMethod") + "", FontChinese11));
            cell60.setColspan(2);
            cell60.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell61.setVerticalAlignment(Element.ALIGN_MIDDLE);

            cell60.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell61.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell cell48 = new PdfPCell(new Paragraph("本次获得订单积分" + Math.round(Double.parseDouble(data.get("payPrice").toString())) + "分", FontChinese11));
            cell48.setColspan(4);

            cell48.setUseAscender(true);
            // 垂直居中
            cell48.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell48.setHorizontalAlignment(Element.ALIGN_RIGHT);


//        cell42.setBorder(0);
//        cell43.setBorder(0);
            cell44.setBorder(0);
            cell45.setBorder(0);
            cellJifenLabel.setBorder(0);
            cellJifenValue.setBorder(0);
            cell46.setBorder(0);
            cell47.setBorder(0);
            cell48.setBorder(0);
            cell60.setBorderColor(GAINSBORO);
            cell61.setBorderColor(GAINSBORO);
            cell60.setBorder(2);
            cell61.setBorder(2);
            cell62.setBorder(0);


//       cell42.setMinimumHeight(15);
            cell44.setMinimumHeight(25);
            cell46.setMinimumHeight(25);
            cell60.setMinimumHeight(25);
            cell48.setMinimumHeight(30);


//        table4.addCell(cell42);
//        table4.addCell(cell43);
            table4.addCell(cell44);
            table4.addCell(cell45);
            table4.addCell(cellJifenLabel);
            table4.addCell(cellJifenValue);
            table4.addCell(cell46);
            table4.addCell(cell47);
            table4.addCell(cell62);
            table4.addCell(cell60);
            table4.addCell(cell61);
            table4.addCell(cell48);
//        //发票信息
//        if (null != data[17]) {
//            JSONObject jsonObject = JSONObject.fromObject(data[17].toString());
//
//            PdfPCell cell49 = new PdfPCell(new Paragraph("发票信息", FontChinese11));
//            cell49.setColspan(4);
//            cell49.setBorder(2);
//            table4.addCell(cell49);
//
//            PdfPCell cell50 = new PdfPCell(new Paragraph("发票类型", FontChinese11));
//            PdfPCell cell51 = new PdfPCell(new Paragraph(jsonObject.getString("titleType"), FontChinese11));
//            cell51.setColspan(3);
//            cell51.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell51.setMinimumHeight(20);
//            cell50.setBorder(0);
//            cell51.setBorder(0);
//            table4.addCell(cell50);
//            table4.addCell(cell51);
//
//            PdfPCell cell52 = new PdfPCell(new Paragraph("发票金额", FontChinese11));
//            PdfPCell cell53 = new PdfPCell(new Paragraph("¥" + data.get("payPrice"), FontChinese11));
//            cell53.setColspan(3);
//            cell53.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell53.setMinimumHeight(20);
//            cell52.setBorder(0);
//            cell53.setBorder(0);
//            table4.addCell(cell52);
//            table4.addCell(cell53);
//
//            if (null != jsonObject.get("companyName")) {
//                PdfPCell cell54 = new PdfPCell(new Paragraph("发票抬头", FontChinese11));
//                PdfPCell cell55 = new PdfPCell(new Paragraph(jsonObject.getString("companyName"), FontChinese11));
//                cell55.setColspan(3);
//                cell55.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell55.setMinimumHeight(20);
//                cell54.setBorder(0);
//                cell55.setBorder(0);
//                table4.addCell(cell54);
//                table4.addCell(cell55);
//            }
//            if (null != jsonObject.get("companyNum")) {
//                PdfPCell cell56 = new PdfPCell(new Paragraph("税务登记号", FontChinese11));
//                PdfPCell cell57 = new PdfPCell(new Paragraph(jsonObject.getString("companyNum"), FontChinese11));
//                cell57.setColspan(3);
//                cell57.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell57.setMinimumHeight(20);
//                cell56.setBorder(0);
//                cell57.setBorder(0);
//                table4.addCell(cell56);
//                table4.addCell(cell57);
//            }
//
//        }
            document.add(table4);

            Paragraph blankRow3 = new Paragraph(30f, " ", FontChinese18);
            document.add(blankRow3);
            PdfPTable table5 = new PdfPTable(4);
            int width5[] = {15, 20, 20, 15};
            table5.setWidths(width5);
            table5.setTotalWidth(480);
            table5.setTotalWidth(480);
            table5.setLockedWidth(true);

            PdfPCell cell58 = new PdfPCell(new Paragraph());
            String pathQRCode=pdfUtil.pdfFontFileConfig.returnActQRCode();
//            Image jpeg3 = Image.getInstance(
//                    "https://aiplatform.qiniu.uuair.com.cn//PEKMallCms/images/401542270292_.pic_thumb.jpg_324a2452-21b0-4a7c-8b9f-202d884dd27b");
            Image jpeg3 = Image.getInstance(pathQRCode);
            jpeg3.setAlignment(Image.LEFT);
            jpeg3.scaleAbsolute(60, 30);
            cell58.addElement(jpeg3);
            PdfPCell cell59 = new PdfPCell(new Paragraph(
                    "感谢选择首都机场微商城,祝您生活愉快！\n感谢您对所购产品及我们的服务感到满意,等待您再次莅临选购！\n\n\n 更多优惠,请时刻关注微商城！", FontChinese10));
            cell59.setColspan(3);
            cell58.setBorder(0);
            cell59.setBorder(0);
            table5.addCell(cell58);
            table5.addCell(cell59);
            document.add(table5);
            //开始新的一页
            document.newPage();
        }

        document.close();

    }


//    public void onEndPage(PdfWriter writer, Document document) {
//        //在每页结束的时候把“第x页”信息写道模版指定位置
//        PdfContentByte cb = writer.getDirectContent();
//        cb.saveState();
//        String text = "第" + writer.getPageNumber() + "页,共";
//        cb.beginText();
//        cb.setFontAndSize(bf, 8);
//        cb.setTextMatrix(460, 786);//定位“第x页,共” 在具体的页面调试时候需要更改这xy的坐标
//        cb.showText(text);
//        cb.endText();
//        cb.addTemplate(tpl, 492, 786);//定位“y页” 在具体的页面调试时候需要更改这xy的坐标
//
//        cb.saveState();
//        cb.stroke();
//        cb.restoreState();
//        cb.closePath();//sanityCheck();
//    }
//
//    public void onCloseDocument(PdfWriter writer, Document document) {
//        //关闭document的时候获取总页数，并把总页数按模版写道之前预留的位置
//        tpl.beginText();
//        tpl.setFontAndSize(bf, 8);
//        tpl.showText(Integer.toString(writer.getPageNumber() - 1)+"页");
//        tpl.endText();
//        tpl.closePath();//sanityCheck();
//    }

}
