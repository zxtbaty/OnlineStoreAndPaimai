<template>
  <div class="app-container">

    <img v-for="item in  huodongDetailPicLink" :key="item.picUrl" :src="item.picUrl" >

  </div>
</template>

<style>

</style>

<script>
import { readHuodongset } from '@/api/huodongset'
import { getToken } from '@/utils/auth'


export default {
  name: 'hudongsetpreview',
  data() {
    return {
      mainId:undefined,
      listLoading: false,
      dataForm: {
          id: undefined,
          name: undefined,
          beginDate: undefined,
          endDate: undefined,
          expireFlag: false,
          linkPicUrl: undefined,
          linkWebUrl: "/pages/promotion/promotion?id=",
          sortOrder: undefined,

      },
        huodongDetailPicLink:[],
        huodongDetailGoodsList:[]
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    if (this.$route.query.id == null) {
        return
    }
    this.mainId=this.$route.query.id;
    this.getHuodongsetMainAndDetail();
  },


  methods: {
      getHuodongsetMainAndDetail(){
          if(this.mainId!=null){
              this.listLoading = true
              readHuodongset(this.mainId).then(
                  (response)=>{
                      this.dataForm=response.data.data.huodongMain
                      this.huodongDetailPicLink=response.data.data.huodongDetailPicLink
                      this.huodongDetailGoodsList=response.data.data.huodongDetailGoodsList
                      this.listLoading = false
                  }
              ).catch(() => {
                  this.huodongPicGoodsList = []
                  this.huodongDisGoodsList=[]
                  this.listLoading = false
              })
          }
      },
  }
}

</script>
