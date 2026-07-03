<template>
  <div
    class="user-badge"
    :class="['ub-' + size, { 'ub-clickable': clickable }]"
    @click.stop="handleClick"
  >
    <div class="ub-avatar">
      <img v-if="resolvedAvatar" :src="resolvedAvatar" :alt="resolvedName" />
      <span v-else>{{ initial }}</span>
    </div>
    <span v-if="showName" class="ub-name">{{ resolvedName || userId || '未知用户' }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  userId: { type: String, default: '' },
  userName: { type: String, default: '' },
  avatar: { type: String, default: '' },
  size: { type: String, default: 'md' },
  showName: { type: Boolean, default: true },
  clickable: { type: Boolean, default: true }
})

const emit = defineEmits(['navigate'])

const router = useRouter()

const resolvedName = computed(() => props.userName || '')
const resolvedAvatar = computed(() => props.avatar || '')

const initial = computed(() => {
  const name = resolvedName.value || props.userId || 'U'
  return (name[0] || 'U').toUpperCase()
})

function handleClick() {
  if (!props.clickable || !props.userId) return
  emit('navigate', props.userId)
  router.push({ name: 'user-home', params: { userId: props.userId } })
}
</script>

<style scoped>
.user-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  vertical-align: middle;
}

.ub-clickable {
  cursor: pointer;
}

.ub-clickable:hover .ub-name {
  color: var(--bil-primary);
}

.ub-avatar {
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  background: var(--bil-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
}

.ub-sm .ub-avatar { width: 24px; height: 24px; font-size: 12px; }
.ub-md .ub-avatar { width: 36px; height: 36px; font-size: 14px; }
.ub-lg .ub-avatar { width: 48px; height: 48px; font-size: 18px; }

.ub-sm .ub-name { font-size: 12px; }
.ub-md .ub-name { font-size: 14px; }
.ub-lg .ub-name { font-size: 16px; }

.ub-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ub-name {
  font-weight: 500;
  color: var(--bil-text);
  transition: color 0.15s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}
</style>
