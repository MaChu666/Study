<template>
  <div class="theme-page">
    <el-card>
      <template #header>主题管理</template>
      <div class="toolbar">
        <el-button type="primary" @click="showAddDialog">新增主题</el-button>
      </div>
      <el-table :data="themes" border stripe style="width: 100%">
        <el-table-column prop="themeId" label="ID" width="60" />
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <div class="preview-swatch" :style="{ background: row.gradient }" />
          </template>
        </el-table-column>
        <el-table-column prop="themeName" label="主题名称" />
        <el-table-column prop="gradient" label="渐变色值" min-width="200" />
        <el-table-column prop="primaryColor" label="主色调" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.primaryColor }">{{ row.primaryColor }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-position="left" label-width="100px">
        <el-form-item label="主题名称">
          <el-input v-model="form.themeName" placeholder="如：星河紫" />
        </el-form-item>
        <el-form-item label="渐变色">
          <el-input v-model="form.gradient" placeholder="如：linear-gradient(135deg, #667eea, #764ba2)" />
        </el-form-item>
        <el-form-item label="主色调">
          <el-input v-model="form.primaryColor" placeholder="如：#667eea" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width:160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          <span class="switch-hint">{{ form.status === 1 ? '启用' : '禁用' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loadAllThemesApi, addThemeApi, updateThemeApi, deleteThemeApi } from '@/api/modules/theme'

const themes = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增主题')
const saving = ref(false)
const editingId = ref(null)

const form = reactive({
  themeName: '',
  gradient: '',
  primaryColor: '',
  sort: 0,
  status: 1
})

async function loadData() {
  try {
    const data = await loadAllThemesApi()
    themes.value = data || []
  } catch {
    ElMessage.error('加载主题列表失败')
  }
}

function showAddDialog() {
  editingId.value = null
  dialogTitle.value = '新增主题'
  form.themeName = ''
  form.gradient = ''
  form.primaryColor = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function handleEdit(row) {
  editingId.value = row.themeId
  dialogTitle.value = '编辑主题'
  form.themeName = row.themeName
  form.gradient = row.gradient
  form.primaryColor = row.primaryColor
  form.sort = row.sort
  form.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateThemeApi({ themeId: editingId.value, ...form })
      ElMessage.success('主题已更新')
    } else {
      await addThemeApi({ ...form })
      ElMessage.success('主题已添加')
    }
    dialogVisible.value = false
    await loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该主题吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteThemeApi({ themeId: row.themeId })
    ElMessage.success('主题已删除')
    await loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.theme-page {
  max-width: 900px;
}
.toolbar {
  margin-bottom: 16px;
}
.preview-swatch {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid var(--admin-border);
}
.switch-hint {
  margin-left: 10px;
  font-size: 13px;
  color: var(--admin-text-secondary);
}
</style>
