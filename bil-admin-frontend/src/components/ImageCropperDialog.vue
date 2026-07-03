<template>
  <el-dialog v-model="visible" title="裁剪图标" width="560px" :close-on-click-modal="false" @closed="reset">
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
          :autoCropWidth="200"
          :autoCropHeight="200"
          :fixed="true"
          :fixedNumber="[1, 1]"
          :centerBox="true"
          :info="true"
          :full="true"
          :canScale="true"
          :canMoveBox="false"
          outputType="png"
          outputSize="1"
          @realTime="onRealTime"
          @imgLoad="onImgLoad"
        />
        <div class="circle-mask" />
      </div>
      <div class="cropper-footer">
        <el-button size="small" @click="triggerUpload">重新选择</el-button>
      </div>
    </template>
    <div v-if="imageUrl" class="preview-row">
      <span class="preview-label">预览</span>
      <div class="preview-circle" :style="{ backgroundImage: previewUrl ? 'url(' + previewUrl + ')' : '' }" />
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="uploading" :disabled="!previewUrl" @click="confirmUpload">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import request from '@/api/request'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const cropperRef = ref()
const fileInput = ref()
const imageUrl = ref('')
const previewUrl = ref('')
const uploading = ref(false)

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { if (!val) emit('update:modelValue', false) })

function triggerUpload() {
  fileInput.value?.click()
}

function onFilePicked(e) {
  const file = e.target.files?.[0]
  if (!file) return
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
      data: { file: previewUrl.value, createThumbnail: 'false' }
    })
    if (data) {
      emit('success', data.filePath || '')
      ElMessage.success('图标上传成功')
      visible.value = false
    }
  } catch (err) {
    ElMessage.error(err?.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

function reset() {
  imageUrl.value = ''
  previewUrl.value = ''
}
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
.upload-placeholder p {
  margin: 0;
  font-size: 14px;
}
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
.preview-label {
  font-size: 13px;
  color: #999;
}
.preview-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #e0e0e0 center/cover;
  border: 2px solid #ddd;
  flex-shrink: 0;
}
</style>
