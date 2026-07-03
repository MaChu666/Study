<template>
  <el-dialog v-model="visible" :title="title" width="560px" :close-on-click-modal="false" @closed="reset">
    <div v-if="!imageUrl" class="upload-placeholder" @click="triggerUpload">
      <el-icon :size="48" color="#999"><UploadFilled /></el-icon>
      <p>点击选择图片</p>
      <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="onFilePicked" />
    </div>
    <template v-else>
      <div class="cropper-area">
        <vue-cropper
          ref="cropperRef"
          :img="imageUrl"
          :autoCrop="true"
          :autoCropWidth="cropWidth"
          :autoCropHeight="cropHeight"
          :fixed="true"
          :fixedNumber="fixedNumber"
          :centerBox="true"
          :info="true"
          :full="true"
          :canScale="true"
          :canMove="true"
          :canMoveBox="false"
          :mode="'contain'"
          outputType="jpeg"
          outputSize="0.8"
          @realTime="onRealTime"
          @imgLoad="onImgLoad"
        />
        <div v-if="shape === 'circle'" class="circle-mask" />
      </div>
      <div class="cropper-footer">
        <el-button size="small" @click="triggerUpload">重新选择</el-button>
      </div>
    </template>
    <div v-if="imageUrl" class="preview-row">
      <span class="preview-label">预览</span>
      <div class="preview-box" :class="{ 'preview-circle': shape === 'circle', 'preview-rect': shape === 'rectangle' }" :style="{ backgroundImage: previewUrl ? 'url(' + previewUrl + ')' : '' }" />
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="uploading" :disabled="!previewUrl" @click="confirmUpload">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import request from '@/api/request'

const props = defineProps({
  modelValue: Boolean,
  shape: { type: String, default: 'circle' },
  aspectRatio: { type: Array, default: () => [1, 1] }
})
const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const cropperRef = ref()
const fileInput = ref()
const imageUrl = ref('')
const previewUrl = ref('')
const uploading = ref(false)

const title = computed(() => props.shape === 'circle' ? '裁剪头像' : '裁剪封面')
const fixedNumber = computed(() => props.aspectRatio)
const cropWidth = computed(() => props.shape === 'circle' ? 200 : 320)
const cropHeight = computed(() => props.shape === 'circle' ? 200 : 180)

watch(() => props.modelValue, (val) => { visible.value = val }, { immediate: true })
watch(visible, (val) => { if (!val) emit('update:modelValue', false) })

function triggerUpload() {
  fileInput.value?.click()
}

function onFilePicked(e) {
  const file = e.target.files?.[0]
  if (!file) return
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error("图片不能超过10MB")
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    imageUrl.value = ev.target.result
    previewUrl.value = ''
  }
  reader.readAsDataURL(file)
}

function onImgLoad() {
  nextTick(() => updatePreview())
}

function onRealTime() {
  updatePreview()
}

function updatePreview() {
  if (!cropperRef.value) return
  cropperRef.value.getCropData((data) => {
    previewUrl.value = data
  })
}

async function confirmUpload() {
  if (!previewUrl.value) {
    ElMessage.warning('裁剪数据为空')
    return
  }
  uploading.value = true
  try {
    const data = await request({
      url: '/file/uploadImage',
      method: 'post',
      data: {
        file: previewUrl.value,
        createThumbnail: 'false'
      },
      timeout: 120000
    })
    if (data) {
      const resultPath = data.filePath || data.fileId || ''
      if (!resultPath) {
        ElMessage.error('上传返回数据异常')
        return
      }
      emit('success', resultPath)
      ElMessage.success(props.shape === 'circle' ? '头像上传成功' : '封面上传成功')
      visible.value = false
    } else {
      ElMessage.error('上传失败：服务器未返回数据')
    }
  } catch (err) {
    ElMessage.error(err?.message || err?.info || '上传失败')
  } finally {
    uploading.value = false
  }
}

function reset() {
  imageUrl.value = ''
  previewUrl.value = ''
}

// Also reset when dialog opens
watch(visible, (val) => {
  if (val) {
    imageUrl.value = ''
    previewUrl.value = ''
  }
})
</script>

<style scoped>
.upload-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 12px;
  color: #999;
  background: #f5f5f5;
  border-radius: 8px;
  cursor: pointer;
}
.upload-placeholder p { margin: 0; font-size: 14px; }
.cropper-area {
  position: relative;
  width: 100%;
  height: 400px;
}
.cropper-area :deep(.vue-cropper) {
  height: 400px;
  background: #808080;
  border-radius: 8px;
}
.circle-mask {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  pointer-events: none;
  background: radial-gradient(circle at 50% 50%,
    transparent 98px,
    rgba(0,0,0,0.6) 99px,
    rgba(0,0,0,0.6) 100%
  );
}
.cropper-footer {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}
.preview-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0 0;
}
.preview-label { font-size: 13px; color: #999; }
.preview-box {
  background: #e0e0e0 center/cover;
  border: 2px solid #ddd;
  flex-shrink: 0;
}
.preview-circle {
  width: 64px; height: 64px; border-radius: 50%;
}
.preview-rect {
  width: 128px; height: 72px; border-radius: 4px;
}
/* Hide crop box corner handles - box is fixed size */
.cropper-area :deep(.cropper-point) {
  opacity: 0 !important;
  pointer-events: none !important;
}
.cropper-area :deep(.cropper-line) {
  opacity: 0 !important;
}
</style>