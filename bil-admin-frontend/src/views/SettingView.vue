<template>
  <div class="setting-page">
    <el-card>
      <template #header>系统设置</template>
      <el-form :model="form" label-position="left" label-width="140px">
        <el-form-item label="系统名称">
          <el-input v-model="form.sysName" placeholder="BilBil" />
        </el-form-item>
        <el-form-item label="最大文件大小 (B)">
          <el-input-number v-model="form.maxFileSize" :min="0" style="width:200px" />
        </el-form-item>
        <el-form-item label="最大分片大小 (B)">
          <el-input-number v-model="form.maxChunkSize" :min="0" style="width:200px" />
        </el-form-item>
        <el-divider />
        <el-form-item label="视频审核">
          <el-switch v-model="form.videoAudit" :active-value="1" :inactive-value="0" />
          <span class="switch-hint">{{ form.videoAudit ? '新视频需审核' : '新视频免审核' }}</span>
        </el-form-item>
        <el-form-item label="评论功能">
          <el-switch v-model="form.commentOpen" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="弹幕功能">
          <el-switch v-model="form.danmuOpen" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="开放注册">
          <el-switch v-model="form.registerOpen" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettingApi, saveSettingApi } from '@/api/modules/setting'

const saving = ref(false)
const form = reactive({
  sysName: '',
  maxFileSize: 0,
  maxChunkSize: 0,
  commentOpen: 1,
  danmuOpen: 1,
  videoAudit: 1,
  registerOpen: 1
})

async function loadData() {
  try {
    const data = await getSettingApi()
    if (data) {
      Object.assign(form, data)
    }
  } catch {
    // keep defaults
  }
}

async function handleSave() {
  saving.value = true
  try {
    await saveSettingApi({ ...form })
    ElMessage.success('设置已保存')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.setting-page {
  max-width: 640px;
}
.switch-hint {
  margin-left: 10px;
  font-size: 13px;
  color: var(--admin-text-secondary);
}
</style>
