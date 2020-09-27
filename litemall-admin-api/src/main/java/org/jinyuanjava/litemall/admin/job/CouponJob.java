package org.jinyuanjava.litemall.admin.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.admin.service.CommonService;
import org.jinyuanjava.litemall.db.domain.LitemallCoupon;
import org.jinyuanjava.litemall.db.domain.LitemallCouponUser;
import org.jinyuanjava.litemall.db.service.LitemallCouponService;
import org.jinyuanjava.litemall.db.service.LitemallCouponUserService;
import org.jinyuanjava.litemall.db.util.CouponConstant;
import org.jinyuanjava.litemall.db.util.CouponUserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;


/**
 * 检测优惠券过期情况
 */
@Component
public class CouponJob {
    private final Log logger = LogFactory.getLog(CouponJob.class);

    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;

    @Autowired
    private CommonService commonService;

    /**
     * 每隔1分钟检查
     * TODO
     * 注意，因为是相隔1分钟检查，因此导致优惠券真正超时时间可能比设定时间延迟1分钟
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void checkCouponExpired() {
        String booStartJob= commonService.getProjectYml("startJob");

        if(!booStartJob.equals("true"))
        {
            return;
        }

        logger.info("系统开启任务检查优惠券是否已经过期");


        List<LitemallCoupon> couponList = couponService.queryExpired();
        for(LitemallCoupon coupon : couponList){
            coupon.setStatus(CouponConstant.STATUS_EXPIRED);
            couponService.updateById(coupon);
        }

        List<LitemallCouponUser> couponUserList = couponUserService.queryExpired();
        for(LitemallCouponUser couponUser : couponUserList){
            couponUser.setStatus(CouponUserConstant.STATUS_EXPIRED);
            couponUserService.update(couponUser);
        }
    }

}
