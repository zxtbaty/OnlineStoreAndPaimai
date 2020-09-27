

/**
 * 日期格式转换模块，还可以从字符串中提取日期，同时提供了几个数字格式转换函数。 
 * 2019.5.12  v1.0.0
 * 
## 背景
当需要将日期转换为指定的格式时，或者需要从某个字符串中按照指定的格式提取日期，均可使用本模块。

## 使用说明  
格式标记的含意:   
yyyy-四位阿拉伯数字的年份，如： 2018  
YYYY-四位中文数字的年份，如： 二零一八  
yy-两位阿拉伯数字的年份，如： 18  
YY-两位中文数字的年份，如： 一八  
mm-一位或两位阿拉伯数字的月数，如： 3 、 12   
0m-两位阿拉伯数字的月数，如： 03 、 12  
MM-一位或两位中文数字的月数，如：  三、 十二   
dd-一位或两位阿拉伯数字的日数，如： 3 、 12   
0d-两位阿拉伯数字的日数，如： 03 、 12、28   
DD-中文数字的日数，如：  三、 十二、二十八   
hh-一位或两位阿拉伯数字的小时数，如： 3 、 12   
0h-两位阿拉伯数字的小时数，如： 03 、 12   
HH-中文数字的小时数，如：  三、 十二、二十三   
ff-一位或两位阿拉伯数字的分钟数，如： 3 、 12、56  
0f-两位阿拉伯数字的分钟数，如： 03 、 12  
0F-中文数字的分钟，如：  零三、 十二、二十三  
FF-中文数字的分钟，如：  三、 十二、二十三   
ss-一位或两位阿拉伯数字的秒数，如： 3 、 12   
0s-两位阿拉伯数字的秒数，如： 03 、 12   
0S-中文数字的秒数，如：  零三、 十二、二十三   
SS-中文数字的秒数，如：  三、 十二、二十三   
w-阿拉伯数字的星期数，如：  1、6、7  
W-中文数字的星期数，如：  一、三、六、日   
WT-中文数字的星期数，如：  一、三、六、天   
  
### 属性  
这些属性是可读可写的，如果直接更改，则会自动计算其关联的其它值。  
dataformat.year; //年 [1000, 3000]  
dataformat.moth; //月 [1, 12]  
dataformat.day; //日 [1, 31]  
dataformat.hour; //时 [0, 23]  
dataformat.minute; //分 [0, 59]  
dataformat.second; //秒 [0, 59]  
dataformat.mscond; //毫秒 [0, 999]  
dataformat.week; //星期 [1, 7]  
dataformat.timestamp; //时间戳  

### 构造函数
构造函数内部直接调用的 setTime(ms, formatStr);
  
### toString(formatStr)  
  
使用举例:   
1. toString() //结果: '2014-11-7 03:08:01'  
2. toString('yyyy-mm-dd hh:ff:ss') //结果: '2014-11-11 13:12:34'  
3. toString('yyyy年mm月dd日') //结果: '2014年11月11日'  
4. toString('yy年mm月dd日') //结果: '14年11月11日'  
5. toString('YY年MM月DD日') //结果: '一四年十一月八日'  
6. toString('星期W') //结果: '星期日'  或 '星期三'
7. toString('星期WT')  //结果: '星期天'  或 '星期三'
8. toString('星期w') //结果: '星期1'   或 '星期7'
9. toString('hh:0f:ss') //结果: '3:04:5'  
10. toString('date') //结果: '2014-11-7'  
11. toString('time') //结果: '03:08:01'  
  
### setTime(ms, formtStr)  
设置时间。如果存在 formatStr， 则表示从字符串 ms 中按 formatStr中的规则提取时间。  
  
使用举例:  
1. setTime( new Date() ); //传入一个时间对象  
2. setTime( new Date().getTime() ); //传入一个毫秒数  
3. setTime( new Date().getTime() ); //传入一个毫秒数  
4. setTime( new Date().getTime() + '' ); //传入一个毫秒数的字符串  
5. setTime(); //传入其它的值或不传入，则创建当前时间  
6. setTime(str, formatStr); //传入两个参数，第一个为字符串，第二个为格式规则，表示按照规则从字符串中提取时间  
7. setTime('今天是2014年8月9号，天气特别好，但明天是2014年8月10号，....', '明天是yyyy年mm月dd号');  // 得到  2014.8.10 0:0:0
  
### fillChar(num, len, char)  
字符填充  
  
使用举例:   
1. fillChar(44, 5, '*'); //结果: '***44'   
2. fillChar(44, 5); //结果: '00044'   
3. fillChar('44', 5); //结果: '   44'   
4. fillChar('aaa', 5); //结果: '   aa'   
5. fillChar('aaa', 5, '$'); //结果: '$$aaa'   
5. fillChar('aaa', 1, '$'); //结果: 'aaa'  //指定的长度太小，不做任何操作。  
  
### perNumToChinese(num)  
将阿拉伯数字每一位对应转换为中文的格式。  
  
使用举例:   
1. perNumToChinese(34); //结果: '三四'  
2. perNumToChinese(3874); //结果: '三八七四'  
3. perNumToChinese('3874'); //结果: '三八七四'  
4. perNumToChinese('404'); //结果: '四零四'    
5. perNumToChinese('0  04   '); //结果: '零零四'  
6. perNumToChinese('  '); //结果: ''  
  
### perChineseToNum(numStr)
将中文的格式的数字每一位对应转换为阿拉伯数字。 是perNumToChinese(num)的逆操作。  
  
使用举例:   
1. perChineseToNum('三四'); //结果: 34  
2. perChineseToNum('三八七四'); //结果: 3874  
3. perChineseToNum('四零四'); //结果: 404  
4. perChineseToNum('零零四'); //结果: 4  
5. perChineseToNum('  '); //结果: NaN  
  
### numToChinese(num)  
将阿拉伯数字转换为中文的格式 , 最多只能处理13位数(万亿)  
  
使用举例:   
1. numToChinese(0); //结果: '零'  
2. numToChinese(5); //结果: '五'  
3. numToChinese(16); //结果: '十六'  
4. numToChinese(34); //结果: '三十四'  
5. numToChinese(106); //结果: '一百零六'  
6. numToChinese(886); //结果: '八百八十六'  
7. numToChinese(1004); //结果: '一千零四'  
8. numToChinese(1000); //结果: '一千'  
9. numToChinese(9904); //结果: '九千九百零四'  
10. numToChinese(19904); //结果: '一万九千九百零四'  
11. numToChinese(10004); //结果: '一万零四'  
12. numToChinese(10000); //结果: '一万'  
13. numToChinese(100404); //结果: '十万零四百零四'  
14. numToChinese(9000000); //结果: '九百万'  
15. numToChinese(90000000); //结果: '九千万'  
16. numToChinese(900000000); //结果: '九亿'  
17. numToChinese(9000000000); //结果: '九十亿'  
18. numToChinese(9020030401); //结果: '九十亿零二千零三万零四百零一'  
19. numToChinese(90000000000); //结果: '九百亿'  
20. numToChinese(900000000000); //结果: '九千亿'  
21. numToChinese(9000000000000); //结果: '九万亿'  
22. numToChinese(90000000000000); //结果: undefined  
  
  
### chineseToNum (numStr)
将中文的格式的数字转换为阿拉伯数字, 最多只能处理13位数(万亿)。 是 numToChinese(num) 函数的逆操作。  
  
使用举例:   
1. chineseToNum('十六'); //结果: 16  
2. chineseToNum('一万九千九百零四'); //结果: 19904  
3. chineseToNum('十万零四百零四'); //结果: 100404   
4. chineseToNum('九十亿零二千零三万零四百零一'); //结果: 9020030401  
 */
