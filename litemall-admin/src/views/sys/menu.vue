<template>
  <div class="app-container">
    <el-container>

      <el-aside style="width: 300px">
        <el-card >
          <h3 class="box-title" slot="header" style="width: 100%;">
            <el-button type="primary" size="mini"   @click="batchReadMenu">自动读取</el-button>
            <el-button type="primary" size="mini"   @click="load">刷新菜单</el-button>
          </h3>
          <h3 class="box-title" slot="header" style="width: 100%;">
            <el-button type="primary" size="mini"   @click="newAdd">新增菜单</el-button>
            <el-button type="danger" size="mini"   @click="batchDelete">批量删除</el-button>
          </h3>
          <el-tree v-if="menuTree"
                   ref="menuTree"
                   :data="menuTree"
                   show-checkbox
                   :check-strictly=false
                   highlight-current
                   :render-content="renderContent"
                   @node-click="handleNodeClick" clearable node-key="id" :props="defaultProps"></el-tree>
        </el-card>
      </el-aside>
      <el-main style="margin: 0px;padding: 0px;margin-left: 20px">

          <el-form :model="form" ref="form" label-width="150px">
            <el-col :span="12" >
              <el-form-item label="上级节点" :label-width="formLabelWidth">
                <!--<el-input v-model="form.parentId" auto-complete="off"></el-input>-->
                <el-cascader class="filter-item" clearable ref="belongClass" :props="defaultCascadeProps"  style="width: 312px;"
                             :options="menuTree"
                             v-model="form.parentId" expand-trigger="hover" @change="handleParentNodeChange"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="代码" :label-width="formLabelWidth">
                <el-input v-model="form.code" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="名称" :label-width="formLabelWidth">
                <el-input v-model="form.name" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="链接" :label-width="formLabelWidth">
                <el-input v-model="form.href" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="路径" :label-width="formLabelWidth">
                <el-input v-model="form.path" disabled auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="是否显示" :label-width="formLabelWidth">
                <el-radio-group style="width: 200px" v-model="form.isShow">
                  <el-radio :label="true">显示</el-radio>
                  <el-radio :label="false">不显示</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="图标" :label-width="formLabelWidth">
                <i :class="form.icon" v-model="form.icon"></i>
                <el-button type="text" @click="selectIconDialog=true">选择</el-button>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="排序" :label-width="formLabelWidth">
                <el-input v-model="form.sort" auto-complete="off"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
            <el-form-item label="备注" :label-width="formLabelWidth">
              <el-input v-model="form.remarks" auto-complete="off"></el-input>
            </el-form-item>
            </el-col>
            <el-col :span="24" >
                <hr />
            </el-col>

            <el-col :span="24" >
            <el-form-item label="" :label-width="formLabelWidth">
              <el-button type="primary" size="medium"  @click="onSubmit" v-text="form.id?'修改':'新增'"></el-button>
              <el-button type="danger" size="medium"   @click="deleteSelected" icon="delete" v-show="form.id && form.id!=null">删除
              </el-button>
            </el-form-item>
            </el-col>
          </el-form>

