<template>
  <div class="app-container">
    <user-charge-list inUserId ></user-charge-list>

  </div>

</template>
<style scoped>

</style>
<script>

  import {default as UserChargeList} from '@/components/Order/UserChargeList'
export default {

  name: 'ChargeMoney',
  components: {UserChargeList},



  watch:{

  },

  data() {
    return {

    }
  },


  created() {
  },
  methods: {


  },

}
</script>