export default class DateFormat{
    constructor(ms, formatStr){
        this.setTime(ms, formatStr);
    }

    set year(y){ if(this.y !== y){ this.__date.setFullYear(y); this.setTime(this.__date); }  }
    get year(){ return this.y; }

    set month(y){ if(this.m !== y){ this.__date.setMonth(y-1); this.setTime(this.__date); }  }
    get month(){ return this.m; }

    set day(y){ if(this.d !== y){ this.__date.setDate(y); this.setTime(this.__date); }  }
    get day(){ return this.d; }

    set hour(y){ if(this.h !== y){ this.__date.setHours(y); this.setTime(this.__date); }  }
    get hour(){ return this.h; }

    set minute(y){ if(this.f !== y){ this.__date.setMinutes(y); this.setTime(this.__date); }  }
    get minute(){ return this.f; }

    set second(y){ if(this.s !== y){ this.__date.setSeconds(y); this.setTime(this.__date); }  }
    get second(){ return this.s; }

    set msecond(y){ if(this.ms !== y){ this.__date.setMilliseconds(y); this.setTime(this.__date); }  }
    get msecond(){ return this.ms; }

    get week(){ return this.w; }

     setTime(ms, formatStr){  //切换时间
        if(formatStr && typeof ms === 'string'){
            let result;
            if(this.__analysis){
                result = this.__analysis(ms, formatStr);
            }
            if(!result){
                console.log('匹配时间失败，默认设置为当前时间');
                ms = '';
            }else{
                ms = result;
            }
        }
        let date;
        if(ms instanceof Date){
            date = ms;
        }else if(typeof ms === 'number'){
            date = new　Date(ms);
        }else if( parseInt(ms) > 0 ){
            date = new　Date(parseInt(ms));
        }else{
            if(typeof ms === 'object' && ms.y && ms.m && ms.d){
                date = new　Date(ms.y, ms.m-1, ms.d, ms.h || 0, ms.f || 0, ms.s || 0, ms.ms || 0);
            }else{
                date = new　Date();
            }
        }

        this.y = date.getFullYear(), //年 [1000, 3000]
        this.m = date.getMonth() + 1, //月 [1, 12]
        this.d = date.getDate(), //日 [1, 31]
        this.h = date.getHours(), //时 [0, 23]
        this.f = date.getMinutes(), //分 [0, 59]
        this.s = date.getSeconds(), //秒 [0, 59]
        this.ms = date.getMilliseconds(), //秒 [0, 9999]
        this.w = date.getDay(); //星期 [1, 7]
        if(this.w === 0){  //0表示星期天
            this.w = 7;
        }
        this.timestamp = date.getTime(); //时间戳

        this.__date = date;
        
        return this;
     }

/***************************************************** 时间格式化 *********************************************** */
     toString(formatStr){  //将时间转换为格式的字符串
         if(formatStr){
            if(formatStr === 'time'){
                //03:00:01
                return this.fillChar(this.h, 2) + ':' + this.fillChar(this.f, 2) + ':' + this.fillChar(this.s, 2);
            }else if(formatStr === 'date'){
                //2014-11-7
                return this.y + '-' + this.m + '-' + this.d;
            }else{
               return this.__replaceTime(formatStr);
            }
            
        }else{
            //2014-11-7 03:00:01
            return this.y + '-' + this.m + '-' + this.d + ' ' + this.fillChar(this.h, 2) + ':' + this.fillChar(this.f, 2) + ':' + this.fillChar(this.s, 2);
        }
     }

