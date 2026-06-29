<template>
  <div class="dashboard">
    <div class="stat-cards">
      <el-card v-for="card in statCards" :key="card.label" class="stat-card">
        <div class="stat-card-inner">
          <span class="stat-label">{{ card.label }}</span>
          <span class="stat-value">{{ card.value }}</span>
        </div>
      </el-card>
    </div>
    <el-card class="chart-card">
      <template #header>本周统计</template>
      <div class="chart-placeholder">
        <div v-for="(d, i) in weekLabels" :key="i" class="chart-bar-wrap">
          <div class="chart-bar" :style="{ height: Math.max(4, weekVideo[i]) * 3 + 'px' }" />
          <span class="chart-label">{{ d }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getActualTimeStatisticsInfoApi, getWeekStatisticsInfoApi } from '@/api/modules/index'

const statCards = ref([
  { label: '用户总数', value: 0 },
  { label: '视频总数', value: 0 },
  { label: '今日新增用户', value: 0 },
  { label: '今日新增视频', value: 0 }
])

const weekLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const weekVideo = ref([0, 0, 0, 0, 0, 0, 0])

async function loadStats() {
  try {
    const data = await getActualTimeStatisticsInfoApi()
    if (data) {
      statCards.value[0].value = data.totalUserCount || 0
      statCards.value[1].value = data.totalVideoCount || 0
      statCards.value[2].value = data.todayAddUserCount || 0
      statCards.value[3].value = data.todayAddVideoCount || 0
    }
  } catch {
    // ignore
  }
  try {
    const data = await getWeekStatisticsInfoApi()
    if (data?.weekVideoCounts) {
      weekVideo.value = data.weekVideoCounts
    }
  } catch {
    // ignore
  }
}

onMounted(loadStats)
</script>

<style scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card-inner {
  display: grid;
  gap: 8px;
}
.stat-label {
  color: var(--admin-text-secondary);
  font-size: 13px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
}
.chart-placeholder {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  height: 200px;
  padding: 8px 0;
}
.chart-bar-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.chart-bar {
  width: 100%;
  min-height: 4px;
  max-height: 180px;
  border-radius: 6px 6px 0 0;
  background: var(--admin-primary);
  transition: height 0.3s;
}
.chart-label {
  font-size: 12px;
  color: var(--admin-text-secondary);
}
</style>
