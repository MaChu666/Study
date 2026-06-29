<template>
  <div class="login-page">
    <el-card class="login-card">
      <h1>BilBil 管理后台</h1>
      <el-form :model="form" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="form.account" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="submitLogin" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model="form.checkCode" placeholder="验证码" />
            <button type="button" class="captcha-btn" @click="loadCaptcha">
              <img v-if="captchaImg" :src="captchaImg" alt="验证码" />
              <span v-else>点击获取</span>
            </button>
          </div>
        </el-form-item>
        <el-button type="primary" :loading="loading" class="submit-btn" @click="submitLogin">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import md5 from 'crypto-js/md5'
import { getCheckCodeApi, loginApi } from '@/api/modules/account'
import { setToken, setProfile } from '@/stores/auth'

const router = useRouter()
const loading = ref(false)
const captchaImg = ref('')
const checkCodeKey = ref('')
const form = reactive({ account: '', password: '', checkCode: '' })

async function loadCaptcha() {
  try {
    const data = await getCheckCodeApi()
    captchaImg.value = data?.checkCode || ''
    checkCodeKey.value = data?.checkCodeKey || ''
  } catch {
    captchaImg.value = ''
  }
}

async function submitLogin() {
  loading.value = true
  try {
    const profile = await loginApi({
      account: form.account,
      password: md5(form.password).toString(),
      checkCodeKey: checkCodeKey.value,
      checkCode: form.checkCode
    })
    setToken(profile?.token || '')
    setProfile(profile)
    ElMessage.success('登录成功')
    router.push({ name: 'dashboard' })
  } catch {
    loadCaptcha()
    form.checkCode = ''
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--admin-bg);
}
.login-card {
  width: 400px;
}
.login-card h1 {
  margin: 0 0 24px;
  text-align: center;
  font-size: 22px;
}
.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 10px;
}
.captcha-btn {
  border: 1px solid var(--admin-border);
  border-radius: 6px;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
  padding: 0;
}
.captcha-btn img {
  display: block;
  width: 100%;
  height: 32px;
  object-fit: cover;
}
.submit-btn {
  width: 100%;
}
</style>