     valueOf(){
         return this.toString();
     }
 
     __replaceTime(format){
        var str = 
        'y{4}|Y{4}|y{2}|Y{2}|mm|MM|0m|dd|DD|0d|hh|HH|0h|ff|FF|0F|0f|ss|SS|0S|0s|w|WT|W';
        var reg = new RegExp(str, 'g');


        format = format.replace(reg, (mstr)=>{
           switch(mstr){
              case 'yyyy': return this.y;                        // [0000, 9999]
              case 'YYYY': return this.perNumToChinese(this.y);  // [零零零零-九九九九]   
              case 'yy': return ('' + this.y).slice(-2);         // [00, 99]
              case 'YY': return this.perNumToChinese( +('' + this.y).slice(-2) ); // [零零-九九]    
              case 'mm': return this.m;                          // [1, 12]
              case 'MM': return this.numToChinese(this.m);       // [一-十二]    
              case '0m': return this.m < 10 ? '0'+ this.m : this.m; // [01, 12]
              case 'dd': return this.d;                          // [1, 31]
              case 'DD': return this.numToChinese(this.d);       // [一-三十一]  
              case '0d': return this.d < 10 ? '0' + this.d : this.d; // [01, 31]
              case 'hh': return this.h;                          // [0, 24]
              case 'HH': return this.numToChinese(this.h);       // [零-二十四]  
              case '0h': return this.h < 10 ? '0' + this.h : this.h; // [00, 24]
              case 'ff': return this.f;                          // [0, 60]
              case 'FF': return this.numToChinese(this.f);       // [零-六十]
              case '0F': return this.f<10&&this.f>0 ? ('零' + this.numToChinese(this.f) ):this.numToChinese(this.f); // [零零-六十]   
              case '0f': return this.f < 10 ? '0' + this.f : this.f; // [00, 60]
              case 'ss': return this.s;                          // [0, 60]
              case 'SS': return this.numToChinese(this.s);       // [零-六十]
              case '0S': return this.s<10&&this.s>0 ? ('零' + this.numToChinese(this.s) ):this.numToChinese(this.s); // [零零-六十]    
              case '0s': return this.s < 10 ? '0' + this.s : this.s;  // [00, 60]
              case 'w': return this.w;                           // [7]
              case 'W': return this.w == 7 ? '日' : this.numToChinese(this.w);  // [天|日]
              case 'WT': return this.w == 7 ? '天' : this.numToChinese(this.w);  // [天|日]
           }
        });
         return format;
     }

/***************************************************** 时间提取 *********************************************** */
     __analysis(str, rule){  //从指定的字符串中根据规则提取日期和时间。
         let resultObj;
        if(typeof rule === 'function'){
            resultObj = rule(str);
            if(!resultObj){
                return null;
            }
        }else{
            resultObj = {};
            var str11 = 
            'y{4}|Y{4}|y{2}|Y{2}|mm|MM|0m|dd|DD|0d|hh|HH|0h|ff|FF|0F|0f|ss|SS|0S|0s|w|WT|W';
            var reg = new RegExp(str11, 'g');

            // rule = 'yyyy年mm月'
            var namesArr = [null];   // [null, 'yyyy', 'mm']
            rule = rule.replace(reg, (mstr)=>{
                var result = this.__ANALYREGSTR[mstr];
                if(result){
                    namesArr.push(mstr);
                    return `(${result})`;
                }
             });
            //  rule = '(\\d{4})年([1-9]|1[012])月'

            var resultArr = str.match( new RegExp(rule) );
             if(resultArr){
                for(let i=1,val; i<namesArr.length; i++){
                    val = resultArr[i];
                    if(val == null){
                        continue;
                    }
                    switch(namesArr[i]){
                        case 'yyyy': resultObj.y = +val; break;                        // [0000, 9999]
                        case 'YYYY': resultObj.y = this.perChineseToNum(val); break;   // [零零零零-九九九九]   
                        case 'yy': resultObj.y = +val + 2000; break;          // [00, 99]
                        case 'YY': resultObj.y = this.perChineseToNum(val) + 2000; break;  // [零零-九九]    
                        case 'mm': resultObj.m = +val;    break;                        // [1, 12]
                        case 'MM': resultObj.m = this.chineseToNum(val);  break;       // [一-十二]    
                        case '0m': resultObj.m = +val;  break; // [01, 12]
                        case 'dd': resultObj.d = +val; break;                           // [1, 31]
                        case 'DD': resultObj.d = this.chineseToNum(val); break;        // [一-三十一]  
                        case '0d': resultObj.d = +val; break;  // [01, 31]
                        case 'hh': resultObj.h = +val;  break;                           // [0, 24]
                        case 'HH': resultObj.h = this.chineseToNum(val); break;        // [零-二十四]  
                        case '0h': resultObj.h = +val; break;  // [00, 24]
                        case 'ff': resultObj.f = +val;   break;                         // [0, 60]
                        case 'FF': resultObj.f = this.chineseToNum(val); break;        // [零-六十]
                        case '0F': resultObj.f = this.chineseToNum(val); break;  // [零零-六十]   
                        case '0f': resultObj.f = +val; break;   // [00, 60]
                        case 'ss': resultObj.s = +val;   break;                         // [0, 60]
                        case 'SS': resultObj.s = this.chineseToNum(val);  break;       // [零-六十]
                        case '0S': resultObj.s = this.chineseToNum(val); break;  // [零零-六十]    
                        case '0s': resultObj.s = +val; break;    // [00, 60]
                        case 'w': resultObj.w = 7; break;                             // [7]
                        case 'W': resultObj.w = 7; break;      // [天|日]
                        case 'WT': resultObj.w = 7; break;      // [天|日]
                    }
                }
             }else{
                return null;
             }
             
        }
        //   'yyyy年mm月'  '(\\d{4})年([1-9]|1[012])月'
        return resultObj;
     }

