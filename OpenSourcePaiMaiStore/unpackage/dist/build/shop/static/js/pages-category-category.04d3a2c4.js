(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["pages-category-category"],{"0667":function(t,e,n){var i=n("5f8f");"string"===typeof i&&(i=[[t.i,i,""]]),i.locals&&(t.exports=i.locals);var a=n("4f06").default;a("9de31092",i,!0,{sourceMap:!1,shadowMode:!1})},"23fc":function(t,e,n){"use strict";var i={"min-badge":n("a512").default},a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("v-uni-view",{staticClass:"content"},[n("uni-page-head",[n("div",{staticClass:"uni-page-head ",staticStyle:{"background-color":"#7E0076",color:"rgb(0, 0, 0)"}},[n("div",{staticClass:"uni-page-head-hd"},[n("div",{staticClass:"uni-page-head-btn",staticStyle:{display:"none","background-color":"rgba(0, 0, 0, 0.5)"}},[n("i",{staticClass:"uni-btn-icon",staticStyle:{color:"rgb(255, 255, 255)","font-size":"27px"}})])]),n("div",{staticClass:"uni-page-head-search",staticStyle:{"border-radius":"16px","background-color":"rgba(251, 251, 251, 0.6)"}},[n("div",{staticClass:"uni-page-head-search-placeholder uni-page-head-search-placeholder-left",staticStyle:{color:"rgb(251, 251, 251)"}}),n("uni-input",{staticClass:"uni-page-head-search-input",staticStyle:{color:"rgb(0, 0, 0)"}},[n("div",{staticClass:"uni-input-wrapper"},[n("div",{staticClass:"uni-input-placeholder",staticStyle:{color:"rgb(251, 251, 251)"}}),n("v-uni-form",{staticClass:"uni-input-form",attrs:{action:""}},[n("v-uni-input",{staticClass:"uni-input-input",attrs:{maxlength:"140",step:"",placeholder:"搜索商品",autocomplete:"off",type:"search"},on:{confirm:function(e){arguments[0]=e=t.$handleEvent(e),t.navToSearch.apply(void 0,arguments)}},model:{value:t.txtSearch,callback:function(e){t.txtSearch=e},expression:"txtSearch"}})],1)],1)])],1),n("div",{staticClass:"uni-page-head-ft"},[n("v-uni-view",{staticClass:"set-box"},[n("v-uni-view",{staticClass:"icon",on:{click:function(e){arguments[0]=e=t.$handleEvent(e),t.navTo("/pages/notice/notice")}}},[n("min-badge",{attrs:{count:t.userInfoCount,maxCount:99}},[n("v-uni-image",{attrs:{src:"/static/newimg/message.png",mode:""}})],1)],1)],1)],1)])]),n("v-uni-view",{staticClass:"item_content"},[n("v-uni-view",{staticClass:"left-aside"},t._l(t.catoryList,function(e){return n("v-uni-view",{key:e.id,staticClass:"f-item b-b",class:{active:e.id===t.sel_catoryId},on:{click:function(n){arguments[0]=n=t.$handleEvent(n),t.tabtap(e)}}},[t._v(t._s(e.name))])}),1),n("v-uni-view",{staticClass:"guess-section"},[n("v-uni-view",{staticClass:"guess-left"},[t._l(t.goodsList,function(e,i){return[i%2==0?n("v-uni-view",{key:i+"_0",staticClass:"guess-item",on:{click:function(n){arguments[0]=n=t.$handleEvent(n),t.navToDetailPage(e)}}},[n("v-uni-view",{staticClass:"image-wrapper"},[n("v-uni-image",{attrs:{src:e.picUrl,mode:"aspectFill"}})],1),n("v-uni-view",{staticClass:"guess-s-bottom"},[n("v-uni-view",{staticClass:"title clamp"},[t._v(t._s(e.name))])],1),n("v-uni-view",{staticClass:"guess-bug"},[n("v-uni-text",{staticClass:"price"},[t._v("￥"+t._s(e.retailPrice))])],1)],1):t._e()]})],2),n("v-uni-view",{staticClass:"guess-right"},[t._l(t.goodsList,function(e,i){return[i%2==1?n("v-uni-view",{key:i+"_0",staticClass:"guess-item",on:{click:function(n){arguments[0]=n=t.$handleEvent(n),t.navToDetailPage(e)}}},[n("v-uni-view",{staticClass:"image-wrapper"},[n("v-uni-image",{attrs:{src:e.picUrl,mode:"aspectFill"}})],1),n("v-uni-view",{staticClass:"guess-s-bottom"},[n("v-uni-view",{staticClass:"title clamp"},[t._v(t._s(e.name))])],1),n("v-uni-view",{staticClass:"guess-bug"},[n("v-uni-text",{staticClass:"price"},[t._v("￥"+t._s(e.retailPrice))])],1)],1):t._e()]})],2)],1)],1)],1)},r=[];n.d(e,"b",function(){return a}),n.d(e,"c",function(){return r}),n.d(e,"a",function(){return i})},"3b8d":function(t,e,n){"use strict";n.r(e),n.d(e,"default",function(){return o});var i=n("795b"),a=n.n(i);function r(t,e,n,i,r,o,s){try{var c=t[o](s),u=c.value}catch(l){return void n(l)}c.done?e(u):a.a.resolve(u).then(i,r)}function o(t){return function(){var e=this,n=arguments;return new a.a(function(i,a){var o=t.apply(e,n);function s(t){r(o,i,a,s,c,"next",t)}function c(t){r(o,i,a,s,c,"throw",t)}s(void 0)})}}},"43d2":function(t,e,n){"use strict";var i,a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("v-uni-view",{staticClass:"min-badge"},[t._t("default"),t.dot?n("v-uni-view",{staticClass:"min-badge-dot"}):t.count?n("v-uni-view",{staticClass:"min-badge-count"},[t._v(t._s(t.finalCount))]):t._e()],2)},r=[];n.d(e,"b",function(){return a}),n.d(e,"c",function(){return r}),n.d(e,"a",function(){return i})},5761:function(t,e,n){var i=n("cbf3");"string"===typeof i&&(i=[[t.i,i,""]]),i.locals&&(t.exports=i.locals);var a=n("4f06").default;a("7af94994",i,!0,{sourceMap:!1,shadowMode:!1})},"5e73":function(t,e,n){"use strict";n.r(e);var i=n("23fc"),a=n("bea7");for(var r in a)"default"!==r&&function(t){n.d(e,t,function(){return a[t]})}(r);n("bc7b");var o,s=n("f0c5"),c=Object(s["a"])(a["default"],i["b"],i["c"],!1,null,"77b6c54a",null,!1,i["a"],o);e["default"]=c.exports},"5f8f":function(t,e,n){e=t.exports=n("2350")(!1),e.push([t.i,'@charset "UTF-8";\n/* 页面左右间距 */\n/* 文字尺寸 */\n/*文字颜色*/\n/* 边框颜色 */\n/* 图片加载中颜色 */\n/* 行为相关颜色 */uni-page-body[data-v-77b6c54a],\n.content[data-v-77b6c54a]{height:100%;background-color:#f8f8f8}.content[data-v-77b6c54a]{display:-webkit-box;display:-webkit-flex;display:flex}.set-box[data-v-77b6c54a]{-webkit-box-flex:1;-webkit-flex:1;flex:1}.set-box .icon[data-v-77b6c54a]{float:right;margin-left:%?0?%;margin-right:%?20?%}.set-box uni-image[data-v-77b6c54a]{width:%?44?%;height:%?40?%}.item_content[data-v-77b6c54a]{background:#fff;margin-top:45px;display:-webkit-box;display:-webkit-flex;display:flex;font-size:%?29?%}.item_content .left-aside[data-v-77b6c54a]{\n    /* bottom: 50px; */\n    /* flex-shrink: 0; */width:%?200?%;\n    /* height: 100%; */background-color:#fff}.item_content .right-aside[data-v-77b6c54a]{\n    /* flex: 1; */overflow:hidden;padding-left:%?20?%}.f-item[data-v-77b6c54a]{display:-webkit-box;display:-webkit-flex;display:flex;-webkit-box-align:center;-webkit-align-items:center;align-items:center;-webkit-box-pack:center;-webkit-justify-content:center;justify-content:center;width:100%;height:%?100?%;font-size:%?28?%;color:#606266;position:relative}.f-item.active[data-v-77b6c54a]{color:#fa436a;background:#f8f8f8}.f-item.active[data-v-77b6c54a]:before{content:"";position:absolute;left:0;top:50%;-webkit-transform:translateY(-50%);transform:translateY(-50%);height:%?36?%;width:%?8?%;background-color:#fa436a;border-radius:0 4px 4px 0;opacity:.8}.guess-section[data-v-77b6c54a]{display:-webkit-box;display:-webkit-flex;display:flex;-webkit-justify-content:space-around;justify-content:space-around;-webkit-flex-wrap:wrap;flex-wrap:wrap;padding:0 %?27?%;width:75vw;margin:%?0?% 0 %?100?% 0}.guess-section .guess-left[data-v-77b6c54a], .guess-section .guess-right[data-v-77b6c54a]{-webkit-box-flex:1;-webkit-flex:1;flex:1;display:-webkit-box;display:-webkit-flex;display:flex;-webkit-flex-wrap:wrap;flex-wrap:wrap;-webkit-box-orient:vertical;-webkit-box-direction:normal;-webkit-flex-direction:column;flex-direction:column}.guess-section .guess-left[data-v-77b6c54a]{padding-right:%?10?%;margin-bottom:50px;width:120px}.guess-section .guess-right[data-v-77b6c54a]{padding:0 0 0 %?10?%;margin-bottom:50px;width:120px}.guess-section .guess-item[data-v-77b6c54a]{display:-webkit-box;display:-webkit-flex;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-webkit-flex-direction:column;flex-direction:column;width:100%;-webkit-flex-shrink:0;flex-shrink:0;padding-bottom:%?22?%;margin:%?17?% 0 0 0;background:#fff;box-shadow:0 0 %?9?% 0 rgba(47,47,47,.1);height:152px}.guess-section .image-wrapper[data-v-77b6c54a]{width:80%;height:%?327?%;border-radius:3px;overflow:hidden}.guess-section .image-wrapper uni-image[data-v-77b6c54a]{width:100%;height:100%;opacity:1}.guess-section .title[data-v-77b6c54a]{font-size:%?29?%;font-weight:700;font-weight:800;color:#303133;margin:%?14?% 0 0;overflow:hidden;text-overflow:ellipsis;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical}.guess-section .price[data-v-77b6c54a]{font-size:%?24?%;color:#ff3b30;font-weight:700}.guess-section .guess-s-bottom[data-v-77b6c54a]{padding:0 %?27?% 0 %?11?%;display:-webkit-box;display:-webkit-flex;display:flex;-webkit-box-pack:left;-webkit-justify-content:left;justify-content:left}.guess-section .guess-s-bottom .clamp[data-v-77b6c54a]{white-space:normal}.guess-section .guess-s-bottom .clamp uni-text[data-v-77b6c54a]{background:red;color:#fff;font-size:%?19?%;padding:%?6?% %?15?%;border-radius:%?13?%;line-height:%?18?%;display:inline-block;margin:0 %?10?% 0 %?13?%}.guess-section .guess-bug[data-v-77b6c54a]{padding:0 %?27?% 0 %?11?%;display:-webkit-box;display:-webkit-flex;display:flex;-webkit-box-align:center;-webkit-align-items:center;align-items:center;-webkit-box-pack:justify;-webkit-justify-content:space-between;justify-content:space-between;margin:%?3?% 0 0 0}.guess-section .guess-bug uni-image[data-v-77b6c54a]{width:%?48?%;height:%?48?%}body.?%PAGE?%[data-v-77b6c54a]{background-color:#f8f8f8}',""])},6558:function(t,e,n){"use strict";var i=n("288e");Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0,n("ac6a"),n("96cf");var a=i(n("3b8d")),r=i(n("a512")),o={components:{minBadge:r.default},data:function(){return{txtSearch:"",userInfoCount:0,catoryList:[],goodsList:[],page:1,size:10,loading:!0,sel_catoryId:""}},onLoad:function(){this.loadData()},methods:{navTo:function(t){uni.navigateTo({url:t})},navToSearch:function(){uni.navigateTo({url:"/pages/category/search?keyword="+this.txtSearch})},loadData:function(){var t=(0,a.default)(regeneratorRuntime.mark(function t(){var e;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:e=this,e.$http({method:"GET",params:{parentClassName:"特产"},url:this.global.target+"/wx/catalog/getClassListAndFirstClassGoods"}).then(function(t){e.catoryList=t.data.data.categoryList,null!=e.catoryList&&e.catoryList.length>0&&(e.sel_catoryId=e.catoryList[0]["id"]),e.goodsList=t.data.data.firstCategoryGoodsList},function(t){});case 2:case"end":return t.stop()}},t,this)}));function e(){return t.apply(this,arguments)}return e}(),onReachBottom:function(){this.getGoodslist()},getGoodslist:function(){var t=(0,a.default)(regeneratorRuntime.mark(function t(e){var n,i,a,r,o;return regeneratorRuntime.wrap(function(t){while(1)switch(t.prev=t.next){case 0:n=this.loading,uni.showLoading(),1==n?(i=this,a=i.sel_catoryId,console.log(a),r=i.page,o=i.size,i.$http({method:"GET",params:{categoryId:a,page:r,size:o},url:this.global.target+"/wx/catalog/getGoodsListByCategoryId"}).then(function(t){var n=t.data.data.total;0==n&&(uni.hideLoading(),i.$api.msg("无数据"));var a=r*o;n<=a&&(i.loading=!1);var s=t.data.data.list;e?(i.goodsList=s,document.documentElement.scrollTop=0):s.forEach(function(t){i.goodsList.push(t)}),i.page++,uni.hideLoading()},function(t){uni.hideLoading()})):uni.hideLoading();case 3:case"end":return t.stop()}},t,this)}));function e(e){return t.apply(this,arguments)}return e}(),navToDetailPage:function(t){var e=t.id;uni.navigateTo({url:"/pages/product/product?id=".concat(e)})},tabtap:function(t){this.page=1,this.loading=!0,this.sel_catoryId=t.id,this.getGoodslist(1)}}};e.default=o},"6cec":function(t,e,n){"use strict";n.r(e);var i=n("b08c"),a=n.n(i);for(var r in i)"default"!==r&&function(t){n.d(e,t,function(){return i[t]})}(r);e["default"]=a.a},"96cf":function(t,e){!function(e){"use strict";var n,i=Object.prototype,a=i.hasOwnProperty,r="function"===typeof Symbol?Symbol:{},o=r.iterator||"@@iterator",s=r.asyncIterator||"@@asyncIterator",c=r.toStringTag||"@@toStringTag",u="object"===typeof t,l=e.regeneratorRuntime;if(l)u&&(t.exports=l);else{l=e.regeneratorRuntime=u?t.exports:{},l.wrap=m;var f="suspendedStart",d="suspendedYield",h="executing",p="completed",g={},v={};v[o]=function(){return this};var b=Object.getPrototypeOf,w=b&&b(b(P([])));w&&w!==i&&a.call(w,o)&&(v=w);var y=L.prototype=k.prototype=Object.create(v);_.prototype=y.constructor=L,L.constructor=_,L[c]=_.displayName="GeneratorFunction",l.isGeneratorFunction=function(t){var e="function"===typeof t&&t.constructor;return!!e&&(e===_||"GeneratorFunction"===(e.displayName||e.name))},l.mark=function(t){return Object.setPrototypeOf?Object.setPrototypeOf(t,L):(t.__proto__=L,c in t||(t[c]="GeneratorFunction")),t.prototype=Object.create(y),t},l.awrap=function(t){return{__await:t}},C(E.prototype),E.prototype[s]=function(){return this},l.AsyncIterator=E,l.async=function(t,e,n,i){var a=new E(m(t,e,n,i));return l.isGeneratorFunction(e)?a:a.next().then(function(t){return t.done?t.value:a.next()})},C(y),y[c]="Generator",y[o]=function(){return this},y.toString=function(){return"[object Generator]"},l.keys=function(t){var e=[];for(var n in t)e.push(n);return e.reverse(),function n(){while(e.length){var i=e.pop();if(i in t)return n.value=i,n.done=!1,n}return n.done=!0,n}},l.values=P,O.prototype={constructor:O,reset:function(t){if(this.prev=0,this.next=0,this.sent=this._sent=n,this.done=!1,this.delegate=null,this.method="next",this.arg=n,this.tryEntries.forEach(G),!t)for(var e in this)"t"===e.charAt(0)&&a.call(this,e)&&!isNaN(+e.slice(1))&&(this[e]=n)},stop:function(){this.done=!0;var t=this.tryEntries[0],e=t.completion;if("throw"===e.type)throw e.arg;return this.rval},dispatchException:function(t){if(this.done)throw t;var e=this;function i(i,a){return s.type="throw",s.arg=t,e.next=i,a&&(e.method="next",e.arg=n),!!a}for(var r=this.tryEntries.length-1;r>=0;--r){var o=this.tryEntries[r],s=o.completion;if("root"===o.tryLoc)return i("end");if(o.tryLoc<=this.prev){var c=a.call(o,"catchLoc"),u=a.call(o,"finallyLoc");if(c&&u){if(this.prev<o.catchLoc)return i(o.catchLoc,!0);if(this.prev<o.finallyLoc)return i(o.finallyLoc)}else if(c){if(this.prev<o.catchLoc)return i(o.catchLoc,!0)}else{if(!u)throw new Error("try statement without catch or finally");if(this.prev<o.finallyLoc)return i(o.finallyLoc)}}}},abrupt:function(t,e){for(var n=this.tryEntries.length-1;n>=0;--n){var i=this.tryEntries[n];if(i.tryLoc<=this.prev&&a.call(i,"finallyLoc")&&this.prev<i.finallyLoc){var r=i;break}}r&&("break"===t||"continue"===t)&&r.tryLoc<=e&&e<=r.finallyLoc&&(r=null);var o=r?r.completion:{};return o.type=t,o.arg=e,r?(this.method="next",this.next=r.finallyLoc,g):this.complete(o)},complete:function(t,e){if("throw"===t.type)throw t.arg;return"break"===t.type||"continue"===t.type?this.next=t.arg:"return"===t.type?(this.rval=this.arg=t.arg,this.method="return",this.next="end"):"normal"===t.type&&e&&(this.next=e),g},finish:function(t){for(var e=this.tryEntries.length-1;e>=0;--e){var n=this.tryEntries[e];if(n.finallyLoc===t)return this.complete(n.completion,n.afterLoc),G(n),g}},catch:function(t){for(var e=this.tryEntries.length-1;e>=0;--e){var n=this.tryEntries[e];if(n.tryLoc===t){var i=n.completion;if("throw"===i.type){var a=i.arg;G(n)}return a}}throw new Error("illegal catch attempt")},delegateYield:function(t,e,i){return this.delegate={iterator:P(t),resultName:e,nextLoc:i},"next"===this.method&&(this.arg=n),g}}}function m(t,e,n,i){var a=e&&e.prototype instanceof k?e:k,r=Object.create(a.prototype),o=new O(i||[]);return r._invoke=j(t,n,o),r}function x(t,e,n){try{return{type:"normal",arg:t.call(e,n)}}catch(i){return{type:"throw",arg:i}}}function k(){}function _(){}function L(){}function C(t){["next","throw","return"].forEach(function(e){t[e]=function(t){return this._invoke(e,t)}})}function E(t){function e(n,i,r,o){var s=x(t[n],t,i);if("throw"!==s.type){var c=s.arg,u=c.value;return u&&"object"===typeof u&&a.call(u,"__await")?Promise.resolve(u.__await).then(function(t){e("next",t,r,o)},function(t){e("throw",t,r,o)}):Promise.resolve(u).then(function(t){c.value=t,r(c)},function(t){return e("throw",t,r,o)})}o(s.arg)}var n;function i(t,i){function a(){return new Promise(function(n,a){e(t,i,n,a)})}return n=n?n.then(a,a):a()}this._invoke=i}function j(t,e,n){var i=f;return function(a,r){if(i===h)throw new Error("Generator is already running");if(i===p){if("throw"===a)throw r;return z()}n.method=a,n.arg=r;while(1){var o=n.delegate;if(o){var s=S(o,n);if(s){if(s===g)continue;return s}}if("next"===n.method)n.sent=n._sent=n.arg;else if("throw"===n.method){if(i===f)throw i=p,n.arg;n.dispatchException(n.arg)}else"return"===n.method&&n.abrupt("return",n.arg);i=h;var c=x(t,e,n);if("normal"===c.type){if(i=n.done?p:d,c.arg===g)continue;return{value:c.arg,done:n.done}}"throw"===c.type&&(i=p,n.method="throw",n.arg=c.arg)}}}function S(t,e){var i=t.iterator[e.method];if(i===n){if(e.delegate=null,"throw"===e.method){if(t.iterator.return&&(e.method="return",e.arg=n,S(t,e),"throw"===e.method))return g;e.method="throw",e.arg=new TypeError("The iterator does not provide a 'throw' method")}return g}var a=x(i,t.iterator,e.arg);if("throw"===a.type)return e.method="throw",e.arg=a.arg,e.delegate=null,g;var r=a.arg;return r?r.done?(e[t.resultName]=r.value,e.next=t.nextLoc,"return"!==e.method&&(e.method="next",e.arg=n),e.delegate=null,g):r:(e.method="throw",e.arg=new TypeError("iterator result is not an object"),e.delegate=null,g)}function T(t){var e={tryLoc:t[0]};1 in t&&(e.catchLoc=t[1]),2 in t&&(e.finallyLoc=t[2],e.afterLoc=t[3]),this.tryEntries.push(e)}function G(t){var e=t.completion||{};e.type="normal",delete e.arg,t.completion=e}function O(t){this.tryEntries=[{tryLoc:"root"}],t.forEach(T,this),this.reset(!0)}function P(t){if(t){var e=t[o];if(e)return e.call(t);if("function"===typeof t.next)return t;if(!isNaN(t.length)){var i=-1,r=function e(){while(++i<t.length)if(a.call(t,i))return e.value=t[i],e.done=!1,e;return e.value=n,e.done=!0,e};return r.next=r}}return{next:z}}function z(){return{value:n,done:!0}}}(function(){return this||"object"===typeof self&&self}()||Function("return this")())},a512:function(t,e,n){"use strict";n.r(e);var i=n("43d2"),a=n("6cec");for(var r in a)"default"!==r&&function(t){n.d(e,t,function(){return a[t]})}(r);n("ca86");var o,s=n("f0c5"),c=Object(s["a"])(a["default"],i["b"],i["c"],!1,null,"353b6818",null,!1,i["a"],o);e["default"]=c.exports},b08c:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0,n("c5f6");var i={props:{count:{type:Number,default:0},maxCount:{type:Number,default:99},dot:{type:Boolean,default:!1}},computed:{finalCount:function(){return this.count>this.maxCount?"".concat(this.maxCount,"+"):this.count}}};e.default=i},bc7b:function(t,e,n){"use strict";var i=n("0667"),a=n.n(i);a.a},bea7:function(t,e,n){"use strict";n.r(e);var i=n("6558"),a=n.n(i);for(var r in i)"default"!==r&&function(t){n.d(e,t,function(){return i[t]})}(r);e["default"]=a.a},ca86:function(t,e,n){"use strict";var i=n("5761"),a=n.n(i);a.a},cbf3:function(t,e,n){e=t.exports=n("2350")(!1),e.push([t.i,".min-badge[data-v-353b6818]{position:relative;display:inline-block;line-height:1;vertical-align:middle}.min-badge-count[data-v-353b6818]{position:absolute;-webkit-transform:translateX(50%);transform:translateX(50%);top:%?-12?%;right:0;height:%?30?%;border-radius:%?15?%;min-width:%?30?%;background:#dd524d;color:#fff;line-height:%?30?%;text-align:center;padding:0 %?10?%;font-size:%?20?%;white-space:nowrap;z-index:10;box-shadow:0 0 0 1px #fff;box-sizing:border-box}.min-badge-dot[data-v-353b6818]{position:absolute;-webkit-transform:translateX(-50%);transform:translateX(-50%);top:%?-8?%;right:%?-16?%;height:%?16?%;width:%?16?%;border-radius:100%;background:#dd524d;z-index:10;box-shadow:0 0 0 %?1?% #fff}",""])}}]);