<!--        </el-card>-->
      </el-main>
    </el-container>

      <el-dialog title="选择图标" :visible.sync="selectIconDialog"  :close-on-click-modal="false"  size="tiny">
        <div class="select-tree">
          <el-scrollbar
            tag="div"
            class='is-empty'
            wrap-class="el-select-dropdown__wrap"
            view-class="el-select-dropdown__list">

            <div class="icons-wrapper">
              <i class="el-icon-info" @click="selectIcon($event)"></i>
              <i class="el-icon-error" @click="selectIcon($event)"></i>
              <i class="el-icon-success" @click="selectIcon($event)"></i>
              <i class="el-icon-warning"@click="selectIcon($event)"></i>
              <i class="el-icon-question"@click="selectIcon($event)"></i>
              <i class="el-icon-back"@click="selectIcon($event)"></i>
              <i class="el-icon-arrow-left"@click="selectIcon($event)"></i>
              <i class="el-icon-arrow-down"@click="selectIcon($event)"></i>
              <i class="el-icon-arrow-right"@click="selectIcon($event)"></i>
              <i class="el-icon-arrow-up"@click="selectIcon($event)"></i>
              <i class="el-icon-caret-left"@click="selectIcon($event)"></i>
              <i class="el-icon-caret-bottom"@click="selectIcon($event)"></i>
              <i class="el-icon-caret-top"@click="selectIcon($event)"></i>
              <i class="el-icon-caret-right"@click="selectIcon($event)"></i>
              <i class="el-icon-d-arrow-left"@click="selectIcon($event)"></i>
              <i class="el-icon-d-arrow-right"@click="selectIcon($event)"></i>
              <i class="el-icon-minus"@click="selectIcon($event)"></i>
              <i class="el-icon-plus"@click="selectIcon($event)"></i>
              <i class="el-icon-remove"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-plus"@click="selectIcon($event)"></i>
              <i class="el-icon-remove-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-plus-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-close"@click="selectIcon($event)"></i>
              <i class="el-icon-check"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-close"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-check"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-close-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-circle-check-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-zoom-out"@click="selectIcon($event)"></i>
              <i class="el-icon-zoom-in"@click="selectIcon($event)"></i>
              <i class="el-icon-d-caret"@click="selectIcon($event)"></i>
              <i class="el-icon-sort"@click="selectIcon($event)"></i>
              <i class="el-icon-sort-down"@click="selectIcon($event)"></i>
              <i class="el-icon-sort-up"@click="selectIcon($event)"></i>
              <i class="el-icon-tickets"@click="selectIcon($event)"></i>
              <i class="el-icon-document"@click="selectIcon($event)"></i>
              <i class="el-icon-goods"@click="selectIcon($event)"></i>
              <i class="el-icon-sold-out"@click="selectIcon($event)"></i>
              <i class="el-icon-news"@click="selectIcon($event)"></i>
              <i class="el-icon-message"@click="selectIcon($event)"></i>
              <i class="el-icon-date"@click="selectIcon($event)"></i>
              <i class="el-icon-printer"@click="selectIcon($event)"></i>
              <i class="el-icon-time"@click="selectIcon($event)"></i>
              <i class="el-icon-bell"@click="selectIcon($event)"></i>
              <i class="el-icon-mobile-phone"@click="selectIcon($event)"></i>
              <i class="el-icon-"@click="selectIcon($event)"></i>
              <i class="el-icon-view"@click="selectIcon($event)"></i>
              <i class="el-icon-menu"@click="selectIcon($event)"></i>
              <i class="el-icon-more"@click="selectIcon($event)"></i>
              <i class="el-icon-more-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-star-on"@click="selectIcon($event)"></i>
              <i class="el-icon-star-off"@click="selectIcon($event)"></i>
              <i class="el-icon-location"@click="selectIcon($event)"></i>
              <i class="el-icon-location-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-phone"@click="selectIcon($event)"></i>
              <i class="el-icon-phone-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-picture"@click="selectIcon($event)"></i>
              <i class="el-icon-picture-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-delete"@click="selectIcon($event)"></i>
              <i class="el-icon-search"@click="selectIcon($event)"></i>
              <i class="el-icon-edit"@click="selectIcon($event)"></i>
              <i class="el-icon-edit-outline"@click="selectIcon($event)"></i>
              <i class="el-icon-rank"@click="selectIcon($event)"></i>
              <i class="el-icon-refresh"@click="selectIcon($event)"></i>
              <i class="el-icon-share"@click="selectIcon($event)"></i>
              <i class="el-icon-setting"@click="selectIcon($event)"></i>
              <i class="el-icon-upload"@click="selectIcon($event)"></i>
              <i class="el-icon-upload2"@click="selectIcon($event)"></i>
              <i class="el-icon-download"@click="selectIcon($event)"></i>
              <i class="el-icon-loading"@click="selectIcon($event)"></i>
              <i class="el-icon--right"@click="selectIcon($event)"></i>
              <i class="el-icon--left"@click="selectIcon($event)"></i>
            </div>
          </el-scrollbar>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="selectIconDialog = false">取 消</el-button>
          <el-button type="primary" @click="selectIconDialog = false">确 定</el-button>
          </span>
      </el-dialog>

  </div>