     __ANALYREGSTR = {
         'yyyy': '\\d{4}',                   // [0000, 9999]
         'YYYY': '[零一二三四五六七八九]{4}',              // [零零零零-九九九九]    
         'yy': '\\d{2}',                     // [00, 99]
         'YY': '[零一二三四五六七八九]{2}',                // [零零-九九]    
         'mm': '[1-9]|1[012]',               // [1, 12]
         'MM': '十[一二]?|[一二三四五六七八九]',// [一-十二]    
         '0m': '[0][1-9]|1[012]',             // [01, 12]
         'dd': '3[01]|[12]0|[12]?[1-9]',            //  [1, 31]
         'DD': '三十[一]?|二?十[一二三四五六七八九]|[零一二三四五六七八九十]',     // [一-三十一]  
         '0d': '[012][0-9]|3[01]',            //  [01, 31]
         'hh': '2[0-4]|[1]?[0-9]',            //  [0, 24]
         'HH': '二十[一二三]?|十[一二三四五六七八九]|[零一二三四五六七八九十]',   // [零-二十四]  
         '0h': '[01][0-9]|2[0-4]',             // [00, 24]
         'ff': '60|[1-5]?[0-9]',               // [0, 60]
         'FF': '[二三四五]十[一二三四五六七八九]|十[一二三四五六七八九]|[二三四五六]十|[零一二三四五六七八九十]',    // [零-六十]
         '0F': '零[零一二三四五六七八九]|十[一二三四五六七八九]|[二三四五]十[一二三四五六七八九]|[二三四五六]十|十', // [零零-六十]     
         '0f': '[0-5][0-9]|60',               // [00, 60]
         'ss': '60|[1-5]?[0-9]',              // [0, 60]
         'SS': '[二三四五]十[一二三四五六七八九]|十[一二三四五六七八九]|[二三四五六]十|[零一二三四五六七八九十]',    // [零-六十] 
         '0S': '零[零一二三四五六七八九]|十[一二三四五六七八九]|[二三四五]十[一二三四五六七八九]|[二三四五六]十|十', // [零零-六十]    
         '0s': '[0-5][0-9]|60',               // [00, 60]
         'w': '7',                            // [7]
         'W': '日',                        //[天|日]
         'WT': '天',                        //[天|日]
     }

/***************************************************** 字符填充部分 *********************************************** */
      /**
     * 在一个字符前面补充指定个数的字符。
     * <br><font color="red">ref: null</font> <br>
     * @public
     * @param {string|number} num -  要操作的数字或字符串。
     * @param {number} len -  要保留的最大长度。如果指定的长度不比传入的参数的本身的长度大，则不做任何操作。
     * @param {string} [char=' '|'0'] -  当长度不足时，要在最前面补足的字符。第一个参数为数字时，默认为0，否则默认为' '。
     * @example
       1. fillChar(44, 5, '*'); //结果: '***44' 
       2. fillChar(44, 5); //结果: '00044' 
       3. fillChar('44', 5); //结果: '   44' 
       4. fillChar('aaa', 5); //结果: '   aa' 
       5. fillChar('aaa', 5, '$'); //结果: '$$aaa' 
       5. fillChar('aaa', 1, '$'); //结果: 'aaa'  //指定的长度太小，不做任何操作。
     */
    fillChar(num, len, char){
        if(typeof num === 'number'){
            char = char || '0';
        }else{
            char = char || ' ';
        }
        let str = '' + num, charStr = '';

        if(str.length < len){
            for(let i=0, len1=len-str.length; i<len1; i++){
                charStr += char;
            }
            return charStr + str;
        }

        return str;
    }

/***************************************************** 数字转换部分 *********************************************** */

