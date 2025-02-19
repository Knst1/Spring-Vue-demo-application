<template>
  <a-menu
    class="mb20"
    v-model:selectedKeys="current"
    mode="horizontal"
    :items="items"
    @click="handleClick"
  />
</template>

<script lang="ts" setup>
  import { h, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { SolutionOutlined, ApartmentOutlined, TeamOutlined } from '@ant-design/icons-vue'
  import type { MenuProps } from 'ant-design-vue'

  const router = useRouter()
  const current = ref<string[]>([])
  const items: MenuProps['items'] = [
    { key: 'profession', icon: () => h(SolutionOutlined), label: 'Профессии' },
    { key: 'department', icon: () => h(ApartmentOutlined), label: 'Отделы' },
    { key: 'employee', icon: () => h(TeamOutlined), label: 'Сотрудники' },
  ]

  const handleClick = (e: { key: string }) => {
    router.push(`/${e.key}`)
  }
</script>

<style scoped>
  .mb20 {
    margin-bottom: 20px;
  }
</style>
