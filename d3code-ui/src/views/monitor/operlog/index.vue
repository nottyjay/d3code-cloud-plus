<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="操作地址" prop="operIp">
              <el-input v-model="queryParams.operIp" placeholder="请输入操作地址" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="系统模块" prop="title">
              <el-input v-model="queryParams.title" placeholder="请输入系统模块" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="操作人员" prop="operName">
              <el-input v-model="queryParams.operName" placeholder="请输入操作人员" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="类型" prop="businessType">
              <el-select v-model="queryParams.businessType" placeholder="操作类型" clearable>
                <el-option v-for="dict in sys_oper_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="操作状态" clearable>
                <el-option v-for="dict in sys_common_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作时间" style="width: 308px">
              <el-date-picker
                v-model="queryParams.operTime"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="daterange"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
                class="!w-220px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:operlog:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:operlog:remove']" type="danger" plain icon="WarnTriangleFilled" @click="handleClean">清空</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['monitor:operlog:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table
        ref="operLogTableRef"
        v-loading="loading"
        :data="operlogList"
        border
        :default-sort="defaultSort"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="日志编号" align="center" prop="operId" />
        <el-table-column label="系统模块" align="center" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="操作类型" align="center" prop="businessType">
          <template #default="scope">
            <dict-tag :options="sys_oper_type" :value="scope.row.businessType" />
          </template>
        </el-table-column>
        <el-table-column label="操作人员" align="center" width="110" prop="operName" :show-overflow-tooltip="true" sortable="custom" :sort-orders="['descending', 'ascending']" />
        <el-table-column label="部门" align="center" prop="deptName" width="130" :show-overflow-tooltip="true" />
        <el-table-column label="操作地址" align="center" prop="operIp" width="130" :show-overflow-tooltip="true" />
        <el-table-column label="操作状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sys_common_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作日期" align="center" prop="operTime" width="180" sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            <span>{{ proxy.parseTime(scope.row.operTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="消耗时间" align="center" prop="costTime" width="110" :show-overflow-tooltip="true" sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            <span>{{ scope.row.costTime }}毫秒</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="详细" placement="top">
              <el-button v-hasPermi="['monitor:operlog:query']" link type="primary" icon="View" @click="handleView(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 操作日志详细 -->
    <OperInfoDialog ref="operInfoDialogRef" />
  </div>
</template>

<script setup name="Operlog" lang="ts">
import { cleanOperlog, delOperlog, list } from '@/api/monitor/operlog'
import { OperLogForm, OperLogQuery, OperLogVO } from '@/api/monitor/operlog/types'
import OperInfoDialog from './oper-info-dialog.vue'

const { proxy } = getCurrentInstance() as ComponentInternalInstance
const { sys_oper_type, sys_common_status } = toRefs<any>(proxy?.useDict('sys_oper_type', 'sys_common_status'))

const operlogList = ref<OperLogVO[]>([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<Array<number | string>>([])
const multiple = ref(true)
const total = ref(0)
const defaultSort = ref<any>({ prop: 'operTime', order: 'descending' })

const operLogTableRef = ref<ElTableInstance>()
const queryFormRef = ref<ElFormInstance>()

const data = reactive<PageData<OperLogForm, OperLogQuery>>({
  form: {
    operId: undefined,
    tenantId: undefined,
    title: '',
    businessType: 0,
    businessTypes: undefined,
    method: '',
    requestMethod: '',
    operatorType: 0,
    operName: '',
    deptName: '',
    operUrl: '',
    operIp: '',
    operLocation: '',
    operParam: '',
    jsonResult: '',
    status: 0,
    errorMsg: '',
    operTime: '',
    costTime: 0,
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    operIp: '',
    title: '',
    operName: '',
    businessType: '',
    createTime: [],
    status: '',
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order,
  },
  rules: {},
})

const { queryParams } = toRefs(data)

/** 查询登录日志 */
const getList = async () => {
  loading.value = true
  const res = await list(proxy?.addDateRange(queryParams.value, dateRange.value))
  operlogList.value = res.rows
  total.value = res.total
  loading.value = false
}
/** 操作日志类型字典翻译 */
// const typeFormat = (row: OperLogForm) => {
//   return proxy?.selectDictLabel(sys_oper_type.value, row.businessType)
// }
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}
/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.value.pageNum = 1
  operLogTableRef.value?.sort(defaultSort.value.prop, defaultSort.value.order)
}
/** 多选框选中数据 */
const handleSelectionChange = (selection: OperLogVO[]) => {
  ids.value = selection.map((item) => item.operId)
  multiple.value = !selection.length
}
/** 排序触发事件 */
const handleSortChange = (column: any) => {
  queryParams.value.orderByColumn = column.prop
  queryParams.value.isAsc = column.order
  getList()
}

const operInfoDialogRef = ref<InstanceType<typeof OperInfoDialog>>()
/** 详细按钮操作 */
const handleView = (row: OperLogVO) => {
  operInfoDialogRef.value.openDialog(row)
}

/** 删除按钮操作 */
const handleDelete = async (row?: OperLogVO) => {
  const operIds = row?.operId || ids.value
  await proxy?.$modal.confirm('是否确认删除日志编号为"' + operIds + '"的数据项?')
  await delOperlog(operIds)
  await getList()
  proxy?.$modal.msgSuccess('删除成功')
}

/** 清空按钮操作 */
const handleClean = async () => {
  await proxy?.$modal.confirm('是否确认清空所有操作日志数据项?')
  await cleanOperlog()
  await getList()
  proxy?.$modal.msgSuccess('清空成功')
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'monitor/operlog/export',
    {
      ...queryParams.value,
    },
    `config_${new Date().getTime()}.xlsx`
  )
}
onMounted(() => {
  getList()
})
</script>
