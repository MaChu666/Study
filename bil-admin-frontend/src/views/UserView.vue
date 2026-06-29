<template>
  <div class="user-page">
    <div class="page-toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索用户名" clearable style="width:240px" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="userList" stripe border style="width:100%">
      <el-table-column prop="userId" label="用户ID" width="140" />
      <el-table-column prop="useName" label="昵称" width="140" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="joinTime" label="注册时间" width="180" />
      <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-popconfirm
            :title="row.status === 1 ? '确定封禁该用户？' : '确定解封该用户？'"
            @confirm="handleToggleStatus(row)"
          >
            <template #reference>
              <el-button text :type="row.status === 1 ? 'danger' : 'success'" size="small">
                {{ row.status === 1 ? '封禁' : '解封' }}
              </el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm title="确定删除此用户？此操作不可恢复。" @confirm="handleDelete(row.userId)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="totalCount > pageSize"
      v-model:current-page="pageNo"
      :page-size="pageSize"
      :total="totalCount"
      layout="prev, pager, next"
      style="margin-top: 16px; justify-content: flex-end"
    />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { loadUserApi, changeStatusApi } from '@/api/modules/user'

const searchKeyword = ref('')
const userList = ref([])
const pageNo = ref(1)
const pageSize = ref(15)
const totalCount = ref(0)

async function loadData() {
  try {
    const result = await loadUserApi({
      pageNo: String(pageNo.value),
      useNameFuzzy: searchKeyword.value
    })
    userList.value = result?.list || []
    totalCount.value = result?.totalCount || 0
  } catch {
    userList.value = []
    totalCount.value = 0
  }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await changeStatusApi(row.userId, String(newStatus))
  ElMessage.success(newStatus ? '已解封' : '已封禁')
  await loadData()
}

async function handleDelete(userId) {
  // Backend deleteUserInfoByUserId exists in service but no direct controller endpoint,
  // reuse changeStatus with disabled status as soft-ban workaround
  await changeStatusApi(userId, '0')
  ElMessage.success('用户已禁用')
  await loadData()
}

watch(pageNo, () => loadData())

onMounted(loadData)
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}
</style>