</template>
<script type="text/babel">
  import merge from 'element-ui/src/utils/merge';
  import { listMenu, createMenu, updateMenu, deleteMenu,deleteMenulist,readMenu,batchAddMenu} from '@/api/menu'

  import { asyncRouterMap } from '@/router'

  export default {
    components: {

    },
    data(){
      return {
        selectIconDialog: false,
        formLabelWidth: '80px',
        defaultProps: {
          children: 'children',
          label: 'name',
          id: "id",
        },
        defaultCascadeProps: {
            children: 'children',
            label: 'name',
            value: "id",
            checkStrictly: true
        },
        menuTree: [],
        form: {
          id: null,
          code:'',
          name: '',
          sort: null,
          icon: '',
          href: '',
          path: '',
          isShow: true,
          parentId: 0,
          remarks: ''
        },


        asyncRouterMap:asyncRouterMap,//一级菜单 导航栏菜单
      }
    },
    created(){
      this.load();

    },
    methods: {
       async batchReadMenu(){
        if(confirm("批量读取菜单将会重新配置菜单，原有的一些配置将会失效，确认吗？")==false){
          return;
        }
        //从路由中批量读取菜单信息
        //读取一级菜单
        //系统目前支持三级菜单
         let indexFirst=10
         this.asyncRouterMap.forEach(
           (item)=>{
             if(item.path!="\'*\'"){
               let postData={
                 id: null,
                 code:item.name, //默认code和name一致
                 name:item.meta.title,
                 sort: indexFirst,
                 icon: item.meta.icon,
                 href: item.path,
                 path: item.path,
                 isShow: item.hidden==null||item.hidden==false?true:false,
                 parentId: 0,
                 level:1,
                 remarks: ''
               };
               batchAddMenu(postData).then(firstLevelMenu => {
                 this.load();
                 let menuFirst=firstLevelMenu.data.data;
                 let menuSecondJson=[];
                 //填加二级菜单
                 if(item.children!=null&&item.children.length>0) {
                   menuSecondJson = item.children;
                 }
                 if(menuSecondJson.length<=0){
                   return;
                 }
                 let indexSecond=10
                 menuSecondJson.forEach((secondLevelMenu)=>{
                   let secondMenuData={
                     id: null,
                     code:secondLevelMenu.name, //默认code和name一致
                     name:secondLevelMenu.meta.title,
                     sort: indexSecond,
                     icon: secondLevelMenu.meta.icon,
                     href: secondLevelMenu.path,
                     path: item.path+secondLevelMenu.path,
                     isShow: secondLevelMenu.hidden==null||secondLevelMenu.hidden==false?true:false,
                     parentId: menuFirst.id,
                     level:2,
                     remarks: ''
                   };
                   batchAddMenu(secondMenuData).then(dbSecondMenu => {
                     this.load();
                     let menuSecond = dbSecondMenu.data.data;
                     //填加三级菜单
                     if(secondLevelMenu.children!=null&&secondLevelMenu.children.length>0){
                       let menuThirdJson = secondLevelMenu.children;
                       let indexThird=10
                       menuThirdJson.forEach(
                         (thirdLevelMenu)=>{
                           let thirdMenuData={
                             id: null,
                             code:thirdLevelMenu.name, //默认code和name一致
                             name:thirdLevelMenu.meta.title,
                             sort: indexThird,
                             icon: thirdLevelMenu.meta.icon,
                             href: thirdLevelMenu.path,
                             path: secondLevelMenu.path+'/'+thirdLevelMenu.path,
                             isShow: thirdLevelMenu.hidden==null||thirdLevelMenu.hidden==false?true:false,
                             parentId: menuSecond.id,
                             level:3,
                             remarks: '',
                           };
                           batchAddMenu(thirdMenuData).then(dbThirdLevelMenu => {
                             this.load();
                             let menuThird = dbThirdLevelMenu.data.data;
                             //todo 如果有四级菜单，需要在此处填加
                           })
                           indexThird=indexThird+10
                         }
                       )
                     }
                   })
                   indexSecond=indexSecond+10
                 })

               })
                 .catch(response => {
                   this.$message.error( '失败:'+response.data.errmsg);
                 })
               indexFirst=indexFirst+10;
             }

           }
         );
      },

      handleParentNodeChange(value) {
          if(value==null||value.length==0){
              this.form.parentId =undefined
          }
          else
          {
              this.form.parentId = value[value.length - 1]
          }
      },
      selectIcon(event){
        this.form.icon = event.target.className;
        this.selectIconDialog = false;
      },
      renderContent(h, { node, data, store }) {
        return (
          <span>
            <span>
              <span><i class={data.icon}></i>&nbsp;{node.label}</span>
            </span>
          </span>);
      },
      newAdd(){
       this.resetForm();
      },
      resetForm(){
          this.form = {
              id: null,
              code:'',
              name: '',
              sort: null,
              icon: '',
              href: '',
              isShow: true,
              parentId: 0,
              remarks: ''
          };
      },
      deleteSelected(){
          if(this.form.id==null){
              this.form = {
                  id: null,
                  code:'',
                  name: '',
                  sort: null,
                  icon: '',
                  href: '',
                  isShow: true,
                  parentId: 0,
                  remarks: ''
              };
          } else
          {
              deleteMenu(this.form)
                  .then(response => {
                      this.load();
                      this.$message.success('删除成功');

                  })
                  .catch(response => {
                      this.$message.error( '失败:'+response.data.errmsg);
                  })
          }

      },
      batchDelete(){
        var checkKeys = this.$refs.menuTree.getCheckedKeys();
        if (checkKeys == null || checkKeys.length <= 0) {
          this.$message.warning('请选择要删除的菜单');
          return;
        }
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            deleteMenulist(checkKeys)
                .then(response => {
                    this.load();
                    this.$message.success('删除成功');

                })
                .catch(response => {
                    this.$message.error( '失败:'+response.data.errmsg);
                })

            // this.batchDeleteFromTree(this.menuTree, checkKeys);
          });

      },
      handleNodeClick(data){
        this.form = merge({}, data);
          readMenu(this.form.id).then(res=>{
              this.form=res.data.data
          })
      },
      onSubmit(){
        if (this.form.id == null) {
            createMenu(this.form)
                .then(response => {
                    this.load();
                    this.$message.success('创建成功');
                    this.newAdd();
                })
                .catch(response => {
                    this.$message.error( '失败:'+response.data.errmsg);
                })
        } else
        {
            updateMenu(this.form)
                .then(response => {
                    this.load();
                    this.$message.success('修改成功');

                })
                .catch(response => {
                    this.$message.error( '失败:'+response.data.errmsg);
                })
        }
      },
      load(){
        listMenu().then(response => {
            this.menuTree = response.data.data.list
        })
      }
    },
  }
</script>

<style>
  .select-tree .icons-wrapper {
    display: block;
  }

  .select-tree .is-empty i {
    width: 30px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    display: inline-block;
    cursor: pointer;
  }

  .select-tree .is-empty i:hover {
    /*background-color: #0d6aad;*/
    /*color: #ffffff;*/
  }

</style>
