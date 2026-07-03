<template>
  <div class="category-page">
    <div class="page-toolbar">
      <el-button type="primary" @click="openAdd(null)">新增分区</el-button>
    </div>

    <el-table :data="categories" stripe border style="width:100%">
      <el-table-column prop="categoryId" label="ID" width="80" />
      <el-table-column prop="categoryCode" label="编码" width="120" />
      <el-table-column prop="categoryName" label="名称" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openAdd(row)">添加子分区</el-button>
          <el-button text size="small" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除此分区？" @confirm="handleDelete(row.categoryId)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" label-position="top">
        <el-form-item label="分区编码">
          <el-input v-model="form.categoryCode" placeholder="例: game" />
        </el-form-item>
        <el-form-item label="分区名称">
          <el-input v-model="form.categoryName" placeholder="例: 游戏" />
        </el-form-item>
        <el-form-item label="图标">
          <div class="icon-upload-row">
            <img v-if="form.icon && !iconBroken" :src="form.icon" class="icon-preview" @error="onIconError" />
            <el-button size="small" @click="showCropper = true">{{ form.icon ? '更换图标' : '上传图标' }}</el-button>
            <el-button v-if="form.icon" size="small" @click="form.icon = ''">清除</el-button>
          </div>
        </el-form-item>
        <el-form-item label="背景">
          <el-input v-model="form.background" placeholder="背景 URL 或色值" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <ImageCropperDialog v-model="showCropper" @success="onIconUploaded" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loadCategoryApi, saveCategoryApi, delCategoryApi } from '@/api/modules/category'
import ImageCropperDialog from '@/components/ImageCropperDialog.vue'

const categories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const showCropper = ref(false)
const iconBroken = ref(false)
const form = reactive({ pCategoryId: 0, categoryId: undefined, categoryCode: '', categoryName: '', icon: '', background: '' })
let editingParent = null

async function loadData() {
  try {
    const data = await loadCategoryApi(0)
    categories.value = Array.isArray(data) ? data : []
  } catch {
    categories.value = []
  }
}

function openAdd(parent) {
  editingParent = parent
  dialogTitle.value = parent ? `添加「${parent.categoryName}」的子分区` : '新增分区'
  form.pCategoryId = parent?.categoryId || 0
  form.categoryId = undefined
  form.categoryCode = ''
  form.categoryName = ''
  form.icon = ''
  form.background = ''
  iconBroken.value = false
dialogVisible.value = true
}

function openEdit(row) {
  editingParent = null
  dialogTitle.value = '编辑分区'
  form.pCategoryId = row.pCategoryId || 0
  form.categoryId = row.categoryId
  form.categoryCode = row.categoryCode || ''
  form.categoryName = row.categoryName || ''
  form.icon = row.icon || ''
  form.background = row.background || ''
  dialogVisible.value = true
}

function onIconError() {
  iconBroken.value = true
}

function onIconUploaded(url) {
  iconBroken.value = false
  form.icon = url
}

async function handleSave() {
  await saveCategoryApi({ ...form })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await loadData()
}

async function handleDelete(categoryId) {
  await delCategoryApi(String(categoryId))
  ElMessage.success('已删除')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}
.icon-upload-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.icon-preview {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 50%;
  border: 1px solid var(--el-border-color, #dcdfe6);
}
</style>
