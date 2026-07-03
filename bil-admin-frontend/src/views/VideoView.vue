<template>
  <div class="video-page">
    <div class="page-toolbar">
      <el-select v-model="statusFilter" style="width:160px" @change="loadData">
        <el-option :value="0" label="待审核" />
        <el-option :value="1" label="已审核" />
        <el-option :value="2" label="已驳回" />
        <el-option :value="3" label="已隐藏" />
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索视频名称" clearable style="width:240px" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-table :data="videoList" stripe border style="width:100%" @row-click="onRowClick">
      <el-table-column prop="videoId" label="视频ID" width="140" />
      <el-table-column prop="videoName" label="标题" min-width="200">
        <template #default="{ row }">
          <div class="video-title-cell">
            <img v-if="row.videoCover" :src="row.videoCover" class="thumb" />
            <span>{{ row.videoName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="UP主" width="120" />
      <el-table-column prop="playCount" label="播放量" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已审核</el-tag>
          <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
          <el-tag v-else-if="row.status === 3" type="info">已隐藏</el-tag>
          <el-tag v-else>未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <div class="op-group">
            <el-button text type="primary" size="small" @click.stop="openDetail(row)">详情</el-button>
            <el-button text type="warning" size="small" @click.stop="openWatch(row)">观看</el-button>
            <template v-if="row.status === 0 || row.status === 2">
              <el-button text type="success" size="small" :loading="auditLoading[row.videoId]" @click.stop="handleAudit(row, 1)">通过</el-button>
              <el-button text type="warning" size="small" @click.stop="openReject(row)">驳回</el-button>
            </template>
            <el-button v-if="row.status === 1" text type="info" size="small" :loading="auditLoading[row.videoId]" @click.stop="handleAudit(row, 3)">隐藏</el-button>
            <el-button v-if="row.status === 3" text type="success" size="small" :loading="auditLoading[row.videoId]" @click.stop="handleAudit(row, 1)">显示</el-button>
            <el-button text size="small" :loading="recommendLoading[row.videoId]" @click.stop="handleRecommend(row)">
              {{ row.recommendType === 1 ? '取消推荐' : '推荐' }}
            </el-button>
            <el-popconfirm title="确定删除此视频？此操作不可恢复。" @confirm="handleDelete(row.videoId)">
              <template #reference>
                <el-button text type="danger" size="small" @click.stop>删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="totalCount > pageSize"
      v-model:current-page="pageNo"
      :page-size="pageSize"
      :total="totalCount"
      layout="prev, pager, next"
      style="margin-top: 16px; justify-content: flex-end"
      @current-change="onPageChange"
    />

    <!-- Watch video dialog -->
    <el-dialog v-model="watchVisible" title="观看视频" width="800px" :close-on-click-modal="false" @closed="stopWatch">
      <div v-if="watchVideo" style="background:#000;aspect-ratio:16/9;border-radius:8px;overflow:hidden">
        <video :src="watchSrc" controls autoplay style="width:100%;height:100%;object-fit:contain" />
      </div>
    </el-dialog>

    <!-- Audit confirm dialog -->
    <el-dialog v-model="auditVisible" :title="auditDialogTitle" width="480px" :close-on-click-modal="false">
      <div class="audit-info" v-if="auditTarget">
        <div class="audit-info-row"><span class="label">视频标题：</span><span>{{ auditTarget.videoName }}</span></div>
        <div class="audit-info-row"><span class="label">UP主：</span><span>{{ auditTarget.useName }}</span></div>
        <div class="audit-info-row"><span class="label">视频ID：</span><span>{{ auditTarget.videoId }}</span></div>
      </div>
      <el-input
        v-if="auditAction === 2"
        v-model="rejectReason"
        type="textarea"
        :rows="3"
        placeholder="填写驳回原因（选填）"
        style="margin-top: 16px"
      />
      <p v-if="auditAction !== 2" style="margin-top: 12px; color: var(--bil-muted, #999);">
        确认{{ auditAction === 1 ? '通过' : '隐藏' }}该视频吗？
      </p>
      <template #footer>
        <el-button @click="auditVisible = false" :disabled="auditing">取消</el-button>
        <el-button type="primary" :loading="auditing" :disabled="auditing" @click="confirmAudit">
          {{ auditing ? '处理中...' : '确认' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- Detail drawer -->
    <el-drawer v-model="detailVisible" title="视频详情" size="420px">
      <template v-if="detailVideo">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="视频ID">{{ detailVideo.videoId }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ detailVideo.videoName }}</el-descriptions-item>
          <el-descriptions-item label="UP主">{{ detailVideo.useName }}</el-descriptions-item>
          <el-descriptions-item label="播放量">{{ detailVideo.playCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="弹幕数">{{ detailVideo.danmuCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="点赞">{{ detailVideo.likeCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="投币">{{ detailVideo.coinCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="收藏">{{ detailVideo.collectCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="简介">{{ detailVideo.introduction || '无' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="detailVideo.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="detailVideo.status === 1" type="success">已审核</el-tag>
            <el-tag v-else-if="detailVideo.status === 2" type="danger">已驳回</el-tag>
            <el-tag v-else-if="detailVideo.status === 3" type="info">已隐藏</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { loadVideoListApi, auditVideoApi, deleteVideoApi, recommendVideoApi, loadVideoPListApi } from '@/api/modules/video'

const statusFilter = ref(0)
const searchKeyword = ref('')
const videoList = ref([])
const pageNo = ref(1)
const pageSize = ref(15)
const totalCount = ref(0)

const auditVisible = ref(false)
const auditTarget = ref(null)
const watchVisible = ref(false)
const watchVideo = ref(null)
const watchSrc = computed(() => {
  const v = watchVideo.value
  if (!v) return ''
  const fp = v.filePath || v.videoUrl
  if (!fp) return ''
  if (fp.startsWith('http://') || fp.startsWith('https://') || fp.startsWith('/')) return fp
  return '/' + fp.replace(/^[A-Za-z]:\\/, '').replace(/\\/g, '/').replace(/^.*?\/videos\//, 'videos/')
})

function openWatch(row) {
  watchVideo.value = row
  watchVisible.value = true
  loadWatchFile(row.videoId)
}

async function loadWatchFile(videoId) {
  try {
    const files = await loadVideoPListApi(videoId)
    if (Array.isArray(files) && files.length > 0) {
      const fp = files[0]?.filePath || ''
      watchVideo.value = { ...watchVideo.value, filePath: fp }
    }
  } catch {}
}

function stopWatch() {
  watchVideo.value = null
}

const auditAction = ref(0) // 1=通过, 2=驳回, 3=隐藏
const rejectReason = ref('')
const auditing = ref(false)
const auditDialogTitle = ref('')
const auditLoading = reactive({})
const recommendLoading = reactive({})

const detailVisible = ref(false)
const detailVideo = ref(null)

async function loadData() {
  try {
    const status = statusFilter.value === -1 ? '' : String(statusFilter.value)
    const result = await loadVideoListApi({
      status: status,
      pageNo: String(pageNo.value),
      videoNameFuzzy: searchKeyword.value
    })
    videoList.value = result?.list || []
    totalCount.value = result?.totalCount || 0
  } catch {
    videoList.value = []
    totalCount.value = 0
  }
}

function onPageChange() {
  loadData()
}

async function handleAudit(row, status) {
  // For reject, open the dialog instead
  if (status === 2) {
    openReject(row)
    return
  }

  auditTarget.value = row
  auditAction.value = status
  rejectReason.value = ''
  auditDialogTitle.value = status === 1 ? '审核通过' : '隐藏视频'
  auditVisible.value = true
}

async function confirmAudit() {
  auditing.value = true
  try {
    await auditVideoApi({
      videoId: auditTarget.value.videoId,
      status: String(auditAction.value),
      reason: rejectReason.value || ''
    })
    const actionLabels = { 1: '审核通过', 2: '已驳回', 3: '已隐藏' }
    ElMessage.success(actionLabels[auditAction.value] || '操作成功')
    auditVisible.value = false
    await loadData()
  } catch (err) {
    ElMessage.error(err?.message || '操作失败')
  } finally {
    auditing.value = false
  }
}

function openReject(row) {
  auditTarget.value = row
  auditAction.value = 2
  rejectReason.value = ''
  auditDialogTitle.value = '驳回视频'
  auditVisible.value = true
}

function openDetail(row) {
  detailVideo.value = row
  detailVisible.value = true
}

async function handleDelete(videoId) {
  try {
    await deleteVideoApi(videoId)
    ElMessage.success('已删除')
    await loadData()
  } catch (err) {
    ElMessage.error(err?.message || '删除失败')
  }
}

async function handleRecommend(row) {
  recommendLoading[row.videoId] = true
  try {
    await recommendVideoApi(row.videoId)
    ElMessage.success(row.recommendType === 1 ? '已取消推荐' : '已推荐')
    await loadData()
  } catch (err) {
    ElMessage.error(err?.message || '操作失败')
  } finally {
    recommendLoading[row.videoId] = false
  }
}

function onRowClick(row) {
  // Row click handled via stopPropagation on buttons
}

watch(pageNo, () => loadData())

onMounted(loadData)
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.video-title-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.thumb {
  width: 64px;
  height: 36px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.op-group {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-wrap: wrap;
}

.audit-info {
  padding: 12px;
  background: var(--el-fill-color-light, #f5f5f5);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.audit-info-row {
  font-size: 14px;
  color: var(--el-text-color-primary, #333);
  display: flex;
  gap: 4px;
}

.audit-info-row .label {
  color: var(--el-text-color-secondary, #999);
  flex-shrink: 0;
}
</style>
