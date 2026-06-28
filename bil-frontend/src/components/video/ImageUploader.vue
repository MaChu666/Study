<template>
  <div
    class="image-uploader"
    :class="{ 'is-dragover': dragOver }"
    @dragenter.prevent="onDragEnter"
    @dragover.prevent="onDragOver"
    @dragleave.prevent="onDragLeave"
    @drop.prevent="onDrop"
    @click="triggerFileInput"
  >
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      hidden
      @change="onFileSelected"
    />

    <div v-if="!previewUrl && !uploading" class="upload-placeholder">
      <span class="upload-icon">+</span>
      <p>拖拽图片到此处，或点击选择封面</p>
      <p class="hint">支持 JPG / PNG / WebP</p>
    </div>

    <div v-else-if="uploading" class="upload-progress">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>上传中...</p>
    </div>

    <div v-else class="preview-area">
      <img :src="previewUrl" alt="封面预览" />
      <div class="preview-mask" @click.stop="removeCover">
        <span>点击更换</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Loading } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImageApi } from '@/api/modules/file'

const emit = defineEmits(['uploaded', 'removed'])

const fileInputRef = ref(null)
const dragOver = ref(false)
const uploading = ref(false)
const previewUrl = ref('')
const uploadedFileId = ref('')
let dragCounter = 0

async function processFile(file) {
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  const reader = new FileReader()
  reader.onload = async (e) => {
    previewUrl.value = e.target.result
    uploading.value = true

    try {
      const result = await uploadImageApi({
        file: e.target.result,
        createThumbnail: 'false'
      })
      uploadedFileId.value = result.fileId
      ElMessage.success('封面上传成功')
      emit('uploaded', result.fileId)
    } catch {
      previewUrl.value = ''
      ElMessage.error('封面上传失败')
    } finally {
      uploading.value = false
    }
  }
  reader.readAsDataURL(file)
  fileInputRef.value.value = ''
}

function onDragEnter() {
  dragCounter++
  dragOver.value = true
}

function onDragOver() {
  dragOver.value = true
}

function onDragLeave() {
  dragCounter--
  if (dragCounter <= 0) {
    dragOver.value = false
  }
}

function onDrop(e) {
  dragOver.value = false
  dragCounter = 0
  const file = e.dataTransfer?.files?.[0]
  if (file) processFile(file)
}

function onFileSelected(e) {
  const file = e.target?.files?.[0]
  if (file) processFile(file)
}

function triggerFileInput() {
  if (!uploading.value) {
    fileInputRef.value?.click()
  }
}

function removeCover() {
  previewUrl.value = ''
  uploadedFileId.value = ''
  emit('removed')
}

function reset() {
  removeCover()
}

defineExpose({ reset })
</script>

<style scoped>
.image-uploader {
  width: 320px;
  height: 180px;
  border: 2px dashed var(--bil-border, #ddd);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s, background 0.3s;
  overflow: hidden;
  position: relative;
}

.image-uploader:hover,
.image-uploader.is-dragover {
  border-color: var(--bil-primary, #00a1d6);
  background: rgba(0, 161, 214, 0.05);
}

.upload-placeholder {
  text-align: center;
  color: var(--bil-muted, #999);
}

.upload-placeholder p {
  margin: 4px 0 0;
  font-size: 13px;
}

.hint {
  font-size: 12px !important;
  color: #bbb;
}

.upload-icon {
  font-size: 32px;
  font-weight: 200;
  color: #ccc;
  line-height: 1;
}

.upload-progress {
  text-align: center;
  color: var(--bil-muted, #999);
  font-size: 14px;
}

.preview-area {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-area img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s;
}

.preview-mask:hover {
  opacity: 1;
}
</style>
