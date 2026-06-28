<template>
  <el-dialog
    v-model="userStore.loginDialogVisible"
    width="420px"
    class="login-dialog"
    title="登录 BilBil"
  >
    <el-tabs v-model="mode">
      <el-tab-pane label="登录" name="login">
        <el-form :model="loginForm" label-position="top">
          <el-form-item label="邮箱">
            <el-input v-model="loginForm.email" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" show-password />
          </el-form-item>
          <el-form-item label="验证码">
            <div class="captcha-row">
              <el-input v-model="loginForm.checkCode" />
              <button type="button" @click="loadCaptcha">
                <img v-if="captcha.checkCode" :src="captcha.checkCode" alt="验证码" />
                <span v-else>刷新</span>
              </button>
            </div>
          </el-form-item>
          <el-button class="bil-gradient-button submit" @click="submitLogin">登录</el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="注册" name="register">
        <el-form :model="registerForm" label-position="top">
          <el-form-item label="邮箱">
            <el-input v-model="registerForm.email" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="registerForm.useName" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="registerForm.registerPassword" show-password />
          </el-form-item>
          <el-form-item label="验证码">
            <div class="captcha-row">
              <el-input v-model="registerForm.checkCode" />
              <button type="button" @click="loadCaptcha">
                <img v-if="captcha.checkCode" :src="captcha.checkCode" alt="验证码" />
                <span v-else>刷新</span>
              </button>
            </div>
          </el-form-item>
          <el-button class="bil-gradient-button submit" @click="submitRegister">注册</el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckCodeApi } from '@/api/modules/account'
import { useUserStore } from '@/stores/user'
import { eventBus } from '@/utils/eventBus'
import md5 from 'crypto-js/md5'

const userStore = useUserStore()
const mode = ref('login')
const captcha = reactive({ checkCode: '', checkCodeKey: '' })
const loginForm = reactive({ email: '', password: '', checkCode: '', checkCodeKey: '' })
const registerForm = reactive({ email: '', useName: '', registerPassword: '', checkCode: '', checkCodeKey: '' })

async function loadCaptcha() {
  try {
    const data = await getCheckCodeApi()
    captcha.checkCode = data?.checkCode || ''
    captcha.checkCodeKey = data?.checkCodeKey || ''
    loginForm.checkCodeKey = captcha.checkCodeKey
    registerForm.checkCodeKey = captcha.checkCodeKey
  } catch {
    captcha.checkCode = ''
    captcha.checkCodeKey = ''
    loginForm.checkCodeKey = ''
    registerForm.checkCodeKey = ''
    ElMessage.error('验证码加载失败，请稍后重试')
  }
}

async function submitLogin() {
  const payload = {
    ...loginForm,
    password: md5(loginForm.password).toString()
  }
  await userStore.login(payload)
  loginForm.checkCode = ''
  ElMessage.success('欢迎回来')
}

async function submitRegister() {
  await userStore.register(registerForm)
  ElMessage.success('注册成功，请登录')
  mode.value = 'login'
  registerForm.checkCode = ''
  loginForm.checkCode = ''
  await loadCaptcha()
}

function openDialog() {
  userStore.openLoginDialog()
}

watch(
  () => userStore.loginDialogVisible,
  async (visible) => {
    if (visible && !captcha.checkCode) {
      await loadCaptcha()
    }
  }
)

onMounted(() => {
  eventBus.on('auth:required', openDialog)
})

onUnmounted(() => {
  eventBus.off('auth:required', openDialog)
})
</script>

<style scoped>
.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 108px;
  gap: 10px;
}

.captcha-row button {
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  background: var(--bil-surface);
  overflow: hidden;
  cursor: pointer;
}

.captcha-row img {
  display: block;
  width: 100%;
  height: 36px;
  object-fit: cover;
}

.submit {
  width: 100%;
}

:deep(.el-dialog) {
  border-radius: 14px;
}
</style>