    __SIZEPOW = {'十': 1, '百': 2, '千': 3, '万': 4, '亿': 8};
    /** 
     * 将中文的格式的数字转换为阿拉伯数字, 最多只能处理13位数(万亿)。 是 numToChinese(num) 函数的逆操作。 
     *  @example
        1. chineseToNum('十六'); //结果: 16  
        2. chineseToNum('一万九千九百零四'); //结果: 19904  
        3. chineseToNum('十万零四百零四'); //结果: 100404   
        4. chineseToNum('九十亿零二千零三万零四百零一'); //结果: 9020030401  
     */
    chineseToNum (numStr){   //目前只能处理万亿以下的数据
        if(numStr.length === 1){
            return this.perChineseToNum(numStr);
        }
        if(numStr.length === 2 && numStr[0] === '十'){
            return this.perChineseToNum(numStr[1]) + 10;
        }

        let arr;
        if(!numStr.match(/[亿万]/)){
            arr = numStr.split('零'); 
            let num = 0, val = 0;
            for(let i=0, str=''; i<arr.length; i++){
                str = arr[i];
                for(let j=0; j<str.length; j++){
                    if(str[j].match(/[一二三四五六七八九]/)){
                        num += val;
                        val = 1;
                        val *= this.perChineseToNum(str[j]);
                    }else{
                        val *= Math.pow(10, this.__SIZEPOW[str[j]] )
                    }
                }
            }
            num += val;
            return num;
        }else{
            arr = numStr.split('亿');
            if(arr.length > 1){
                var arr1 = arr[1].split('万');
                return this.chineseToNum(arr[0])*Math.pow(10, 8) + this.chineseToNum(arr1[0])*10000 + this.chineseToNum(arr1[1]);
            }else{
                return this.chineseToNum(arr[0])*Math.pow(10, 8) + this.chineseToNum(arr[1]);
            }
        }
    }

