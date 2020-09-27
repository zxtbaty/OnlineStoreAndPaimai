<template>
  <div class="app-container">
    <user-charge-lock-list :inUnlockIsNull="true"></user-charge-lock-list>

  </div>

</template>
<style scoped>

</style>
<script>

  import {default as UserChargeLockList} from '@/components/Order/UserChargeLockList'

  export default {
    name: 'baozhengjin',
    components: {UserChargeLockList},
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
