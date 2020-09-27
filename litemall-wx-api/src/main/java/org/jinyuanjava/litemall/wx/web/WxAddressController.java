package org.jinyuanjava.litemall.wx.web;

import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinyuanjava.litemall.core.util.RegexUtil;
import org.jinyuanjava.litemall.core.util.ResponseUtil;
import org.jinyuanjava.litemall.db.domain.LitemallAddress;
import org.jinyuanjava.litemall.db.domain.LitemallRegion;
import org.jinyuanjava.litemall.db.domain.LitemallUser;
import org.jinyuanjava.litemall.db.service.LitemallAddressService;
import org.jinyuanjava.litemall.db.service.LitemallRegionService;
import org.jinyuanjava.litemall.db.service.LitemallUserService;
import org.jinyuanjava.litemall.wx.annotation.LoginUser;
import org.jinyuanjava.litemall.wx.service.GetRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收货地址服务
 */
@RestController
@RequestMapping("/wx/address")
@Validated
@Api(description = "微信前端/地址管理:/wx/address")
public class WxAddressController extends GetRegionService {
	private final Log logger = LogFactory.getLog(WxAddressController.class);

	@Autowired
	private LitemallAddressService addressService;

	@Autowired
	private LitemallRegionService regionService;

	@Autowired
	private LitemallUserService userService;


	/**
	 * 用户收货地址列表
	 *
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	@GetMapping("list")
	public Object list( Integer userId) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		List<LitemallAddress> addressList = addressService.queryByUid(userId);

		List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
		List<LitemallRegion> regionList = getLitemallRegions();
		for (LitemallAddress address : addressList) {
			Map<String, Object> addressVo = new HashMap<>();
			addressVo.put("id", address.getId());
			addressVo.put("name", address.getName());
			addressVo.put("tel", address.getTel());
			addressVo.put("default", address.getIsDefault());

			/*String addressInfo = address.getAddressDetail();
			if(addressInfo.indexOf("===")!=-1){
				addressVo.put("addressDetail", addressInfo.substring(0, addressInfo.indexOf("===")));
				addressVo.put("area", addressInfo.substring(addressInfo.indexOf("===")+3,addressInfo.length() ));
			}else{
				addressVo.put("addressDetail", addressInfo);
			}*/

			addressVo.put("addressDetail", address.getAddressDetail());

			addressVo.put("area", address.getArea());

			addressVo.put("province", address.getProvince());
			addressVo.put("city", address.getCity());
			addressVo.put("county", address.getCounty());

			if(address.getProvince()!=null){
				LitemallRegion region = addressService.getById(Integer.valueOf(address.getProvince()));
				addressVo.put("provinceName",region.getName() );
			}

			if(address.getCity()!=null){
				LitemallRegion region = addressService.getById(Integer.valueOf(address.getCity()));
				addressVo.put("cityName",region.getName() );
			}

			if(address.getCounty()!=null){
				LitemallRegion region = addressService.getById(Integer.valueOf(address.getCounty()));
				addressVo.put("countyName",region.getName() );
			}


