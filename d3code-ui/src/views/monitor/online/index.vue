<template>
  <div class="p-2">
    <div class="mb-[10px]">
      <el-card shadow="hover">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="登录地址" prop="ipaddr">
            <el-input v-model="queryParams.ipaddr" placeholder="请输入登录地址" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="用户名称" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <el-card shadow="hover">
      <el-table
        v-loading="loading"
        border
        :data="onlineList.slice((queryParams.pageNum - 1) * queryParams.pageSize, queryParams.pageNum * queryParams.pageSize)"
        style="width: 100%"
      >
        <el-table-column label="序号" width="50" type="index" align="center">
          <template #default="scope">
            <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="会话编号" align="center" prop="tokenId" :show-overflow-tooltip="true" />
        <el-table-column label="登录名称" align="center" prop="userName" :show-overflow-tooltip="true" />
        <el-table-column label="客户端" align="center" prop="clientKey" :show-overflow-tooltip="true" />
        <el-table-column label="设备类型" align="center">
          <template #default="scope">
            <dict-tag :options="sys_device_type" :value="scope.row.deviceType" />
          </template>
        </el-table-column>
        <el-table-column label="所属部门" align="center" prop="deptName" :show-overflow-tooltip="true" />
        <el-table-column label="主机" align="center" prop="ipaddr" :show-overflow-tooltip="true" />
        <el-table-column label="登录地点" align="center" prop="loginLocation" :show-overflow-tooltip="true" />
        <el-table-column label="操作系统" align="center" prop="os" :show-overflow-tooltip="true" />
        <el-table-column label="浏览器" align="center" prop="browser" :show-overflow-tooltip="true" />
        <el-table-column label="登录时间" align="center" prop="loginTime" width="180">
          <template #default="scope">
            <span>{{ proxy.parseTime(scope.row.loginTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="强退" placement="top">
              <el-button v-hasPermi="['monitor:online:forceLogout']" link type="primary" icon="Delete" @click="handleForceLogout(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" />
    </el-card>
  </div>
</template>

<script setup name="Online" lang="ts">
import { forceLogout, list as initData } from '@/api/monitor/online'
import { OnlineQuery, OnlineVO } from '@/api/monitor/online/types'
import { to } from 'await-to-js'

const { proxy } = getCurrentInstance() as ComponentInternalInstance
const { sys_device_type } = toRefs<any>(proxy?.useDict('sys_device_type'))

const onlineList = ref<OnlineVO[]>([])
const loading = ref(true)
const total = ref(0)

const queryFormRef = ref<ElFormInstance>()

const queryParams = ref<OnlineQuery>({
  pageNum: 1,
  pageSize: 10,
  ipaddr: '',
  userName: '',
})

/** 查询登录日志列表 */
const getList = async () => {
  loading.value = true
  const res = await initData(queryParams.value)
  onlineList.value = res.rows
  total.value = res.total
  loading.value = false
}
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}
/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}
/** 强退按钮操作 */
const handleForceLogout = async (row: OnlineVO) => {
  const [err] = await to(proxy?.$modal.confirm('是否确认强退名称为"' + row.userName + '"的用户?') as any)
  if (!err) {
    await forceLogout(row.tokenId)
    await getList()
    proxy?.$modal.msgSuccess('删除成功')
  }
}

onMounted(() => {
  getList()
})
</script>
