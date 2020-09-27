const GetDate = {
  withData: (num) => {
		let param = parseInt(num);
    return param < 10 ? '0' + param : '' + param;
  },
  getTimes(str){
    return new Date(str.replace(/-/g,'/')).getTime();
  },
	getCurrentTimes(){
		const date = new Date();
		const year = date.getFullYear();
		const month = date.getMonth() + 1;
		const day = date.getDate();
		const hour = date.getHours();
		const minute = date.getMinutes();
		const second = date.getSeconds();
		return {
			detail: {
				year: year,
				month: month,
				day: day,
				hour: hour,
				minute: minute,
				second: second
			}
		}
	},
  format(arr){
    let curarr = [];
    let curarr0 = [];
    let str = '';
    arr.forEach((cur,index) => {
			let o = GetDate.withData(cur);
      if(index > 2){
        curarr.push(o);
      }else{
        curarr0.push(o);
      }
    })
    if(arr.length < 4){
      str = curarr0.join('-');
    }else{
      str = curarr0.join('-') + ' ' + curarr.join(':');
    }
    return str;
  },
	getCurrentStringValue(str){
		let newstr = str.split(' ');
		if(newstr && newstr[1]){
			let arr = [...newstr[0].split('-'),...newstr[1].split(':')];
			return arr;
		}
		return newstr[0].split('-');
	},
	getCompare(curp,startp,endp,timesp){
		let cur = GetDate.getTimes(curp);
		let start = GetDate.getTimes(startp);
		let end = GetDate.getTimes(endp);
		if(cur < start){
			return GetDate.getTimeIndex(timesp,GetDate.getCurrentStringValue(startp));
		}else if(cur > end){
			return GetDate.getTimeIndex(timesp,GetDate.getCurrentStringValue(endp));
		}else{
			return GetDate.getTimeIndex(timesp,GetDate.getCurrentStringValue(curp));
		}
	},
	getChooseArr(times,indexs){
		let arr = [];
		times.forEach((cur,index) => arr.push(cur[indexs[index]]));
		return arr;
	},
	getNewArray(arr){
		let newarr = [];
		arr.forEach(cur => newarr.push(cur));
		return newarr;
	},
  getLoopArray: (start, end) => {
    var start = start || 0;
    var end = end || 1;
    var array = [];
    for (var i = start; i <= end; i++) {
      array.push(GetDate.withData(i));
    }
    return array;
  },
  getLoopArrayForMod: (start, end,mod) => {
    var start = start || 0;
    var end = end || 1;
    var array = [];
    for (var i = start; i <= end; i++) {
	  if(i%mod==0){
		  array.push(GetDate.withData(i));
	  }
      
    }
    return array;
  },
   getMonth: (year,opts) => {
	let array = null;
	let startyear = opts.start.substring(0,4);
	let endyear = opts.end.substring(0,4);
	let startmonth = opts.start.substring(5,7);
	let endmonth = opts.end.substring(5,7);
	switch (year) {
	  case startyear:
		if(startyear==endyear){
			array = GetDate.getLoopArray(startmonth, endmonth)
		}else{
			array = GetDate.getLoopArray(startmonth, 12)
		}
	    break;
	  case endyear:
	    array = GetDate.getLoopArray(1, endmonth)
	    break;
	  default:
	    array = GetDate.getLoopArray(1, 12)
	}
	//console.log(array);
    return array;
  },
  getMonthDay: (year, month,opts) => {
	var flag = year % 400 == 0 || (year % 4 == 0 && year % 100 != 0), array = null;
	let startyear = opts.start.substring(0,4);
	let endyear = opts.end.substring(0,4);
	let startmonth = opts.start.substring(5,7);
	let endmonth = opts.end.substring(5,7);
	let startday = opts.start.substring(8,10);
	let endday = opts.end.substring(8,10);
	let yearmonth = year +"-"+month;
	let startyearmonth = opts.start.substring(0,7);
	let endyearmonth = opts.end.substring(0,7);
	
	if(startyearmonth==endyearmonth){
		array = GetDate.getLoopArray(startday, endday)
	}else if(yearmonth==startyearmonth){
		switch (month) {
		    case '01':
		    case '03':
		    case '05':
		    case '07':
		    case '08':
		    case '10':
		    case '12':
		      array = GetDate.getLoopArray(startday, 31)
		      break;
		    case '04':
		    case '06':
		    case '09':
		    case '11':
		      array = GetDate.getLoopArray(startday, 30)
		      break;
		    case '02':
		      array = flag ? GetDate.getLoopArray(startday, 29) : GetDate.getLoopArray(startday, 28)
		      break;
		    default:
		      array = '月份格式不正确，请重新输入！'
		  }
	}else if(yearmonth==endyearmonth){
				switch (month) {
		    case '01':
		    case '03':
		    case '05':
		    case '07':
		    case '08':
		    case '10':
		    case '12':
				
		      array = GetDate.getLoopArray(1, endday)
		      break;
		    case '04':
		    case '06':
		    case '09':
		    case '11':
		      array = GetDate.getLoopArray(1, endday)
		      break;
		    case '02':
		      array = flag ? GetDate.getLoopArray(1, endday) : GetDate.getLoopArray(1, endday)
		      break;
		    default:
		      array = '月份格式不正确，请重新输入！'
		  }
	}else{
		switch (month) {
		    case '01':
		    case '03':
		    case '05':
		    case '07':
		    case '08':
		    case '10':
		    case '12':
		      array = GetDate.getLoopArray(1, 31)
		      break;
		    case '04':
		    case '06':
		    case '09':
		    case '11':
		      array = GetDate.getLoopArray(1, 30)
		      break;
		    case '02':
		      array = flag ? GetDate.getLoopArray(1, 29) : GetDate.getLoopArray(1, 28)
		      break;
		    default:
		      array = '月份格式不正确，请重新输入！'
		  }
	}
    return array;
  },
  getHours(day,opts){
	  //console.log(opts)
	  let array = null;
	  let startday = opts.start.substring(8,10);
	  let endday = opts.end.substring(8,10);
	  if(startday==endday||day==startday){
		array= GetDate.getLoopArray(opts.begintime.substring(0, 2), opts.endtime.substring(0, 2));
	  } else
	  {
		array= GetDate.getLoopArray(opts.beginHourMinute.substring(0, 2), opts.endtime.substring(0, 2));   
	  }
	  return array;
  },
  getMinutes(opts){
  	  console.log(opts)
  	  let array = null;
	  let starthours = opts.begintime.substring(0, 2);
	  let endhours = opts.endtime.substring(0, 2);
	  let startminuste =  opts.begintime.substring(3,5);
	  let endminuste =  opts.endtime.substring(3,5);
	  //后台用户设置的起始小时和分钟数
	  let userSetStartHours=opts.beginHourMinute.substring(0, 2);
	  let userSetStartMinute=opts.beginHourMinute.substring(3, opts.beginHourMinute.length);
	  
	  let curhours = opts.curhours;
	  if(starthours==endhours){
		  if(userSetStartHours==starthours&&userSetStartMinute>=starthours){
			  array= GetDate.getLoopArrayForMod(userSetStartMinute, endminuste,5);
		  }else{
			  array= GetDate.getLoopArrayForMod(startminuste, endminuste,5);
		  }
	  }else if(curhours==starthours){
		  array= GetDate.getLoopArrayForMod(startminuste, 59,5);
	  }else if(curhours==endhours){
		  array= GetDate.getLoopArrayForMod(0, endminuste,5);
	  }else{
		  if(curhours==userSetStartHours){
			  array= GetDate.getLoopArrayForMod(userSetStartMinute, 59,5);
		  } else{
			 array= GetDate.getLoopArrayForMod(0, 59,5);
		  }
	  }
  	  return array;
  },
  getDateTimes: (opts) => {
    // var years = GetDate.getLoopArray(opts.start, opts.end);
	var years = GetDate.getLoopArray(opts.start.substring(0, 4), opts.end.substring(0, 4));
    // var months = GetDate.getLoopArray(1, 12);
	var months = GetDate.getMonth(opts.curyear,opts)
    var days = GetDate.getMonthDay(opts.curyear,opts.curmonth,opts);
	//console.log(opts)
	var hours = GetDate.getHours(opts.curday,opts);
	// var hours = GetDate.getLoopArray(9, 17);
    var minutes = GetDate.getMinutes(opts);
    var seconds = GetDate.getLoopArray(0, 59);
    var times = null;

    switch (opts.fields) {
      case 'year':
        times = [years]
        break;
      case 'month':
        times = [years, months]
        break;
      case 'day':
        times = [years, months, days]
        break;
      case 'hour':
        times = [years, months, days, hours]
        break;
      case 'minute':
        times = [years, months, days, hours, minutes]
        break;
      case 'second':
        times = [years, months, days, hours, minutes, seconds]
        break;
      default:
        times = [years, months, days, hours, minutes, seconds]
    }
    return times;
  },
  getIndex(arr,target){
    let len = arr.length;
    for(let i = 0; i < len; i++){
      if(arr[i] == target){
        return i;
      }
    }
  },
  getTimeIndex(arrs, targets){
    let len = arrs.length;
    let arr = [];
    for(let i = 0; i < len; i++){
		let itemindex=GetDate.getIndex(arrs[i], targets[i]);
		if(itemindex){
			 arr.push(GetDate.getIndex(arrs[i], targets[i]))
		} else
		{
			 arr.push(0)
		}
     
    }
    return arr;
  },
  error(str){
	  console.error(str);
  }
}

module.exports = GetDate; 