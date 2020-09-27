<template>
  <div class="app-container">

    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" row-key="id" style="width: 100%;margin-bottom: 20px;" border="">


      <el-table-column label="区域名称" prop="name">
      <template slot-scope="scope">
        <!--{{scope.row.name}}-->
        <slot v-if="scope.row.type<2">
          {{scope.row.name}}
        </slot>
        <slot v-else-if="scope.row.type=3">
          <a style="color:blue" @click="getArea(scope.row)">{{scope.row.name}}</a>
        </slot>

      </template>
      </el-table-column>
      <el-table-column label="区域类型" prop="type">
        <template slot-scope="scope">
          {{ scope.row.type | typeFilter }}
        </template>
      </el-table-column>

      <el-table-column label="区域编码" prop="code"/>

    </el-table>

  </div>
</template>

<script>
import { listRegion,listSubRegion } from '@/api/region'

export default {
  name: 'Region',
  filters: {
    typeFilter(status) {
      const typeMap = {
        '1': '省',
        '2': '市',
        '3': '区'
      }
      return typeMap[status]
    }
  },
  data() {
    return {
      list: [],
      listChild:[],
      listLoading: true,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listRegion().then(response => {
        this.list = response.data.data.list
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.listLoading = false
      })
    },
    getArea(row){
      this.listLoading = true
      listSubRegion({id:row.id}).then(response => {
        this.list = response.data.data.list
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.listLoading = false
      })


    },
        /**
         * 将id、parentId这种JSON数组的数据格式转换为树节点格式
         * @param {Array} arr
         * @param {String} id
         * @param {String} pid
         * @return {Array}
         */
     arrayToTree(arr, id, pid) {
      let data = JSON.parse(JSON.stringify(arr));
      if (!data || !data.length) return [];
      let targetData = [];                    //存储数据的容器(返回)
      let records = {};
      let itemLength = data.length;           //数据集合的个数
      for (let i = 0; i < itemLength; i++){
        let o = data[i];
        records[o[id]] = o;
      }
      for (let i = 0; i < itemLength; i++) {
        let currentData = data[i];
        let parentData = records[currentData[pid]];
        if (!parentData) {
          targetData.push(currentData);
          continue;
        }
        parentData.children = parentData.children || [];
        parentData.children.push(currentData);
      }
      return targetData;
    },
    /**
     * 将数组转换为树结构数据
     *
     * @export
     * @param {Array} array
     * @param {Object} parent 父节点
     * @param {Array} tree
     * @return {Array}
     */
    array2tree(array, parent = { id: 0 }, tree = []) {
    let treeData = tree
    const children = _.filter(array, function(child) {
      return child.parentId === parent.id
    })
    if (!_.isEmpty(children)) {
      if (parent.id === 0) {
        treeData = children
      } else {
        parent['children'] = children
      }
      _.each(children, function(child) {
        array2tree(array, child)
      })
    }

  return treeData
}
  }
}
</script>
