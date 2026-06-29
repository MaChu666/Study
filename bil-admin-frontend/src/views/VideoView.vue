<template>
  <div class="video-page">
    <div class="page-toolbar">
      <el-select v-model="statusFilter" style="width:160px" @change="loadData">
        <el-option :value="0" label="待审核" />
        <el-option :value="1" label="已审核" />
        <el-option :value="2" label="已驳回" />
        <el-option :value="3" label="已隐藏" />
        <el-option :value="-1" label="全部" />
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
      <el-table-column prop="useName" label="UP主" width="120" />
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
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0 || row.status === 2">
            <el-popconfirm title="确认审核通过？" @confirm="handleAudit(row.videoId, 1, '')">
              <template #reference>
                <el-button text type="success" size="small">通过</el-button>
              </template>
            </el-popconfirm>
            <el-button text type="warning" size="small" @click="openReject(row)">驳回</el-button>
          </template>
          <el-button v-if="row.status === 1" text type="info" size="small" @click="handleAudit(row.videoId, 3, '')">隐藏</el-button>
          <el-button v-if="row.status === 3" text type="success" size="small" @click="handleAudit(row.videoId, 1, '')">显示</el-button>
          <el-button text size="small" @click="handleRecommend(row.videoId)">{{ row.recommendType === 1 ? '取消推荐' : '推荐' }}</el-button>
          <el-popconfirm title="确定删除此视频？此操作不可恢复。" @confirm="handleDelete(row.videoId)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
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
    />

    <!-- Reject dialog -->
    <el-dialog v-model="rejectVisible" title="驳回视频" width="440px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="填写驳回原因（选填）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认驳回</el-button>
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
        </el-descriptions>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { loadVideoListApi, auditVideoApi, deleteVideoApi, recommendVideoApi } from '@/api/modules/video'

const statusFilter = ref(0)
const searchKeyword = ref('')
const videoList = ref([])
const pageNo = ref(1)
const pageSize = ref(15)
const totalCount = ref(0)
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectVideoId = ref('')
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

async function handleAudit(videoId, status, reason) {
  await auditVideoApi({ videoId, status: String(status), reason: reason || '' })
  ElMessage.success('操作成功')
  await loadData()
}

function openReject(row) {
  rejectVideoId.value = row.videoId
  rejectReason.value = ''
  rejectVisible.value = true
}

async function confirmReject() {
  await handleAudit(rejectVideoId.value, 2, rejectReason.value)
  rejectVisible.value = false
}

async function handleDelete(videoId) {
  await deleteVideoApi(videoId)
  ElMessage.success('已删除')
  await loadData()
}

async function handleRecommend(videoId) {
  await recommendVideoApi(videoId)
  ElMessage.success('操作成功')
  await loadData()
}

function onRowClick(row) {
  detailVideo.value = row
  detailVisible.value = true
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
</style>
