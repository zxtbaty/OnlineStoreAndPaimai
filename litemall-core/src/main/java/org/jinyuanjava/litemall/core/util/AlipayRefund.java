package org.jinyuanjava.litemall.core.util;
/**
 * @Author: HONGLINCHEN
 * @Description:支付宝退款 类
 * @Date: 2017-9-12 15:48
 */
public class AlipayRefund {
    private String out_trade_no;//必须 商户订单号
    private String trade_no;//必须 支付宝交易号
    private String refund_amount;//必须 退款金额
    private String refund_reason;//可选 代表 退款的原因说明
    private String out_request_no;//可选 标识一次退款请求，同一笔交易多次退款需要保证唯一（就是out_request_no在2次退款一笔交易时，要不一样），如需部分退款，则此参数必传
    private String operator_id;//可选 代表 商户的操作员编号
    private String store_id;//可选 代表 商户的门店编号
    private String terminal_id;//可选 代表 商户的终端编号
    public AlipayRefund() {
    }
    public String getOut_trade_no() {
        return out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public String getTrade_no() {
        return trade_no;
    }
    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }
    public String getRefund_amount() {
        return refund_amount;
    }
    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }
    public String getRefund_reason() {
        return refund_reason;
    }
    public void setRefund_reason(String refund_reason) {
        this.refund_reason = refund_reason;
    }
    public String getOut_request_no() {
        return out_request_no;
    }
    public void setOut_request_no(String out_request_no) {
        this.out_request_no = out_request_no;
    }
    public String getOperator_id() {
        return operator_id;
    }
    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }
    public String getStore_id() {
        return store_id;
    }
    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
    public String getTerminal_id() {
        return terminal_id;
    }
    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }
}

