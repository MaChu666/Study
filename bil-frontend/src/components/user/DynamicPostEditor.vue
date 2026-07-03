<template>
  <div class="dp-editor">
    <div class="dp-header">
      <UserBadge
        :user-id="userStore.profile?.userId"
        :user-name="userStore.profile?.useName"
        :avatar="userStore.profile?.avatar"
        size="md"
        :clickable="false"
      />
    </div>
    <textarea
      v-model="content"
      class="dp-textarea"
      placeholder="分享你的想法..."
      rows="3"
      maxlength="2000"
    />
    <div class="dp-actions">
      <label class="dp-image-btn" title="上传图片">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
        <input type="file" accept="image/*" hidden multiple @change="onImagesSelected" />
      </label>
      <span class="dp-char-count">{{ content.length }}/2000</span>
      <button class="dp-submit" type="button" :disabled="!content.trim() || submitting" @click="submitPost">
        {{ submitting ? '发布中...' : '发布' }}
      </button>
    </div>
    <!-- Image previews -->
    <div v-if="imageUrls.length" class="dp-image-previews">
      <div v-for="(url, idx) in imageUrls" :key="idx" class="dp-preview-item" @click="removeImage(idx)">
        <img :src="url" alt="" />
        <div class="dp-preview-remove">&times;</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import UserBadge from '@/components/user/UserBadge.vue'
import { postDynamicApi } from '@/api/modules/user'
import { uploadImageApi } from '@/api/modules/file'

const emit = defineEmits(['posted'])
const userStore = useUserStore()

const content = ref('')
const submitting = ref(false)
const imageUrls = ref([])
const imageFileIds = ref([])

async function onImagesSelected(e) {
  const files = Array.from(e.target?.files || [])
  if (!files.length) return
  for (const file of files) {
    if (!file.type.startsWith('image/')) continue
    try {
      const reader = new FileReader()
      const base64 = await new Promise((resolve) => {
        reader.onload = (ev) => resolve(ev.target.result)
        reader.readAsDataURL(file)
      })
      const result = await uploadImageApi({ file: base64, createThumbnail: 'false' })
      const fid = result?.filePath || result?.fileId
      if (fid) {
        imageUrls.value.push(base64)
        imageFileIds.value.push(fid)
      }
    } catch {
      ElMessage.warning('图片上传失败')
    }
  }
  e.target.value = ''
}

function removeImage(idx) {
  imageUrls.value.splice(idx, 1)
  imageFileIds.value.splice(idx, 1)
}

async function submitPost() {
  const text = content.value.trim()
  if (!text || submitting.value) return
  submitting.value = true
  try {
    await postDynamicApi({
      content: text,
      dynamicType: 1,
      images: imageFileIds.value.length ? imageFileIds.value.join(',') : undefined
    })
    ElMessage.success('动态已发布')
    content.value = ''
    imageUrls.value = []
    imageFileIds.value = []
    emit('posted')
  } catch {
    ElMessage.error('发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.dp-editor {
  padding: 16px; background: var(--bil-surface);
  border-radius: 12px; box-shadow: var(--bil-shadow); margin-bottom: 20px;
}
.dp-header { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.dp-textarea {
  width: 100%; padding: 10px 12px; border: 1px solid var(--bil-border);
  border-radius: 8px; background: var(--bil-hover); color: var(--bil-text);
  font-size: 13px; resize: vertical; outline: none; font-family: inherit;
  box-sizing: border-box;
}
.dp-textarea:focus { border-color: var(--bil-primary); }
.dp-actions {
  display: flex; align-items: center; gap: 10px; margin-top: 10px;
}
.dp-image-btn {
  cursor: pointer; color: var(--bil-muted); display: flex; align-items: center;
  transition: color 0.2s;
}
.dp-image-btn:hover { color: var(--bil-primary); }
.dp-char-count { flex: 1; font-size: 11px; color: var(--bil-muted); }
.dp-submit {
  padding: 6px 18px; border: 0; border-radius: 6px;
  background: var(--bil-primary); color: #fff; font-size: 13px; cursor: pointer;
  transition: opacity 0.2s;
}
.dp-submit:hover:not(:disabled) { opacity: 0.85; }
.dp-submit:disabled { opacity: 0.5; cursor: not-allowed; }
.dp-image-previews { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 10px; }
.dp-preview-item {
  width: 80px; height: 80px; border-radius: 6px; overflow: hidden;
  position: relative; cursor: pointer;
}
.dp-preview-item img { width: 100%; height: 100%; object-fit: cover; }
.dp-preview-remove {
  position: absolute; top: 2px; right: 2px;
  width: 20px; height: 20px; border-radius: 50%;
  background: rgba(0,0,0,0.6); color: #fff; font-size: 14px;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.15s;
}
.dp-preview-item:hover .dp-preview-remove { opacity: 1; }
</style>
