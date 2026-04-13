<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView v-slot="{ Component, route }">
    <component :is="Component" :key="route.path" />
  </RouterView>
</template>

<style>
/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease-out, transform 0.3s ease-out;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