			addressVoList.add(addressVo);
		}
		return ResponseUtil.ok(addressVoList);
	}


	@GetMapping("getRegionList")
	public Object getRegionList( Integer pid,Integer type) {


		List<LitemallRegion> regionList = addressService.queryRegionByPidAndType(pid,type);
		return ResponseUtil.ok(regionList);
	}

	/**
	 * 收货地址详情
	 *
	 * @param userId 用户ID
	 * @param id     收货地址ID
	 * @return 收货地址详情
	 */
	@GetMapping("detail")
	public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}

		LitemallAddress address = addressService.query(userId, id);
		if (address == null) {
			return ResponseUtil.badArgumentValue();
		}
		return ResponseUtil.ok(address);
	}

	private Object validate(LitemallAddress address) {
		String name = address.getName();
		if (StringUtils.isEmpty(name)) {
			return ResponseUtil.badArgument();
		}

		// 测试收货手机号码是否正确
		String mobile = address.getTel();
		if (StringUtils.isEmpty(mobile)) {
			return ResponseUtil.badArgument();
		}
		if (!RegexUtil.isMobileExact(mobile)) {
			return ResponseUtil.badArgument();
		}

		String province = address.getProvince();
		if (StringUtils.isEmpty(province)) {
			return ResponseUtil.badArgument();
		}

		String city = address.getCity();
		if (StringUtils.isEmpty(city)) {
			return ResponseUtil.badArgument();
		}

		String county = address.getCounty();
		if (StringUtils.isEmpty(county)) {
			return ResponseUtil.badArgument();
		}


		/*String areaCode = address.getAreaCode();
		if (StringUtils.isEmpty(areaCode)) {
			return ResponseUtil.badArgument();
		}*/

		String detailedAddress = address.getAddressDetail();
		if (StringUtils.isEmpty(detailedAddress)) {
			return ResponseUtil.badArgument();
		}

		/*Boolean isDefault = address.getIsDefault();
		if (isDefault == null) {
			return ResponseUtil.badArgument();
		}*/
		return null;
	}

	/**
	 * 添加或更新收货地址
	 *
	 * @param userId  用户ID
	 * @param address 用户收货地址
	 * @return 添加或更新操作结果
	 */
	@PostMapping("save")
	public Object save( Integer userId, @RequestBody LitemallAddress address,String isDefault) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		Object error = validate(address);
		if (error != null) {
			return error;
		}
		//是否默认地址
		if(isDefault!=null&&!"".equalsIgnoreCase(isDefault)){
			address.setIsDefault(Boolean.parseBoolean(isDefault));
		}
//		//门牌号，没字段了，存地址详细里
//		if(address.getArea()!=null){
//			address.setAddressDetail(address.getAddressDetail()+"==="+address.getArea());
//		}

		if (address.getIsDefault()) {
			// 重置其他收获地址的默认选项
			addressService.resetDefault(userId);
		}
		address.setUserId(userId);
		LitemallUser userInfo =userService.findById(userId);
		address.setWxNickname(userInfo.getNickname());
		address.setWeixinOpenid(userInfo.getWeixinOpenid());
		address.setCrmId(userInfo.getCrmId());
		if(!StringUtils.isEmpty(address.getProvince())){
			LitemallRegion region= regionService.findById(Integer.parseInt(address.getProvince()));
			address.setProvinceName(region.getName());
		}
		if(!StringUtils.isEmpty(address.getCity())){
			LitemallRegion region= regionService.findById(Integer.parseInt(address.getCity()));
			address.setCityName(region.getName());
		}
		if(!StringUtils.isEmpty(address.getCounty())){
			LitemallRegion region= regionService.findById(Integer.parseInt(address.getCounty()));
			address.setCountryName(region.getName());
		}
		if (address.getId() == null || address.getId().equals(0)) {
			address.setId(null);


			addressService.add(address);
		} else {
			address.setUserId(userId);
			if (addressService.update(address) == 0) {
				return ResponseUtil.updatedDataFailed();
			}
		}
		return ResponseUtil.ok(address.getId());
	}

	/**
	 * 删除收货地址
	 *
	 * @param userId  用户ID
	 * @param id 用户收货地址，{ id: xxx }
	 * @return 删除操作结果
	 */
	@PostMapping("delete")
	public Object delete( Integer userId, Integer id) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}

		if (id == null) {
			return ResponseUtil.badArgument();
		}

		addressService.delete(id);
		return ResponseUtil.ok();
	}

	@GetMapping("getAddressData")
	public Object getAddressData(@LoginUser Integer userId,Integer addressId) {
		if(StringUtils.isEmpty(userId)){
			return ResponseUtil.unlogin();
		}
		// 收货地址
		LitemallAddress checkedAddress = null;
		if (addressId == null || addressId.equals(0)) {
			checkedAddress = addressService.findDefault(userId);
			// 如果仍然没有地址，则是没有收获地址
			// 返回一个空的地址id=0，这样前端则会提醒添加地址
			if (checkedAddress == null) {
				checkedAddress = new LitemallAddress();
				checkedAddress.setId(0);
				addressId = 0;
			} else {
				addressId = checkedAddress.getId();
			}

		} else {
			checkedAddress = addressService.query(userId, addressId);
			// 如果null, 则报错
			if (checkedAddress == null) {
				return ResponseUtil.badArgumentValue();
			}
		}
		return ResponseUtil.ok(checkedAddress);
	}


}