     /** 
     * 将中文的格式的数字每一位对应转换为阿拉伯数字。 是perNumToChinese(num)的逆操作。  
     *  @example
    1. perChineseToNum('三四'); //结果: 34  
    2. perChineseToNum('三八七四'); //结果: 3874  
    3. perChineseToNum('四零四'); //结果: 404  
    4. perChineseToNum('零零四'); //结果: 4  
    5. perChineseToNum('  '); //结果: NaN  
     */
    perChineseToNum(numStr){
        return +numStr.replace(/[零一二三四五六七八九十\s]/g, (str)=>{
            var index =  this.__CHINANUM.indexOf(str);
            return index == null ? '' : index;
        });
    }

    /**
    * 将阿拉伯数字转换为对应的汉字, 最多只能处理13位数(万亿)。
    * <br><font color="red">ref: this.numToChina()、 this.weiNum() </font> <br>
    * @public
    * @param {number} num - 要转换的数字
    * @example
        1. numToChinese(0); //结果: '零'  
        2. numToChinese(5); //结果: '五'  
        3. numToChinese(16); //结果: '十六'  
        4. numToChinese(34); //结果: '三十四'  
        5. numToChinese(106); //结果: '一百零六'  
        6. numToChinese(886); //结果: '八百八十六'  
        7. numToChinese(1004); //结果: '一千零四'  
        8. numToChinese(1000); //结果: '一千'  
        9. numToChinese(9904); //结果: '九千九百零四'  
        10. numToChinese(19904); //结果: '一万九千九百零四'  
        11. numToChinese(10004); //结果: '一万零四'  
        12. numToChinese(10000); //结果: '一万'  
        13. numToChinese(100404); //结果: '十万零四百零四'  
        14. numToChinese(9000000); //结果: '九百万'  
        15. numToChinese(90000000); //结果: '九千万'  
        16. numToChinese(900000000); //结果: '九亿'  
        17. numToChinese(9000000000); //结果: '九十亿'  
        18. numToChinese(9020030401); //结果: '九十亿零二千零三万零四百零一'  
        19. numToChinese(90000000000); //结果: '九百亿'  
        20. numToChinese(900000000000); //结果: '九千亿'  
        21. numToChinese(9000000000000); //结果: '九万亿'  
        22. numToChinese(90000000000000); //结果: undefined  
    */
    numToChinese (num){
        let numStr = '' + num;
        let len = numStr.length, result;
        if(num <= 10){ //处理0~9
            return this.__CHINANUM[num]; 
        }else if(num < 20){ //处理10~20
            return '十' + this.__CHINANUM[numStr[1]]; 
        }else if(len < 6){ //处理5位数(万)及以下
            if(!this.__WEIARR){
                this.__WEIARR = this.__WEISTR.split(', ');
            }
            let lastStr, resultStr = '', index;
            for(let i=0; i<len; i++){
                lastStr = this.__CHINANUM[numStr[i]];
                if(numStr[i] === '0'){
                    index = null;
                    for(let j=i+1; j<len; j++){
                        if( numStr[j] !== '0' ){
                            index = j - 1;
                            break;
                        }
                    }
                    if(index === null){
                        break;
                    }else{
                        i = index;
                        resultStr += '零';
                    }
                }else{
                    resultStr += ( lastStr + this.__WEIARR[len - i - 1] );
                }
            }
            return resultStr;
        }else if(len < 9){ //处理8位数(千万)及以下
            let low4Str = numStr.slice(-4), heigh4Str = numStr.slice(0, -4), isZero = '';
            isZero = heigh4Str.slice(-1) === '0' ? '零' : ''; //必须用这个，如504000，应该是 五十万零四千 而不是 五十万四千
            if(isZero){
                low4Str = +low4Str;
                if(low4Str === 0){
                    isZero = '';
                }
            }
            if(+heigh4Str !== 0){
                isZero = '万' + isZero;
            }
            result = this.numToChinese( +heigh4Str ) + isZero  + this.numToChinese( +low4Str );
            return result.slice(-1) === '零' ? result.slice(0, -1) : result;

        }else if(len < 14){ //处理13位数(万亿)及以下
            let low8Str = numStr.slice(-8), heigh4Str = numStr.slice(0, -8),  isZero = '';
            isZero = heigh4Str.slice(-1) === '0' ? '零' : ''; //必须用这个
            if(isZero){
                low8Str = +low8Str;
                if(low8Str === 0){
                    isZero = '';
                }
            }
            if(+heigh4Str !== 0){
                isZero = '亿' + isZero;
            }
            result = this.numToChinese( +heigh4Str ) + isZero  + this.numToChinese( +low8Str );
            return result.slice(-1) === '零' ? result.slice(0, -1) : result;
        }
    }

    /**
     * 按数字的位数将每一位数字转换为中文(注意，可以转换多位数字)
     * <br><font color="red">ref: this.numToChina() </font> <br>
     * @public
     * @param {number|string} num - 要转换的数字
     * @example
    1. perNumToChinese(34); //结果: '三四'
    2. perNumToChinese(3874); //结果: '三八七四'
    3. perNumToChinese('3874'); //结果: '三八七四'
    4. perNumToChinese('404'); //结果: '四零四'
    5. perNumToChinese('0  04   '); //结果: '零零四'
    6. perNumToChinese('  '); //结果: ''
    */
    perNumToChinese(num){
        return ('' + num).replace(/[\d\s]/g, (index)=>{
            var result = this.__CHINANUM[index];
            return result ? result : '';
        });
    }
    __CHINANUM =  '零一二三四五六七八九十'; 
    __WEISTR =', 十, 百, 千, 万, 十万, 百万, 千万, 亿, 十亿, 百亿, 千亿, 万亿, 十万亿, 百万亿, 千万亿, 亿亿, 十亿亿, 百亿亿, 千亿亿, 万亿亿';
    __WEIARR = null;
 
 }


