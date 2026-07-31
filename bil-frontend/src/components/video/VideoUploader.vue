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
import { preUploadVideoApi, delUploadVideoApi } from '@/api/modules/file'

const emit = defineEmits(['uploaded', 'removed'])

const CHUNK_SIZE = 10 * 1024 * 1024   // 10MB per chunk
const MAX_CONCURRENT = 2               // parallel chunk uploads
const MAX_RETRIES = 3
const RETRY_DELAY_MS = 2000

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

const MAX_VIDEO_SIZE = 2 * 1024 * 1024 * 1024 // 2GB

async function uploadChunk(blob, index, chunks, uploadId) {
  let lastError = null
  for (let attempt = 0; attempt < MAX_RETRIES; attempt++) {
    try {
      const formData = new FormData()
      formData.append('chunkFile', blob, 'chunk_' + index)
      formData.append('chunkIndex', String(index))
      formData.append('uploadId', uploadId)
      await request.post('/file/uploadVideo', formData, { timeout: 240000, silent: true })
      console.log(`[Upload] Chunk ${index + 1}/${chunks} OK` + (attempt > 0 ? ` (retry ${attempt})` : ''))
      return
    } catch (e) {
      lastError = e
      console.warn(`[Upload] Chunk ${index + 1}/${chunks} attempt ${attempt + 1}/${MAX_RETRIES} failed:`, e.message || e)
      if (attempt < MAX_RETRIES - 1) {
        await new Promise(resolve => setTimeout(resolve, RETRY_DELAY_MS))
      }
    }
  }
  throw new Error(`分片 ${index + 1}/${chunks} 上传失败（已重试${MAX_RETRIES}次）: ` + (lastError?.message || '连接被拒绝'))
}

async function handleFileChange(file) {
  if (uploading.value) return

  const raw = file.raw
  if (!raw) return

  if (raw.size > MAX_VIDEO_SIZE) {
    ElMessage.error('视频文件不能超过 2GB')
    uploadRef.value?.clearFiles()
    return
  }

  fileName.value = raw.name
  fileSize.value = raw.size
  uploadError.value = ''
  uploading.value = true
  progress.value = 0

  let uploadId = ''
  try {
    const chunks = Math.ceil(raw.size / CHUNK_SIZE)
    console.log(`[Upload] Starting: ${raw.name}, ${formatSize(raw.size)}, ${chunks} chunks, ${MAX_CONCURRENT} concurrent`)
    const { fileId, uploadId: preUploadId } = await preUploadVideoApi({
      fileName: raw.name,
      chunks: String(chunks)
    })
    uploadId = preUploadId
    console.log(`[Upload] PreUpload OK, uploadId=${uploadId}, fileId=${fileId}`)

    let completed = 0
    const tasks = []

    for (let i = 0; i < chunks; i++) {
      const start = i * CHUNK_SIZE
      const end = Math.min(start + CHUNK_SIZE, raw.size)
      const blob = raw.slice(start, end)

      const task = uploadChunk(blob, i, chunks, uploadId).then(() => {
        completed++
        progress.value = Math.round((completed / chunks) * 100)
      })

      tasks.push(task)

      // Send up to MAX_CONCURRENT in parallel, then await one before adding next
      if (tasks.length >= MAX_CONCURRENT) {
        await Promise.race(tasks.splice(0, MAX_CONCURRENT))
      }
    }

    // Wait for remaining tasks
    await Promise.all(tasks)

    await request.post('/file/completeUpload', { uploadId, fileId }, { timeout: 600000, silent: true })

    uploadedFileId.value = fileId
    ElMessage.success('视频上传完成，正在转码...')
    emit('uploaded', fileId)
    ignoreRemove = true
    uploadRef.value?.clearFiles()
    ignoreRemove = false
  } catch (e) {
    const msg = e?.message || e?.response?.data?.info || '上传失败'
    uploadError.value = msg
    ElMessage.error(msg)
    console.error('[Upload] Failed:', msg)
    // 清理服务端残留分片（忽略清理失败）
    if (uploadId) {
      try {
        await delUploadVideoApi({ uploadId })
      } catch { /* 忽略 */ }
    }
    ignoreRemove = true
    uploadRef.value?.clearFiles()
    ignoreRemove = false
    // 复位 UI 状态，允许直接重新上传
    fileName.value = ''
    fileSize.value = 0
    progress.value = 0
    uploadedFileId.value = ''
    emit('removed')
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

function handleExceed() {
  // limit=1 已满（如上次文件残留）：清空后允许重新选择
  uploadRef.value?.clearFiles()
  handleRemove()
  ElMessage.warning('已清空旧文件，请重新选择视频文件')
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