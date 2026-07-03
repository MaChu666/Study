<template>
  <div class="banner-page">
    <div class="page-toolbar">
      <el-button type="primary" @click="openAdd">新增Banner</el-button>
    </div>

    <el-table :data="bannerList" stripe border style="width:100%">
      <el-table-column prop="bannerId" label="ID" width="70" />
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column label="图片" width="140">
        <template #default="{ row }">
          <img v-if="row.imageUrl" :src="row.imageUrl" class="banner-thumb" />
          <span v-else class="no-image">无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="链接类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.linkType === 1" type="primary" size="small">视频</el-tag>
          <el-tag v-else-if="row.linkType === 2" type="warning" size="small">外链</el-tag>
          <el-tag v-else type="info" size="small">无</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="linkValue" label="链接值" width="120" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除此Banner？" @confirm="handleDelete(row.bannerId)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px">
      <el-form :model="form" label-position="left" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="Banner标题（选填）" />
        </el-form-item>
        <el-form-item label="图片URL" required>
          <el-input v-model="form.imageUrl" placeholder="Banner图片地址" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-radio-group v-model="form.linkType">
            <el-radio :label="0">无链接</el-radio>
            <el-radio :label="1">跳转视频</el-radio>
            <el-radio :label="2">外部链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="链接值">
          <el-input v-model="form.linkValue" placeholder="视频ID或URL" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width:120px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.statusBool" active-text="启用" inactive-text="停用" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间（选填）"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间（选填）"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width:100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loadBannersApi, saveBannerApi, deleteBannerApi } from '@/api/modules/banner'

const bannerList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editingId = ref(null)

const form = reactive({
  title: '',
  imageUrl: '',
  linkType: 0,
  linkValue: '',
  sort: 0,
  statusBool: true,
  startTime: null,
  endTime: null
})

const formPayload = computed(() => ({
  title: form.title,
  imageUrl: form.imageUrl,
  linkType: String(form.linkType),
  linkValue: form.linkValue,
  sort: String(form.sort),
  status: form.statusBool ? '1' : '0',
  startTime: form.startTime || '',
  endTime: form.endTime || ''
}))

async function loadData() {
  try {
    const data = await loadBannersApi()
    bannerList.value = Array.isArray(data) ? data : []
  } catch {
    bannerList.value = []
  }
}

function resetForm() {
  editingId.value = null
  form.title = ''
  form.imageUrl = ''
  form.linkType = 0
  form.linkValue = ''
  form.sort = 0
  form.statusBool = true
  form.startTime = null
  form.endTime = null
}

function openAdd() {
  resetForm()
  dialogTitle.value = '新增Banner'
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.bannerId
  dialogTitle.value = '编辑Banner'
  form.title = row.title || ''
  form.imageUrl = row.imageUrl || ''
  form.linkType = row.linkType || 0
  form.linkValue = row.linkValue || ''
  form.sort = row.sort || 0
  form.statusBool = row.status === 1
  form.startTime = row.startTime || null
  form.endTime = row.endTime || null
  dialogVisible.value = true
}

async function handleSave() {
  try {
    const payload = { ...formPayload.value }
    if (editingId.value) {
      payload.bannerId = String(editingId.value)
    }
    await saveBannerApi(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadData()
  } catch {
    // error handled by interceptor
  }
}

async function handleDelete(bannerId) {
  try {
    await deleteBannerApi(bannerId)
    ElMessage.success('已删除')
    await loadData()
  } catch {
    // error handled by interceptor
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}
.banner-thumb {
  width: 120px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
}
.no-image {
  color: var(--admin-text-secondary);
  font-size: 12px;
}
</style>
