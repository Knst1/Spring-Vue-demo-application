<template>
  <NavMenu />
  <router-view />
</template>

<script lang="ts" setup>
  import NavMenu from '@/components/NavMenu.vue'
</script>

<style scoped>
  NavMenu {
    margin-bottom: 20px;
  }
</style>
