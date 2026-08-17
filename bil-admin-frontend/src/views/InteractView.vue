<template>
  <div class="interact-page">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="评论管理" name="comment">
        <div class="page-toolbar">
          <el-input v-model="commentKeyword" placeholder="搜索视频名称" clearable style="width:240px" @keyup.enter="loadComments" />
          <el-button type="primary" @click="loadComments">查询</el-button>
        </div>
        <el-table :data="commentList" stripe border style="width:100%">
          <el-table-column prop="commentId" label="ID" width="80" />
          <el-table-column prop="videoId" label="视频ID" width="140" />
          <el-table-column prop="videoName" label="视频名称" />
          <el-table-column prop="userName" label="用户" width="120" />
          <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-popconfirm title="确定删除此评论？" @confirm="handleDelComment(row.commentId)">
                <template #reference>
                  <el-button text type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="弹幕管理" name="danmu">
        <div class="page-toolbar">
          <el-input v-model="danmuKeyword" placeholder="搜索视频名称" clearable style="width:240px" @keyup.enter="loadDanmu" />
          <el-button type="primary" @click="loadDanmu">查询</el-button>
        </div>
        <el-table :data="danmuList" stripe border style="width:100%">
          <el-table-column prop="danmuId" label="ID" width="80" />
          <el-table-column prop="videoId" label="视频ID" width="140" />
          <el-table-column prop="videoName" label="视频名称" />
          <el-table-column label="用户" width="150">
            <template #default="{ row }">
              <div class="user-cell">
                <span class="user-name">{{ row.userName || row.userId }}</span>
                <span class="user-id">{{ row.userId }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="text" label="弹幕内容" min-width="180" show-overflow-tooltip />
          <el-table-column label="举报次数" width="90" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.reportCount > 0" type="danger" size="small">{{ row.reportCount }}</el-tag>
              <span v-else class="report-zero">0</span>
            </template>
          </el-table-column>
          <el-table-column label="颜色" width="80">
            <template #default="{ row }">
              <span :style="{ color: row.color || '#fff' }">●</span>
            </template>
          </el-table-column>
          <el-table-column prop="postTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-popconfirm title="确定删除此弹幕？" @confirm="handleDelDanmu(row.danmuId)">
                <template #reference>
                  <el-button text type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
              <el-popconfirm title="确定封禁该用户？封禁后无法登录" @confirm="handleBanUser(row.userId)">
                <template #reference>
                  <el-button text type="warning" size="small">封禁用户</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loadCommentApi, delCommentApi, loadDanmuApi, delDanmuApi } from '@/api/modules/interact'
import { changeStatusApi } from '@/api/modules/user'

const activeTab = ref('comment')
const commentList = ref([])
const commentKeyword = ref('')
const danmuList = ref([])
const danmuKeyword = ref('')

async function loadComments() {
  try {
    const data = await loadCommentApi('1', commentKeyword.value)
    commentList.value = Array.isArray(data) ? data : []
  } catch {
    commentList.value = []
  }
}

async function loadDanmu() {
  try {
    const data = await loadDanmuApi('1', danmuKeyword.value)
    danmuList.value = Array.isArray(data) ? data : []
  } catch {
    danmuList.value = []
  }
}

async function handleDelComment(commentId) {
  await delCommentApi(String(commentId))
  ElMessage.success('已删除')
  await loadComments()
}

async function handleDelDanmu(danmuId) {
  await delDanmuApi(String(danmuId))
  ElMessage.success('已删除')
  await loadDanmu()
}

async function handleBanUser(userId) {
  try {
    await changeStatusApi(userId, 0)
    ElMessage.success('已封禁该用户')
  } catch {
    ElMessage.error('封禁失败，请稍后再试')
  }
}

function onTabChange(name) {
  if (name === 'comment') loadComments()
  else loadDanmu()
}

onMounted(loadComments)
</script>

<style scoped>
.page-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}

.user-cell {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.user-name {
  color: #333;
  font-weight: 500;
}

.user-id {
  color: #999;
  font-size: 12px;
}

.report-zero {
  color: #bbb;
}
</style>
