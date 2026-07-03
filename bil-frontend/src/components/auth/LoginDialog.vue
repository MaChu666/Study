<template>
  <el-dialog
    v-model="userStore.loginDialogVisible"
    width="420px"
    class="login-dialog"
    :show-close="true"
    :close-on-click-modal="false"
    @closed="onClosed"
  >
    <template #header>
      <div class="dialog-tabs">
        <button
          class="tab-btn"
          :class="{ active: mode === 'login' }"
          @click="switchToLogin"
        >登录</button>
        <button
          class="tab-btn"
          :class="{ active: mode === 'register' }"
          @click="switchToRegister"
        >注册</button>
      </div>
    </template>

    <!-- Login form -->
    <el-form
      v-show="mode === 'login'"
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      label-position="top"
      @submit.prevent
    >
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="loginForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="loginForm.password"
          :type="loginPwdVisible ? 'text' : 'password'"
          placeholder="请输入密码"
        >
          <template #suffix>
            <button type="button" class="pwd-toggle" @click="loginPwdVisible = !loginPwdVisible" tabindex="-1">
              <svg v-if="loginPwdVisible" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg><svg v-else viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            </button>
          </template>
        </el-input>
      </el-form-item>
      <div class="extra-row">
        <el-checkbox v-model="loginForm.rememberMe" size="small">记住我</el-checkbox>
        <a class="forgot-link" href="javascript:void(0)">忘记密码</a>
      </div>
      <el-form-item label="验证码" prop="checkCode">
        <div class="captcha-row">
          <el-input v-model="loginForm.checkCode" placeholder="验证码" />
          <button type="button" class="captcha-btn" @click="loadCaptcha">
            <img v-if="captcha.checkCode" :src="captcha.checkCode" alt="验证码" />
            <span v-else>刷新</span>
          </button>
        </div>
      </el-form-item>
      <el-button
        class="bil-gradient-button submit"
        :loading="submitting"
        :disabled="submitting"
        @click="submitLogin"
      >{{ submitting ? '登录中...' : '登录' }}</el-button>
    </el-form>

    <!-- Register form -->
    <el-form
      v-show="mode === 'register'"
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      label-position="top"
      @submit.prevent
    >
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="昵称" prop="useName">
        <el-input v-model="registerForm.useName" placeholder="给自己取个名字" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="registerForm.password"
          :type="regPwdVisible ? 'text' : 'password'"
          placeholder="请输入密码"
        >
          <template #suffix>
            <button type="button" class="pwd-toggle" @click="regPwdVisible = !regPwdVisible" tabindex="-1">
              {{ regPwdVisible ? '🙈' : '👁' }}
            </button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          :type="regPwdVisible ? 'text' : 'password'"
          placeholder="请再次输入密码"
        />
      </el-form-item>
      <el-form-item label="验证码" prop="checkCode">
        <div class="captcha-row">
          <el-input v-model="registerForm.checkCode" placeholder="验证码" />
          <button type="button" class="captcha-btn" @click="loadCaptcha">
            <img v-if="captcha.checkCode" :src="captcha.checkCode" alt="验证码" />
            <span v-else>刷新</span>
          </button>
        </div>
      </el-form-item>
      <el-button
        class="bil-gradient-button submit"
        :loading="submitting"
        :disabled="submitting"
        @click="submitRegister"
      >{{ submitting ? '注册中...' : '注册' }}</el-button>
    </el-form>

    <!-- Success banner after register -->
    <el-alert
      v-if="registerSuccess"
      title="注册成功，请登录"
      type="success"
      :closable="true"
      show-icon
      style="margin-bottom: 12px"
    />
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
const submitting = ref(false)
const registerSuccess = ref(false)
const loginPwdVisible = ref(false)
const regPwdVisible = ref(false)
const loginFormRef = ref(null)
const registerFormRef = ref(null)

const captcha = reactive({ checkCode: '', checkCodeKey: '' })
const loginForm = reactive({ email: '', password: '', checkCode: '', checkCodeKey: '', rememberMe: false })
const registerForm = reactive({ email: '', useName: '', password: '', confirmPassword: '', checkCode: '', checkCodeKey: '' })

const loginRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  checkCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const registerRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  useName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
    { max: 20, message: '密码最多20位', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!/[a-zA-Z]/.test(value)) callback(new Error('密码需包含字母'))
        else if (!/[0-9]/.test(value)) callback(new Error('密码需包含数字'))
        else callback()
      }, trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  checkCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

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

function switchToLogin() {
  mode.value = 'login'
  registerSuccess.value = false
  loginFormRef.value?.clearValidate()
}

function switchToRegister() {
  mode.value = 'register'
  registerSuccess.value = false
  registerFormRef.value?.clearValidate()
}

function onClosed() {
  loginFormRef.value?.resetFields()
  registerFormRef.value?.resetFields()
  registerSuccess.value = false
  // Reapply captcha key after reset
  loginForm.checkCodeKey = captcha.checkCodeKey
  registerForm.checkCodeKey = captcha.checkCodeKey
}

async function submitLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      email: loginForm.email,
      password: md5(loginForm.password).toString(),
      checkCode: loginForm.checkCode,
      checkCodeKey: loginForm.checkCodeKey,
      rememberMe: loginForm.rememberMe
    }
    await userStore.login(payload)
    loginForm.checkCode = ''
    ElMessage.success('欢迎回来')
  } catch {
    // Error already handled by request interceptor
    await loadCaptcha()
    loginForm.checkCode = ''
  } finally {
    submitting.value = false
  }
}

async function submitRegister() {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      email: registerForm.email,
      useName: registerForm.useName,
      registerPassword: registerForm.password,
      checkCode: registerForm.checkCode,
      checkCodeKey: registerForm.checkCodeKey
    }
    await userStore.register(payload)
    registerSuccess.value = true
    registerForm.checkCode = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    loginForm.checkCode = ''
    mode.value = 'login'
    await loadCaptcha()
  } catch {
    // Error already handled by request interceptor
    await loadCaptcha()
    registerForm.checkCode = ''
  } finally {
    submitting.value = false
  }
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
    if (!visible) {
      registerSuccess.value = false
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
.dialog-tabs {
  display: flex;
  gap: 24px;
  justify-content: center;
  width: 100%;
}

.tab-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: var(--bil-muted, #999);
  cursor: pointer;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;
}

.tab-btn.active {
  color: var(--bil-primary, #00a1d6);
  border-bottom-color: var(--bil-primary, #00a1d6);
  font-weight: 600;
}

.tab-btn:hover {
  color: var(--bil-primary, #00a1d6);
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 108px;
  gap: 10px;
}

.captcha-btn {
  border: 1px solid var(--bil-border, #e8e8e8);
  border-radius: 8px;
  background: var(--bil-surface, #fff);
  overflow: hidden;
  cursor: pointer;
  padding: 0;
}

.captcha-btn img {
  display: block;
  width: 100%;
  height: 36px;
  object-fit: cover;
}

.submit {
  width: 100%;
}

.extra-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  font-size: 13px;
}

.forgot-link {
  color: var(--bil-primary, #00a1d6);
  text-decoration: none;
  font-size: 13px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.pwd-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  display: flex;
  align-items: center;
  color: var(--bil-muted);
}
.pwd-toggle:hover {
  color: var(--bil-text);
}

:deep(.el-dialog) {
  border-radius: 14px;
}

:deep(.el-dialog__header) {
  padding-bottom: 0;
}
</style>
