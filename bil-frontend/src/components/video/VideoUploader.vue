<template>
  <div class="video-uploader">
    <el-upload
      ref="uploadRef"
      :auto-upload="false"
      :limit="1"
      accept="video/*"
      :on-change="handleFileChange"
      :on-remove="handleRemove"
    >
      <template #trigger>
        <el-button type="primary" :disabled="uploading">
          {{ uploading ? '上传中...' : '选择视频文件' }}
        </el-button>
      </template>
    </el-upload>

    <div v-if="fileName" class="upload-info">
      <span>{{ fileName }}</span>
      <span class="file-size">({{ formatSize(fileSize) }})</span>
    </div>

    <el-progress
      v-if="uploading"
      :percentage="progress"
      :status="progress === 100 ? 'success' : ''"
      style="margin-top: 12px"
    />

    <p v-if="uploadError" class="upload-error">{{ uploadError }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { preUploadVideoApi } from '@/api/modules/file'

const emit = defineEmits(['uploaded', 'removed'])

const CHUNK_SIZE = 5 * 1024 * 1024 // 5MB per chunk

const uploadRef = ref(null)
const uploading = ref(false)
const progress = ref(0)
const fileName = ref('')
const fileSize = ref(0)
const uploadError = ref('')
const uploadedFileId = ref('')
let ignoreRemove = false

function formatSize(bytes) {
  if (bytes >= 1073741824) return (bytes / 1073741824).toFixed(1) + ' GB'
  if (bytes >= 1048576) return (bytes / 1048576).toFixed(1) + ' MB'
  if (bytes >= 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return bytes + ' B'
}

async function handleFileChange(file) {
  const raw = file.raw
  if (!raw) return

  fileName.value = raw.name
  fileSize.value = raw.size
  uploadError.value = ''
  uploading.value = true
  progress.value = 0

  try {
    const chunks = Math.ceil(raw.size / CHUNK_SIZE)
    const { fileId, uploadId } = await preUploadVideoApi({
      fileName: raw.name,
      chunks: String(chunks)
    })

    for (let i = 0; i < chunks; i++) {
      const start = i * CHUNK_SIZE
      const end = Math.min(start + CHUNK_SIZE, raw.size)
      const blob = raw.slice(start, end)

      const formData = new FormData()
      formData.append('chunkFile', blob, raw.name)
      formData.append('chunkIndex', String(i))
      formData.append('uploadId', uploadId)
      await request.post('/file/uploadVideo', formData)

      progress.value = Math.round(((i + 1) / chunks) * 100)
    }

    uploadedFileId.value = fileId
    ElMessage.success('视频上传完成')
    emit('uploaded', fileId)
    ignoreRemove = true
    uploadRef.value?.clearFiles()
    ignoreRemove = false
  } catch (e) {
    const msg = e?.message || e?.response?.data?.info || '上传失败'
    uploadError.value = msg
    ElMessage.error(msg)
    ignoreRemove = true
    uploadRef.value?.clearFiles()
    ignoreRemove = false
  } finally {
    uploading.value = false
  }
}

function handleRemove() {
  fileName.value = ''
  fileSize.value = 0
  progress.value = 0
  uploadError.value = ''
  uploadedFileId.value = ''
  if (!ignoreRemove) {
    emit('removed')
  }
}

function reset() {
  uploadRef.value?.clearFiles()
  handleRemove()
}

defineExpose({ reset })
</script>

<style scoped>
.video-uploader {
  padding: 12px 0;
}

.upload-info {
  margin-top: 8px;
  font-size: 14px;
  color: var(--bil-text, #333);
}

.file-size {
  color: var(--bil-muted, #999);
  margin-left: 8px;
}

.upload-error {
  color: #f56c6c;
  font-size: 13px;
  margin: 8px 0 0;
}
</style>
